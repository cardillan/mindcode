package io.github.cardillan.mc.mindcode.compiler;

import io.github.cardillan.mc.messages.MessageEmitter;
import io.github.cardillan.mc.profile.GlobalCompilerProfile;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface MessageContext extends CompilerContext, MessageEmitter {
    GlobalCompilerProfile globalCompilerProfile();
}
