package info.teksol.mindcode.webapp;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;

import java.util.Optional;

public class Main {
    public static void main(String[] args) throws Exception {
        final String ENV = Optional.ofNullable(System.getenv("ENV")).orElse("development");

        final VelocityEngine velocityEngine = new VelocityEngine();
        velocityEngine.setProperty(VelocityEngine.ENCODING_DEFAULT, "UTF-8");
        velocityEngine.setProperty("resource.loaders", "classpath");
        velocityEngine.setProperty("resource.loader.classpath.class", ClasspathResourceLoader.class.getName());
        velocityEngine.setProperty("resource.loader.classpath.cache", false /* ENV.equals("production") */);
        velocityEngine.setProperty("resource.loader.classpath.modificationCheckInterval", -1);
        velocityEngine.init();

        final Server server = new Server();
        final ServerConnector connector = new ServerConnector(server);
        String port = System.getenv("PORT");
        if (port == null) port = "8080";
        connector.setPort(Integer.parseInt(port));
        server.addConnector(connector);

        final ContextHandler homePage = new ContextHandler("/");

        homePage.setHandler(new HomeController(velocityEngine, new VelocityContext()));
        server.setHandler(
                new ContextHandlerCollection(
                        homePage
                )
        );
        server.start();
    }
}
