package org.example.utils;

import com.sun.jna.Native;
import com.sun.jna.Platform;
import com.sun.jna.Pointer;

/**
 * Common library
 *
 * @author 唐家林 on 2021-01-28.
 */
public class StdLib {
    static {
        Native.register(Platform.C_LIBRARY_NAME);
    }

    /**
     * Free Native Heap memory.
     *
     * @param p pointer value of the memory to be freed
     */
    public static native void free(long p);

    /**
     * Free memory allocated by the underlying native language.
     *
     * <p>
     * Calls the C standard library {@code free} function to release memory.
     *
     * <p>
     * Usage scenario:
     * Memory created and owned by the underlying language. For example, in the
     * following code, the {@code C.CString(msg)} returned must be released
     * using this method:
     *
     * <pre>
     * //export echoString
     * func echoString(msg string) *C.char {
     *     return C.CString(msg)
     * }
     * </pre>
     *
     * @param pointer pointer to the memory to be freed
     */
    public static void free(Pointer pointer) {
        long p = Pointer.nativeValue(pointer);
        if (p == 0L) {
            return;
        }
        free(p);
        Pointer.nativeValue(pointer, 0L);
    }

    /**
     * Free Native Heap memory.
     *
     * <p>
     * Calls {@link Native#free(long)} to release memory.
     *
     * <p>
     * Usage scenario:
     * Memory allocated on the Java side using JNA, for example the {@code new Memory}
     * created in the following code must be released using this method:
     *
     * <pre>
     * int size = Native.getNativeSize(long.class);
     * Memory arr = new Memory(array.length * size);
     * arr.write(0, array, 0, array.length);
     * </pre>
     *
     * @param pointer pointer to the memory to be freed
     */
    public static void freeNativeHeap(Pointer pointer) {
        long p = Pointer.nativeValue(pointer);
        if (p == 0L) {
            return;
        }
        Native.free(p);
        Pointer.nativeValue(pointer, 0L);
    }
}
