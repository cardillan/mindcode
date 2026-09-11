package info.teksol.mc.util;

import org.jspecify.annotations.NullMarked;

@NullMarked
public enum EscapeClass {
    MINIMAL {
        @Override
        public boolean matches(int ch) {
            return ch == 0 || ch == '\r' || ch == '\n' || ch == '\\' || ch == '"' || Character.isSurrogate((char) ch);
        }
    },

    NON_PRINTABLE {
        @Override
        public boolean matches(int ch) {
            return ch < ' ' || ch == 0x7F || ch == '\\' || ch == '"' || Character.isSurrogate((char) ch);
        }
    },

    NON_ASCII {
        @Override
        public boolean matches(int ch) {
            return ch < ' ' || ch == 0x7F || ch == '\\' || ch == '"' || ch > 255;
        }
    },

    ALL {
        @Override
        public boolean matches(int ch) {
            return true;
        }
    };

    public abstract boolean matches(int ch);
}
