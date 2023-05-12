package info.teksol.mindcode.webapp;

import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class PasswordAuthenticatorTest {
    private final PasswordAuthenticator sut = new PasswordAuthenticator(4);

    // Deactivated for now. Signing-in is not supported, hashing tests might fail on too fast/too slow computers.
    // @Test
    void defaultPasswordAuthenticatorTakesAReasonableAmountOfTime() {
        final PasswordAuthenticator sut = new PasswordAuthenticator();

        // Attempt to force JIT to kick-in
        final long[] durations = new long[8];
        for (int i = 0; i < durations.length; i++) {
            final long start = System.currentTimeMillis();
            sut.hash(("password:" + i).toCharArray());
            final long end = System.currentTimeMillis();
            durations[i] = end - start;
        }
        assertEquals(8, durations.length);
        for (long duration : durations) {
            System.err.print(duration);
            System.err.print(" ");
        }
        System.err.println();
        System.err.flush();

        // Now, do our *real* test
        long start = System.currentTimeMillis();
        sut.hash("password:1025".toCharArray());
        long end = System.currentTimeMillis();

        long duration = TimeUnit.MILLISECONDS.toMillis(end - start);
        assertTrue(duration >= 850, "Expected hashing to take at least 850 ms, hashed under " + duration + " ms");
        assertTrue(duration < 2000, "Expected hashing to take at most 2000 ms, hashed over " + duration + " ms");
    }

    @Test
    void hashedPasswordMatchesOriginal() {
        final char[] password = {'a', 'p', 'a', 's', 's', 'w', 'o', 'r', 'd'};
        final String hashedPassword = sut.hash(password);
        assertTrue(sut.authenticate(password, hashedPassword), "Passwords failed to match!");
    }

    @Test
    void failsToMatchIncorrectPassword() {
        final String hashedPassword = sut.hash(new char[]{'a', 'p', 'a', 's', 's', 'w', 'o', 'r', 'd'});
        assertFalse(sut.authenticate(new char[]{'a', 'p', 'a', 's', 's', 'w', 'o', 'r', 'd', '1'}, hashedPassword), "Passwords matched!?!");
    }
}
