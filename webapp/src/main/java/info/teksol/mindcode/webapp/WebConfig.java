package info.teksol.mindcode.webapp;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

import java.io.IOException;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/")
                .resourceChain(true)
                .addResolver(new PathResourceResolver() {
                    @Override
                    protected Resource getResource(String resourcePath, Resource location) throws IOException {
                        Resource requested = location.createRelative(resourcePath);

                        // If it's a real file (CSS, JS, Images, or actual JSON), serve it
                        if (requested.exists() && requested.isReadable()) {
                            return requested;
                        }

                        // If it's a request for data or a static asset with an extension,
                        // DO NOT serve the fallback. Return null so Spring sends a 404.
                        if (resourcePath.contains(".") && !resourcePath.endsWith(".html")) {
                            return null; 
                        }

                        // Try appending .html for pretty URLs
                        if (!resourcePath.contains(".")) {
                            Resource htmlResource = location.createRelative(resourcePath + ".html");
                            if (htmlResource.exists() && htmlResource.isReadable()) {
                                return htmlResource;
                            }
                        }

                        // Fallback for SPA routing (only for page requests)
                        Resource fallback = location.createRelative("200.html");
                        return (fallback.exists() && fallback.isReadable()) ? fallback : null;
                    }
                });
    }
}