package io.github.cardillan.mindcode.compiler;

import io.github.cardillan.mindcode.messages.MessageEmitter;
import io.github.cardillan.mindcode.profile.GlobalCompilerProfile;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface MessageContext extends CompilerContext, MessageEmitter {
    GlobalCompilerProfile globalCompilerProfile();
}
