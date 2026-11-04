package io.github.cardillan.mc.mindcode.tests;

import io.github.cardillan.mc.profile.CompilerProfile;
import io.github.cardillan.mc.profile.GenerationGoal;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Order;

import java.io.IOException;

@NullMarked
@Order(5)
public class CaseSwitcherTranslationProcessorTest extends CaseSwitcherProcessorTestBase {

    @Override
    protected CompilerProfile createCompilerProfile() {
        return super.createCompilerProfile().setGoal(GenerationGoal.SIZE).setSymbolicLabels(true).setUseTextTranslations(true).setUseSelectCase(true);
    }

    @AfterAll
    static void done() throws IOException {
        AbstractProcessorTest.done(SCRIPTS_DIRECTORY, CaseSwitcherTranslationProcessorTest.class.getSimpleName());
    }
}
