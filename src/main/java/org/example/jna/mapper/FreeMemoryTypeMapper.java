package org.example.jna.mapper;

import com.sun.jna.FromNativeContext;
import com.sun.jna.FromNativeConverter;
import com.sun.jna.Function;
import com.sun.jna.InvocationMapper;
import com.sun.jna.NativeLibrary;
import com.sun.jna.Pointer;
import com.sun.jna.ToNativeConverter;
import com.sun.jna.TypeMapper;
import com.sun.jna.WString;
import org.example.utils.StdLib;
import org.example.jna.gotype.GoString;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * String cleanup processor
 * <p>
 * This processor must be woven into the invoked methods via dynamic proxy
 * in order to automatically reclaim the corresponding memory allocated in C.
 * <p>
 * Example:
 * <pre>
 * AwesomeInterface awesome = NativeProxy.load(args[0], AwesomeInterface.class);
 *
 * String value = awesome.echoString(new GoString(json));
 * System.out.println("awesome.echoString :" + value);
 *
 * WString echo2 = awesome.echoWString(new GoString(json));
 * System.out.println("awesome.echoWString :" + echo2.toString());
 *
 * GoString echo = awesome.echoGoString(new GoString(json));
 * System.out.println("awesome.echoGoString :" + echo.value);
 * </pre>
 *
 * @author 唐家林 on 2021-01-28.
 */
public class FreeMemoryTypeMapper implements TypeMapper, InvocationMapper {
    private static final Map<Class<?>, java.util.function.Function<Pointer, ?>> VALUE_CONVER_FUNCTION_MAP = new HashMap<>();

    static {
        StringConvertHandler stringConvertHandler = new StringConvertHandler();
        StringArrayConvertHandler stringArrayConvertHandler = new StringArrayConvertHandler();

        VALUE_CONVER_FUNCTION_MAP.put(String.class, stringConvertHandler);
        VALUE_CONVER_FUNCTION_MAP.put(WString.class, new WStringConvertHandler(stringConvertHandler));
        VALUE_CONVER_FUNCTION_MAP.put(GoString.ByReference.class, new GoStringConvertHandler(stringConvertHandler));
        VALUE_CONVER_FUNCTION_MAP.put(String[].class, stringArrayConvertHandler);
        VALUE_CONVER_FUNCTION_MAP.put(WString[].class, new WStringArrayConvertHandler(stringArrayConvertHandler));
    }

    @Override
    public FromNativeConverter getFromNativeConverter(Class<?> javaType) {
        java.util.function.Function<Pointer, ?> function = VALUE_CONVER_FUNCTION_MAP.get(javaType);
        return function == null ? null : new FreeMemoryFromNativeConverter(function);
    }

    @Override
    public ToNativeConverter getToNativeConverter(Class<?> javaType) {
        return null;
    }

    @Override
    public InvocationHandler getInvocationHandler(NativeLibrary lib, Method m) {
        Map<Class<?>, FromNativeConverter> fromNativeConverterMap = new HashMap<>();
        for (Map.Entry<Class<?>, java.util.function.Function<Pointer, ?>> entry : VALUE_CONVER_FUNCTION_MAP.entrySet()) {
            fromNativeConverterMap.put(entry.getKey(), new FreeMemoryFromNativeConverter(entry.getValue()));
        }
        return new FreeMemoryInvocationHandler(fromNativeConverterMap, lib.getFunction(m.getName()));
    }

    /**
     * Free memory regions that Java cannot manage after receiving the raw value returned by JNA.
     * <p>
     * Compatible {@link FreeMemoryTypeMapper#getFromNativeConverter(java.lang.Class)}
     *
     * @author 唐家林 on 2021-02-02.
     */
    private static class FreeMemoryFromNativeConverter implements FromNativeConverter {
        private final java.util.function.Function<Pointer, ?> function;

        public FreeMemoryFromNativeConverter(java.util.function.Function<Pointer, ?> function) {
            this.function = function;
        }

        @Override
        public Object fromNative(Object nativeValue, FromNativeContext context) {
            Pointer pointer = (Pointer) nativeValue;
            try {
                return function.apply(pointer);
            } finally {
                StdLib.free(pointer);
            }
        }

        @Override
        public Class<?> nativeType() {
            return Pointer.class;
        }
    }

    /**
     * This class is used to adapt {@link FreeMemoryFromNativeConverter}
     * <p>
     * It provides a unified handling effect for the two return-value processing approaches
     * {@link InvocationMapper#getInvocationHandler(com.sun.jna.NativeLibrary, java.lang.reflect.Method)}
     * {@link TypeMapper#getFromNativeConverter(java.lang.Class)}
     * Both return-value handling approaches can achieve a unified processing effect.
     *
     * @author 唐家林 on 2021-02-02.
     */
    private static class FreeMemoryInvocationHandler implements InvocationHandler {

        private final Map<Class<?>, FromNativeConverter> handlerMap;
        private final Function function;

        private FreeMemoryInvocationHandler(Map<Class<?>, FromNativeConverter> handlerMap, Function function) {
            this.handlerMap = handlerMap;
            this.function = function;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Class<?> originalReturnType = method.getReturnType();
            FromNativeConverter fromNativeConverter = handlerMap.get(originalReturnType);
            if (fromNativeConverter == null) {
                return function.invoke(originalReturnType, args);
            }

            Object result = function.invoke(fromNativeConverter.nativeType(), args);
            return fromNativeConverter.fromNative(result, null);
        }

    }

    /** Response value conversion ************************************************************************************/
    private static class StringConvertHandler implements java.util.function.Function<Pointer, String> {
        private static final String ENCODING = StandardCharsets.UTF_8.name();

        @Override
        public String apply(Pointer pointer) {
            return pointer.getString(0, ENCODING);
        }
    }

    private static class WStringConvertHandler implements java.util.function.Function<Pointer, WString> {
        private final StringConvertHandler convertHandler;

        public WStringConvertHandler(StringConvertHandler convertHandler) {
            this.convertHandler = convertHandler;
        }

        @Override
        public WString apply(Pointer pointer) {
            return new WString(convertHandler.apply(pointer));
        }
    }

    private static class GoStringConvertHandler implements java.util.function.Function<Pointer, GoString.ByReference> {
        private final StringConvertHandler convertHandler;

        public GoStringConvertHandler(StringConvertHandler convertHandler) {
            this.convertHandler = convertHandler;
        }

        @Override
        public GoString.ByReference apply(Pointer pointer) {
            return new GoString.ByReference(convertHandler.apply(pointer));
        }
    }

    private static class StringArrayConvertHandler implements java.util.function.Function<Pointer, String[]> {
        private static final String ENCODING = StandardCharsets.UTF_8.name();

        @Override
        public String[] apply(Pointer pointer) {
            return pointer.getStringArray(0, ENCODING);
        }
    }

    private static class WStringArrayConvertHandler implements java.util.function.Function<Pointer, WString[]> {
        private final StringArrayConvertHandler convertHandler;

        public WStringArrayConvertHandler(StringArrayConvertHandler convertHandler) {
            this.convertHandler = convertHandler;
        }

        @Override
        public WString[] apply(Pointer pointer) {
            String[] arr = convertHandler.apply(pointer);
            WString[] wArr = new WString[arr.length];
            for (int i = 0; i < arr.length; i++) {
                wArr[i] = new WString(arr[i]);
            }
            return wArr;
        }
    }
}
