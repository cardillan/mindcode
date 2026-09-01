package info.teksol.mc.mindcode.compiler.postprocess;


import info.teksol.mc.profile.CompilerProfile;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

@NullMarked
class CompilerOutputTest extends AbstractCodeOutputTest {

    @Test
    void mergesConstArrays() {
        assertOutputs("""
                        #set optimization = none;

                        inline void foo(x)
                            const a[] = (@mono, @poly, @mega, @flare, @horizon);
                            println(a[x]);
                        end;

                        inline void bar(x)
                            const a[] = (@mono, @poly, @mega, @flare, @horizon);
                            println(a[x]);
                        end;

                        foo(rand(5));
                        bar(rand(5));
                        """,
                """
                        op rand *tmp0 5 0
                        set :foo:x *tmp0
                        set *tmp1 :foo:x
                        set :foo:a*rret 6
                        set :foo:a*rind *tmp1
                        read @counter "" *tmp1
                        set *tmp2 :foo:a*r
                        print *tmp2
                        print "\\n"
                        op rand *tmp3 5 0
                        set :bar:x *tmp3
                        set *tmp4 :bar:x
                        set :foo:a*rret 15
                        set :foo:a*rind *tmp4
                        read @counter "" *tmp4
                        set *tmp5 :foo:a*r
                        print *tmp5
                        print "\\n"
                        end
                        select :foo:a*r lessThan :foo:a*rind 3 @mono @flare # Origin: 5, 14, keys: 0, 3
                        set @counter :foo:a*rret
                        select :foo:a*r lessThan :foo:a*rind 3 @poly @horizon # Origin: 5, 14, keys: 1, 4
                        set @counter :foo:a*rret
                        set :foo:a*r @mega                      # Origin: 5, 14, keys: 2
                        set @counter :foo:a*rret
                        print "%s"
                        """.formatted(CompilerProfile.SIGNATURE_STATIC)
        );
    }
}
