package info.teksol.mc.mindcode.tests;

import org.jspecify.annotations.NullMarked;

/// CRC64 checksum calculator based on the polynomial specified in ISO 3309. The
/// implementation is based on the following publication:
///
/// [Cyclic redundancy check](http://en.wikipedia.org/wiki/Cyclic_redundancy_check)
///
@NullMarked
public final class CRC64 {

    private static final long POLY64REV = 0xd800000000000000L;

    private static final long[] LOOKUPTABLE;

    static {
        LOOKUPTABLE = new long[0x100];
        for (int i = 0; i < 0x100; i++) {
            long v = i;
            for (int j = 0; j < 8; j++) {
                if ((v & 1) == 1) {
                    v = (v >>> 1) ^ POLY64REV;
                } else {
                    v = (v >>> 1);
                }
            }
            LOOKUPTABLE[i] = v;
        }
    }

    /// Calculates the CRC64 checksum for the given data array.
    ///
    /// @param data     data to calculate checksum for
    /// @return checksum value
    public static long hash1(final byte[] data) {
        long sum = 0;
        for (final byte b : data) {
            final int lookupidx = ((int) sum ^ b) & 0xff;
            sum = (sum >>> 8) ^ LOOKUPTABLE[lookupidx];
        }
        return sum;
    }

    private CRC64() {
    }

    // * ECMA: 0x42F0E1EBA9EA3693 / 0xC96C5795D7870F42 / 0xA17870F5D4F51B49
    private static final long POLY64 = 0x42F0E1EBA9EA3693L;
    private static final long[] LOOKUPTABLE2;

    static {
        LOOKUPTABLE2 = new long[0x100];
        for (int i = 0; i < 0x100; i++) {
            long crc = i;
            for (int j = 0; j < 8; j++) {
                if ((crc & 1) == 1) {
                    crc = (crc >>> 1) ^ POLY64;
                } else {
                    crc = (crc >>> 1);
                }
            }
            LOOKUPTABLE2[i] = crc;
        }
    }

    /// The checksum of the data
    /// @param   data    The data to checksum
    /// @return  The checksum of the data
    public static long hash2(final byte[] data) {
        long checksum = 0;

        for (byte datum : data) {
            final int lookupidx = ((int) checksum ^ datum) & 0xff;
            checksum = (checksum >>> 8) ^ LOOKUPTABLE2[lookupidx];
        }

        return checksum;
    }

}








