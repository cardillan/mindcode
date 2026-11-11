package io.github.cardillan.mindcode.webapp;

import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/releases")
public class ReleaseController {
    private final JdbcTemplate jdbcTemplate;
    private final Resource releases = new ClassPathResource("releases.json");

    public ReleaseController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostMapping("/check")
    public ResponseEntity<Resource> check(
            @RequestBody ReleaseCheckRequest request) throws IOException {

        jdbcTemplate.update("INSERT INTO release_usage (version) VALUES (?)", request.version());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .cacheControl(CacheControl.noStore())
                .body(releases);
    }

    @PostConstruct
    void checkResources() {
        if (!releases.exists()) {
            throw new IllegalStateException("Missing releases.json resource");
        }
    }

    public record ReleaseCheckRequest(String version) {}
}
