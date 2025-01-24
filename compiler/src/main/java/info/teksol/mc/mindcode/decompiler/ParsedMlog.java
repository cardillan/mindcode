package info.teksol.mc.mindcode.decompiler;

import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.Map;

@NullMarked
public record ParsedMlog(Map<Integer, String> labels, List<Object> content) {
}
