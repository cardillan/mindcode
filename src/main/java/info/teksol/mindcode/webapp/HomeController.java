package info.teksol.mindcode.webapp;

import info.teksol.mindcode.Tuple2;
import info.teksol.mindcode.ast.AstNodeBuilder;
import info.teksol.mindcode.ast.Seq;
import info.teksol.mindcode.grammar.MindcodeLexer;
import info.teksol.mindcode.grammar.MindcodeParser;
import info.teksol.mindcode.mindustry.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.antlr.v4.runtime.*;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class HomeController extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
    private static final Logger perflogger = LoggerFactory.getLogger(HomeController.class.getName() + ".perf");
    private static final Random RAND = new Random(System.currentTimeMillis());

    private final VelocityContext parent;
    private final Template template;
    private final List<Template> samples;
    private final DataSource dataSource;

    HomeController(VelocityEngine engine, VelocityContext parent, DataSource dataSource) {
        this.parent = parent;
        this.dataSource = dataSource;

        this.template = engine.getTemplate("templates/home.html.vtl");
        this.samples = List.of(
                engine.getTemplate("samples/1-bind-poly-move-to-core.mnd"),
                engine.getTemplate("samples/2-thorium-reactor-stopper.mnd"),
                engine.getTemplate("samples/3-multi-thorium-reactor.mnd"),
                engine.getTemplate("samples/4-demo.mnd")
        );
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        final long startAt = System.nanoTime();
        logger.info("Started GET /?s={}", request.getParameter("s"));

        String id = request.getParameter("s");
        if (id == null) id = "";

        final long getSourceStartAt = System.nanoTime();
        final String sourceCode;
        switch (id) {
            case "":
                sourceCode = returnSampleSourceCode(String.valueOf(RAND.nextInt(samples.size())));
                break;

            case "0":
            case "1":
            case "2":
            case "3":
                sourceCode = returnSampleSourceCode(id);
                break;

            default:
                try {
                    sourceCode = returnDbSourceCode(id);
                    break;
                } catch (SQLException e) {
                    throw new ServletException(e);
                }
        }
        final long getSourceEndAt = System.nanoTime();

        final long compileStartAt = System.nanoTime();
        final Tuple2<String, List<String>> result = compile(sourceCode);
        final long compileEndAt = System.nanoTime();

        final long renderStartAt = System.nanoTime();
        final VelocityContext context = new VelocityContext(parent);
        context.put("source_code", sourceCode);
        context.put("compiled_code", result._1);
        context.put("syntax_errors", result._2);
        context.put("sample", id);
        if (id.length() == 36) context.put("id", id); // if it's a UUID

        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
        template.merge(context, response.getWriter());
        final long renderEndAt = System.nanoTime();

        perflogger.info("controller=home duration={}µs getsource={}µs compile={}µs render={}µs",
                TimeUnit.NANOSECONDS.toMicros(renderEndAt - startAt),
                TimeUnit.NANOSECONDS.toMicros(getSourceEndAt - getSourceStartAt),
                TimeUnit.NANOSECONDS.toMicros(compileEndAt - compileStartAt),
                TimeUnit.NANOSECONDS.toMicros(renderEndAt - renderStartAt)
        );
    }

    private String returnDbSourceCode(String id) throws SQLException {
        try (final Connection connection = dataSource.getConnection()) {
            try (final PreparedStatement statement = connection.prepareStatement("SELECT source FROM sources WHERE id = ?::uuid")) {
                statement.setString(1, id);
                try (final ResultSet resultSet = statement.executeQuery()) {
                    resultSet.next();
                    return resultSet.getString(1);
                }
            }
        }
    }

    private String returnSampleSourceCode(String id) {
        final StringWriter writer = new StringWriter();
        samples.get(Integer.valueOf(id)).merge(new VelocityContext(), writer);
        return writer.toString();
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
