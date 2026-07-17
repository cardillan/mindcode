package info.teksol.mc.mindcode.compiler;

import info.teksol.mc.messages.MessageEmitter;
import info.teksol.mc.profile.GlobalCompilerProfile;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface MessageContext extends CompilerContext, MessageEmitter {
    GlobalCompilerProfile globalCompilerProfile();
}
