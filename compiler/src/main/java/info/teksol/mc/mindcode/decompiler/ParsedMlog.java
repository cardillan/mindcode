package info.teksol.mc.mindcode.decompiler;

import java.util.List;
import java.util.Map;

public record ParsedMlog(Map<Integer, String> labels, List<Object> content) {
}
