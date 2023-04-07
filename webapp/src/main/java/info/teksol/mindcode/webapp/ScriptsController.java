package info.teksol.mindcode.webapp;

import info.teksol.mindcode.compiler.CompilerOutput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.security.SecureRandom;
import java.sql.ResultSet;
import java.util.List;
import java.util.UUID;

import static info.teksol.mindcode.compiler.CompilerFacade.compile;
import static org.springframework.http.HttpStatus.*;

@Controller
@RequestMapping(value = "/scripts")
public class ScriptsController {
    private static final String slugSource = "abcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom random = new SecureRandom();

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static String generateVersionSlug() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("sv_");
        for (int i = 0; i < 16; i++) {
            int index = random.nextInt(slugSource.length());
            buffer.append(slugSource.charAt(index));
        }

        return buffer.toString();
    }

    @GetMapping
    public ModelAndView listScripts(@RequestParam(required = false, name = "q") String query, HttpSession session) {
        final User user = authenticate(session);

        if (query == null) query = "";
        query = query
                .replaceAll("%", "")
                .replaceFirst("^", "%")
                .replaceFirst("$", "%");
        final List<Script> scripts = jdbcTemplate.query(
                "SELECT scripts.id, name, recorded_at, username AS author_name\n" +
                        "FROM scripts\n" +
                        "  JOIN users ON users.id = scripts.author_id\n" +
                        "WHERE author_id = ?::uuid AND (name || source ilike ?)\n" +
                        "ORDER BY lower(name), recorded_at",
                (ResultSet rs, int rowNum) -> new Script(
                        UUID.fromString(rs.getString("id")),
                        rs.getString("name"),
                        null,
                        rs.getString("author_name"),
                        rs.getTimestamp("recorded_at").toInstant()
                ),
                user.getId(), query
        );
        return new ModelAndView(
                "scripts/index",
                "model",
                new ListScriptData(scripts, query.replaceAll("%", ""))
        );
    }

    @GetMapping(value = "/new")
    public ModelAndView newScript(HttpSession session) {
        final User user = authenticate(session);

        return new ModelAndView(
                "scripts/new",
                "model",
                new NewScriptData(
                        "",
                        "// Your Mindcode source",
                        0,
                        "end",
                        0,
                        List.of()
                )
        );
    }

    @PostMapping
    public String create(@RequestParam("script[name]") String name,
                         @RequestParam("script[source]") String source,
                         HttpSession session) {
        final User user = authenticate(session);

        final UUID id = UUID.randomUUID();
        jdbcTemplate.update(
                "INSERT INTO public.scripts(id, name, author_id, source) VALUES (?::uuid, ?, ?::uuid, ?)",
                id.toString(), name, user.getId(), source.replaceAll("\r\n", "\n"));
        return "redirect:/scripts/" + id.toString() + "/edit";
    }

    @GetMapping(value = "/{id}/edit")
    public ModelAndView editScript(@PathVariable(value = "id") UUID id, HttpSession session) {
        final User user = authenticate(session);

        final List<ScriptVersion> versionHistory = jdbcTemplate.query(
                "SELECT id, version, name, source, version_slug, committed_at\n" +
                        "FROM script_versions\n" +
                        "WHERE script_id = ?\n" +
                        "ORDER BY version DESC",
                (rs, rowNum) -> new ScriptVersion(
                        rs.getLong("id"),
                        rs.getInt("version"),
                        rs.getString("name"),
                        rs.getString("source"),
                        rs.getString("version_slug"),
                        rs.getTimestamp("committed_at").toInstant()),
                id);

        final EditScriptData data = jdbcTemplate.queryForObject(
                "SELECT\n" +
                        "  scripts.name\n" +
                        ", scripts.source\n" +
                        "FROM scripts\n" +
                        "WHERE (author_id = ?::uuid)\n" +
                        "  AND id = ?::uuid",
                (rs, rowNum) -> {
                    final String source = rs.getString("source");
                    final CompilerOutput compiled = compile(source, true);

                    return new EditScriptData(
                            id,
                            rs.getString("name"),
                            source,
                            source.split("\n").length,
                            compiled.getInstructions(),
                            compiled.getInstructions().split("\n").length,
                            compiled.getErrors(),
                            versionHistory
                    );
                },
                user.getId(), id
        );

        if (data == null) throw new ResponseStatusException(NOT_FOUND, "No script with this ID");

        return new ModelAndView(
                "scripts/edit",
                "model",
                data
        );
    }

    @PostMapping("/{id}/version/{slug}/rollback")
    public String rollbackScript(@PathVariable(value = "id") UUID id,
                                 @PathVariable(value = "slug") String slug,
                                 HttpSession session) {
        final User user = authenticate(session);

        int affectedRows;

        // In order to never lose work, commit the current version and then rollback
        affectedRows = jdbcTemplate.update("INSERT INTO script_versions(script_id, name, source, version_slug, version)\n" +
                        "  SELECT scripts.id, scripts.name, scripts.source, ?, (SELECT coalesce(max(version), 0) FROM script_versions WHERE script_id = ?::uuid) + 1\n" +
                        "  FROM scripts\n" +
                        "  WHERE id = ?::uuid" +
                        "    AND author_id = ?::uuid",
                generateVersionSlug(), id, id, user.getId());
        if (affectedRows == 0) throw new ResponseStatusException(NOT_FOUND, "No script with this ID");

        affectedRows = jdbcTemplate.update(
                "UPDATE scripts\n" +
                        "SET name = v.name, source = v.source\n" +
                        "FROM script_versions AS v\n" +
                        "WHERE v.script_id = scripts.id\n" +
                        "  AND v.version_slug = ?\n" +
                        "  AND v.script_id = ?\n" +
                        "  AND scripts.id = ?\n" +
                        "  AND scripts.author_id = ?",
                slug, id, id, user.getId());
        if (affectedRows == 0) throw new ResponseStatusException(NOT_FOUND, "No script with this ID");

        return "redirect:/scripts/" + id + "/edit";
    }

    @PostMapping("/{id}")
    public String putScript(@PathVariable(value = "id") UUID id,
                            @RequestParam("script[name]") String name,
                            @RequestParam("script[source]") String source,
                            @RequestParam("action") String action,
                            HttpSession session) {
        final User user = authenticate(session);

        final int affectedRows;
        switch (action) {
            case "compile":
                affectedRows = jdbcTemplate.update(
                        "UPDATE scripts SET name = ?, source = ? \n" +
                                "WHERE author_id = ?::uuid AND id = ?::uuid",
                        name, source, user.getId(), id);
                if (affectedRows == 0) throw new ResponseStatusException(NOT_FOUND, "No script with this ID");
                return "redirect:/scripts/" + id.toString() + "/edit";

            case "commit":
                affectedRows = jdbcTemplate.update(
                        "UPDATE scripts SET name = ?, source = ? \n" +
                                "WHERE author_id = ?::uuid AND id = ?::uuid",
                        name, source, user.getId(), id);
                if (affectedRows == 0) throw new ResponseStatusException(NOT_FOUND, "No script with this ID");

                final String versionSlug = generateVersionSlug();
                jdbcTemplate.update("INSERT INTO script_versions(script_id, name, source, version_slug, version)\n" +
                                "    VALUES (?::uuid, ?, ?, ?, (SELECT coalesce(max(version), 0) FROM script_versions WHERE script_id = ?::uuid) + 1)",
                        id, name, source, versionSlug, id);
                return "redirect:/scripts/" + id.toString() + "/edit";

            case "delete":
                jdbcTemplate.update(
                        "DELETE FROM scripts WHERE author_id = ?::uuid AND id = ?::uuid",
                        user.getId(), id);
                return "redirect:/scripts";

            default:
                throw new ResponseStatusException(BAD_REQUEST, "Don't understand this action");
        }

    }

    private User authenticate(HttpSession session) {
        final String userId = (String) session.getAttribute("userId");
        if (userId == null) throw new ResponseStatusException(UNAUTHORIZED, "Not authorized");

        final User user = jdbcTemplate.queryForObject(
                "SELECT id, username FROM users WHERE id = ?::uuid",
                (rs, rowNum) -> new User(UUID.fromString(rs.getString("id")), rs.getString("username")),
                userId);
        if (user == null) throw new ResponseStatusException(UNAUTHORIZED, "Not authorized");

        return user;
    }
}
