package info.teksol.mindcode.webapp;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.StatisticsHandler;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Optional;

public class Main {
    public static void main(String[] args) throws Exception {
        final String ENV = Optional.ofNullable(System.getenv("ENV")).orElse("development");

        final String jdbcUrl = Optional.ofNullable(System.getenv("JDBC_DATABASE_URL"))
                .orElse("jdbc:postgresql://localhost/mindcode_development");
        final HikariConfig config = new HikariConfig();
        config.setDriverClassName(org.postgresql.Driver.class.getName());
        config.setJdbcUrl(jdbcUrl);
        final DataSource dataSource = new HikariDataSource(config);
        try (final Connection connection = dataSource.getConnection()) {
            try (final Statement statement = connection.createStatement()) {
                statement.execute("" +
                        "CREATE TABLE IF NOT EXISTS sources(" +
                        "  id uuid primary key" +
                        ", source text not null" +
                        ", created_at timestamp with time zone default current_timestamp" +
                        ")"
                );
            }
        }

        final VelocityEngine velocityEngine = new VelocityEngine();
        velocityEngine.setProperty(VelocityEngine.ENCODING_DEFAULT, "UTF-8");
        velocityEngine.setProperty("resource.loaders", "classpath");
        velocityEngine.setProperty("resource.loader.classpath.class", ClasspathResourceLoader.class.getName());
        velocityEngine.setProperty("resource.loader.classpath.cache", ENV.equals("production"));
        velocityEngine.setProperty("resource.loader.classpath.modificationCheckInterval", -1);
        velocityEngine.init();

        final Server server = new Server();
        final ServerConnector connector = new ServerConnector(server);
        final String port = Optional.ofNullable(System.getenv("PORT")).orElse("8080");
        connector.setPort(Integer.parseInt(port));
        server.addConnector(connector);

        final ServletContextHandler context = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        context.setContextPath("/");
        context.addServlet(new ServletHolder(new CompileController(dataSource)), "/compile");
        context.addServlet(new ServletHolder(new HomeController(velocityEngine, new VelocityContext(), dataSource)), "/");

        final GzipHandler gzipHandler = new GzipHandler();
        gzipHandler.setHandler(context);
        final StatisticsHandler statsHandler = new StatisticsHandler();
        statsHandler.setHandler(gzipHandler);

        server.setHandler(statsHandler);
        server.start();
    }
}
