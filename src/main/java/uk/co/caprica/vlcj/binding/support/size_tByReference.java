package uk.co.caprica.vlcj.binding.support;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.ByReference;

public class size_tByReference extends ByReference {

    public size_tByReference() {
        super(Native.SIZE_T_SIZE);
    }

    public size_t getValue() {
        Pointer pointer = getPointer();
        return new size_t(Native.SIZE_T_SIZE == 8 ? pointer.getLong(0) : pointer.getInt(0));
    }

}
