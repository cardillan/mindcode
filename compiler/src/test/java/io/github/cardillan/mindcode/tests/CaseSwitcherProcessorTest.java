package io.github.cardillan.mindcode.tests;

import io.github.cardillan.mindcode.profile.CompilerProfile;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Order;

import java.io.IOException;

@NullMarked
@Order(5)
public class CaseSwitcherProcessorTest extends CaseSwitcherProcessorTestBase {

    @Override
    protected CompilerProfile createCompilerProfile() {
        return super.createCompilerProfile().setUseTextTranslations(false).setUseSelectCase(false);
    }

    @AfterAll
    static void done() throws IOException {
        AbstractProcessorTest.done(SCRIPTS_DIRECTORY, CaseSwitcherProcessorTest.class.getSimpleName());
    }
}
