package info.teksol.mc.util;

import org.jspecify.annotations.NullMarked;

@NullMarked
public class MathUtils {

    public static int gcd(int a, int b) {
        a = Math.abs(a);
        b = Math.abs(b);

        while (b != 0) {
            int t = b;
            b = a % b;
            a = t;
        }

        return a;
    }

    public static int gcd(int[] values) {
        int result = 0;

        for (int value : values) {
            if (value != result) {
                if ((result = gcd(result, value)) == 1) {
                    break; // Cannot get any smaller
                }
            }
        }

        return result;
    }
}
