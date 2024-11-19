package info.teksol.mindcode.exttest;

public class Code {
    public static final String code = """
            require math;
            
            assertEquals(0, sign(0), "sign(0)");
            assertEquals(0, sign(1e-7), "sign(1e-7)");
            assertEquals(1, sign(1e-5), "sign(1e-5)");
            assertEquals(-1, sign(-5), "sign(-5)");
            assertEquals(0, signExact(0), "signExact(0)");
            assertEquals(1, signExact(1e-7), "signExact(1e-7)");
            assertEquals(-1, signExact(-1e-7), "signExact(-1e-7)");
            assertEquals(true, isZero(0), "isZero(0)");
            assertEquals(false, isZero(1e-50), "isZero(1e-50)");
            assertEquals(false, isZero(-1e-50), "isZero(-1e-50)");
            assertEquals(false, isZero(0.01,0.01), "isZero(0.01,0.01)");
            assertEquals(10, sum(1,2,3,4), "sum(1,2,3,4)");
            assertEquals(3, median(1,3,5), "median(1,3,5)");
            assertEquals(1, median(1,1,1), "median(1,1,1)");
            assertEquals(1, median(1,1,3), "median(1,1,3)");
            assertEquals(3, median(1,3,3), "median(1,3,3)");
            assertEquals(5, median(2,4,6,8), "median(2,4,6,8)");
            assertEquals(5, median(8,6,4,2), "median(8,6,4,2)");
            assertEquals(5, median(4,8,2,6), "median(4,8,2,6)");
            assertEquals(5, median(2,6,4,8), "median(2,6,4,8)");
            assertEquals(5, median(6,2,8,4), "median(6,2,8,4)");
            assertEquals(3, median(1,2,3,4,5), "median(1,2,3,4,5)");
            assertEquals(3, median(5,4,3,2,1), "median(5,4,3,2,1)");
            assertEquals(1, median(1,1,1,1,1), "median(1,1,1,1,1)");
            assertEquals(1, median(1,1,1,1,5), "median(1,1,1,1,5)");
            assertEquals(1, median(1,1,1,5,5), "median(1,1,1,5,5)");
            assertEquals(5, median(1,1,5,5,5), "median(1,1,5,5,5)");
            assertEquals(5, median(1,5,5,5,5), "median(1,5,5,5,5)");
            assertEquals(5, median(5,5,5,5,5), "median(5,5,5,5,5)");
            assertEquals(1, median(1,1,1,1,1,1), "median(1,1,1,1,1,1)");
            assertEquals(1, median(1,1,1,1,1,5), "median(1,1,1,1,1,5)");
            assertEquals(1, median(1,1,1,1,5,5), "median(1,1,1,1,5,5)");
            assertEquals(3, median(1,1,1,5,5,5), "median(1,1,1,5,5,5)");
            assertEquals(5, median(1,1,5,5,5,5), "median(1,1,5,5,5,5)");
            assertEquals(5, median(1,5,5,5,5,5), "median(1,5,5,5,5,5)");
            assertEquals(5, median(5,5,5,5,5,5), "median(5,5,5,5,5,5)");
            assertEquals(5, median(1,3,5,7,9,2,4,6,8), "median(1,3,5,7,9,2,4,6,8)");
            """;
}
