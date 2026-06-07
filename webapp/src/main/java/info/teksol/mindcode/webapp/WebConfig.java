package info.teksol.mindcode.webapp;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // cache page data for an hour, includes stuff like
        // the list of samples and their source code
        // Note: Spring's PathPatternParser strictly prohibits "**" in the middle of a pattern
        // like "/**/__data.json". Instead, we provide patterns for different depth levels.
        registry.addResourceHandler(
                "/__data.json",
                "/*/__data.json",
                "/*/*/__data.json",
                "/*/*/*/__data.json"
        )
                .addResourceLocations("classpath:/static/")
                .setCacheControl(CacheControl.maxAge(1, TimeUnit.HOURS));

        // never cache version.json since it can change with each
        // deployment and is used to trigger client updates
        registry.addResourceHandler("/_app/version.json")
                .addResourceLocations("classpath:/static/")
                .setCacheControl(CacheControl.noStore());

        // cache immutable assets for a year since 
        // they have content hashes in their names
        registry.addResourceHandler("/_app/immutable/**")
                .addResourceLocations("classpath:/static/_app/immutable/")
                .setCacheControl(CacheControl.maxAge(365, TimeUnit.DAYS).cachePublic());

        // cache html pages for an hour and handle SPA routing
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/")
                .setCacheControl(CacheControl.maxAge(1, TimeUnit.HOURS))
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