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
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class HomeController extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
    private static final Logger perflogger = LoggerFactory.getLogger(HomeController.class.getName() + ".perf");
    private static final Random RAND = new Random(System.currentTimeMillis());

    private final VelocityContext parent;
    private final Template template;
    private final Map<String, Template> samples;
    private final DataSource dataSource;

    HomeController(VelocityEngine engine, VelocityContext parent, DataSource dataSource) {
        this.parent = parent;
        this.dataSource = dataSource;

        this.template = engine.getTemplate("templates/home.html.vtl");
        this.samples = Map.of(
                "bind-single-unit", engine.getTemplate("samples/7-bind-one-unit.mnd"),
                "one-thorium", engine.getTemplate("samples/2-thorium-reactor-stopper.mnd"),
                "many-thorium", engine.getTemplate("samples/3-multi-thorium-reactor.mnd"),
                "mine-coord", engine.getTemplate("samples/5-mining-drone.mnd"),
                "upgrade-conveyors", engine.getTemplate("samples/6-upgrade-copper-conveyors-to-titanium.mnd"),
                "heal-damaged-building", engine.getTemplate("samples/8-heal-damaged-building.mnd"),
                "features-demo", engine.getTemplate("samples/4-demo.mnd")

                // "poly", engine.getTemplate("samples/1-bind-poly-move-to-core.mnd")
        );
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        final long startAt = System.nanoTime();
        logger.info("Started GET /?s={}", request.getParameter("s"));

        final long getSourceStartAt = System.nanoTime();
        String id = request.getParameter("s");
        if (id == null || id.isEmpty()) {
            final int elem = RAND.nextInt(samples.size());
            id = samples.keySet().stream().skip(elem).findFirst().get();
        }

        final String sourceCode;
        if (samples.containsKey(id)) {
            sourceCode = returnSampleSourceCode(id);
        } else if (id.equals("clean")) {
            sourceCode = "";
        } else {
            try {
                sourceCode = returnDbSourceCode(id);
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
        context.put("source_loc", sourceCode.split("\n").length);
        context.put("compiled_code", result._1);
        context.put("compiled_loc", result._1.split("\n").length);
        context.put("syntax_errors", result._2);
        context.put("sample", id);
        context.put("id", id.length() == 36 ? id : null); // if it's a UUID

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
        samples.get(id).merge(new VelocityContext(), writer);
        return writer.toString();
    }

    private Tuple2<String, List<String>> compile(String program) {
        String instructions = "";

        final MindcodeLexer lexer = new MindcodeLexer(CharStreams.fromString(program));
        final MindcodeParser parser = new MindcodeParser(new BufferedTokenStream(lexer));
        final List<String> errors = new ArrayList<>();
        parser.addErrorListener(new BaseErrorListener() {
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
                errors.add("Syntax error: " + offendingSymbol + " on line " + line + ":" + charPositionInLine + ": " + msg);
            }
        });

        try {
            final MindcodeParser.ProgramContext context = parser.program();
            final Seq prog = AstNodeBuilder.generate(context);
            final List<LogicInstruction> result = LogicInstructionLabelResolver.resolve(
                    LogicInstructionPeepholeOptimizer.optimize(
                            LogicInstructionGenerator.generateFrom(prog)
                    )
            );

            instructions = LogicInstructionPrinter.toString(result);
        } catch (RuntimeException e) {
            errors.add(e.getMessage());
        }

        return new Tuple2<>(instructions, errors);
    }
}
