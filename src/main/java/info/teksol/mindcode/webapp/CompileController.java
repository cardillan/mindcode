package info.teksol.mindcode.webapp;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class CompileController extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(CompileController.class);
    private static final Logger perflogger = LoggerFactory.getLogger(CompileController.class.getName() + ".perf");
    private final DataSource dataSource;

    public CompileController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        final long startAt = System.nanoTime();
        logger.info("Started POST /compile?id=",request.getParameter("id"));

        final String id;
        final String sourceCode = request.getParameter("source").replaceAll("\r\n", "\n");

        if (request.getParameter("id") == null || request.getParameter("id").isBlank()) {
            id = UUID.randomUUID().toString();
        } else {
            id = request.getParameter("id");
        }

        try (final Connection connection = dataSource.getConnection()) {
            try (final PreparedStatement statement = connection.prepareStatement("INSERT INTO sources(id, source) VALUES(?::uuid, ?) ON CONFLICT(id) DO UPDATE SET source = ?")) {
                statement.setString(1, id);
                statement.setString(2, sourceCode);
                statement.setString(3, sourceCode);
                statement.execute();
            }
        } catch (SQLException e) {
            logger.error("Failed to insert source code to database: " + id, e);
            return;
        }

        response.sendRedirect("/?s=" + id);
        final long endAt = System.nanoTime();
        perflogger.info("controller=compile duration={}µs", TimeUnit.NANOSECONDS.toMicros(endAt - startAt));
    }
}
