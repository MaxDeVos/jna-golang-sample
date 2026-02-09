package org.example.jna.gotype;

import com.sun.jna.Structure;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * // GoString class maps to:
 * // C type struct { const char *p; GoInt n; }
 * // Fields must be public; otherwise an exception will occur: “which do not match declared field names”
 *
 * @author 唐家林 on 2021-01-14.
 */
public class GoString extends Structure implements Structure.ByValue {
    public String value;
    public long n;

    public GoString() {
    }

    public GoString(String value) {
        // golang 中 string 没有 null
        this.value = value == null ? "" : value;
        this.n = this.value.getBytes(StandardCharsets.UTF_8).length;
    }

    @Override
    protected List<String> getFieldOrder() {
        return Arrays.asList("value", "n");
    }

    public static class ByReference extends GoString implements Structure.ByReference {
        public ByReference() {
        }

        public ByReference(String value) {
            super(value);
        }
    }
}
