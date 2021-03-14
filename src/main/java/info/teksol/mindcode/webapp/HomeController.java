package info.teksol.mindcode.webapp;

import info.teksol.mindcode.Tuple2;
import info.teksol.mindcode.ast.AstNodeBuilder;
import info.teksol.mindcode.ast.Seq;
import info.teksol.mindcode.grammar.MindcodeLexer;
import info.teksol.mindcode.grammar.MindcodeParser;
import info.teksol.mindcode.mindustry.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.antlr.v4.runtime.*;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class HomeController extends AbstractHandler {
    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
    private static final Random RAND = new Random(System.currentTimeMillis());

    private final VelocityContext parent;
    private final Template template;
    private final List<Template> samples;

    HomeController(VelocityEngine engine, VelocityContext parent) {
        this.parent = parent;

        this.template = engine.getTemplate("templates/home.html.vtl");
        this.samples = List.of(
                engine.getTemplate("samples/1-bind-poly-move-to-core.mnd"),
                engine.getTemplate("samples/2-thorium-reactor-stopper.mnd"),
                engine.getTemplate("samples/3-multi-thorium-reactor.mnd"),
                engine.getTemplate("samples/4-demo.mnd")
        );
    }

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String sampleNo = baseRequest.getParameter("s");
        if (sampleNo == null || sampleNo.equals("")) sampleNo = String.valueOf(RAND.nextInt(samples.size()));

        final StringWriter writer = new StringWriter();
        samples.get(Integer.valueOf(sampleNo)).merge(new VelocityContext(), writer);
        final String sourceCode = writer.toString();
        final Tuple2<String, List<String>> result = compile(sourceCode);

        final VelocityContext context = new VelocityContext(parent);
        context.put("source_code", sourceCode);
        context.put("compiled_code", result._1);
        context.put("syntax_errors", result._2);
        context.put("sample", sampleNo);

        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
        template.merge(context, response.getWriter());
        baseRequest.setHandled(true);
    }

    @Override
    public <T> Collection<T> getCachedBeans(Class<T> clazz) {
        return null;
    }

    @Override
    public boolean isDumpable(Object o) {
        return false;
    }

    @Override
    public String dumpSelf() {
        return null;
    }

    private Tuple2<String, List<String>> compile(String program) {
        final MindcodeLexer lexer = new MindcodeLexer(CharStreams.fromString(program));
        final MindcodeParser parser = new MindcodeParser(new BufferedTokenStream(lexer));
        final List<String> errors = new ArrayList<>();
        parser.addErrorListener(new BaseErrorListener() {
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
                errors.add("Syntax error: " + offendingSymbol + " on line " + line + ":" + charPositionInLine + ": " + msg);
            }
        });

        final MindcodeParser.ProgramContext context = parser.program();
        final Seq prog = AstNodeBuilder.generate(context);
        final List<MOpcode> result = MOpcodeLabelResolver.resolve(
                MOpcodePeepholeOptimizer.optimize(
                        MOpcodeGenerator.generateFrom(prog)
                )
        );

        final String opcodes = MOpcodePrinter.toString(result);
        return new Tuple2<>(opcodes, errors);
    }
}
