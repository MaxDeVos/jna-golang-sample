package org.example.utils;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import org.example.jna.mapper.FreeMemoryTypeMapper;

import java.util.HashMap;
import java.util.Map;

/**
 * Dynamic library proxy
 *
 * @author 唐家林 on 2021-01-28.
 */
public class NativeProxy {

    /**
     * Native method mapping (direct-mapping)
     * <p>
     * Automatically frees return-value memory by injecting a
     * {@link com.sun.jna.TypeMapper}.
     *
     * @param libName library name
     * @param tClass  class mapped to this native library
     * @see com.sun.jna.Native#register(Class, com.sun.jna.NativeLibrary)
     * @see com.sun.jna.Native#getConversion(Class, com.sun.jna.TypeMapper, boolean)
     */
    public static void register(String libName, Class<?> tClass) {
        Map<String, Object> options = new HashMap<>(2);
        options.put(Library.OPTION_CLASSLOADER, tClass.getClassLoader());
        // Provide a TypeMapper that converts String return values and then
        // frees memory not managed by Java to prevent memory leaks
        options.put(Library.OPTION_TYPE_MAPPER, new FreeMemoryTypeMapper());
        NativeLibrary library = NativeLibrary.getInstance(libName, options);
        Native.register(tClass, library);
    }

    /**
     * Interface-based mapping (interface-mapping)
     *
     * <p>
     * Automatically frees return-value memory by injecting an
     * {@link com.sun.jna.InvocationMapper} and using different
     * {@link java.lang.reflect.InvocationHandler} implementations
     * for different return types.
     *
     * @param libName library name
     * @param tClass  interface mapped to this native library
     * @see com.sun.jna.Library.Handler#Handler(String, Class, Map)
     * @see com.sun.jna.Library.Handler#invoke(Object, java.lang.reflect.Method, Object[])
     */
    public static <T extends Library> T load(String libName, Class<T> tClass) {
        Map<String, Object> options = new HashMap<>(2);
        // Provide an InvocationMapper that converts String return values and then
        // frees memory not managed by Java to prevent memory leaks
        options.put(Library.OPTION_INVOCATION_MAPPER, new FreeMemoryTypeMapper());
        return Native.load(libName, tClass, options);
    }
}
