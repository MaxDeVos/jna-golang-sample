package org.example.jna;

import com.sun.jna.WString;
import org.example.jna.gotype.GoSlice;
import org.example.jna.gotype.GoString;
import org.example.utils.NativeProxy;

/**
 * Demo interface (direct-mapping)
 * <p>
 * Direct mapping performs better for primitive types (including Pointer),
 * while interface mapping has a slight advantage when dealing with complex types.
 *
 * @author 唐家林 on 2021-02-02.
 */
public class AwesomeNative {
    static {
        NativeProxy.register("awesome", AwesomeNative.class);
    }

    public static native long add(long a, long b);

    public static native double cosine(double val);

    public static native void sort(GoSlice arrays);

    public static native long print(GoString value);

    public static native String echoString(GoString value);

    public static native WString echoWString(GoString value);

    public static native GoString.ByReference echoGoString(GoString value);

}
