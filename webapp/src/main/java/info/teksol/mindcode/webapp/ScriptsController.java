package info.teksol.mindcode.webapp;

import info.teksol.mindcode.Tuple2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;

import java.sql.ResultSet;
import java.util.List;
import java.util.UUID;

import static info.teksol.mindcode.webapp.CompilerFacade.compile;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Controller
@RequestMapping(value = "/scripts")
public class ScriptsController {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping
    public ModelAndView listScripts(@RequestParam(required = false, name = "q") String query) {
        if (query == null) query = "";
        query = query
                .replaceAll("%", "")
                .replaceFirst("^", "%")
                .replaceFirst("$", "%");
        final String username = jdbcTemplate.queryForObject("SELECT username FROM users WHERE id = ?::uuid",
                String.class,
                DbMigrator.USER_ID);
        final List<Script> scripts = jdbcTemplate.query(
                "SELECT scripts.id, name, published, recorded_at \n" +
                        "FROM scripts\n" +
                        "WHERE author_id = ?::uuid AND (name || source ilike ?)\n" +
                        "ORDER BY lower(name), recorded_at\n",
                (ResultSet rs, int rowNum) -> new Script(
                        UUID.fromString(rs.getString("id")),
                        rs.getString("name"),
                        null,
                        username,
                        rs.getBoolean("published"),
                        rs.getTimestamp("recorded_at").toInstant()
                ),
                DbMigrator.USER_ID, query
        );
        return new ModelAndView(
                "scripts/index",
                "model",
                new ListScriptData(scripts, query.replaceAll("%", ""))
        );
    }

    @GetMapping(value = "/new")
    public ModelAndView newScript() {
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
                         @RequestParam("script[source]") String source) {
        final UUID id = UUID.randomUUID();
        jdbcTemplate.update(
                "INSERT INTO public.scripts(id, name, author_id, source) VALUES (?::uuid, ?, ?::uuid, ?)",
                id.toString(), name, DbMigrator.USER_ID, source.replaceAll("\r\n", "\n"));
        return "redirect:/scripts/" + id.toString() + "/edit";
    }

    @GetMapping(value = "/{id}/edit")
    public ModelAndView editScript(@PathVariable(value = "id") UUID id) {
        final EditScriptData data = jdbcTemplate.queryForObject(
                "SELECT\n" +
                        "  scripts.name\n" +
                        ", scripts.source\n" +
                        "FROM scripts\n" +
                        "WHERE (author_id = ?::uuid OR published)\n" +
                        "  AND id = ?::uuid",
                (rs, rowNum) -> {
                    final String source = rs.getString("source");
                    final Tuple2<String, List<String>> compiled = compile(source);

                    return new EditScriptData(
                            id,
                            rs.getString("name"),
                            source,
                            source.split("\n").length,
                            compiled._1,
                            compiled._1.split("\n").length,
                            compiled._2,
                            List.of()
                    );
                },
                DbMigrator.USER_ID, id
        );

        if (data == null) throw new ResponseStatusException(NOT_FOUND, "No script with this ID");

        return new ModelAndView(
                "scripts/edit",
                "model",
                data
        );
    }

    @PostMapping("/{id}")
    public String putScript(@PathVariable(value = "id") UUID id,
                            @RequestParam("script[name]") String name,
                            @RequestParam("script[source]") String source,
                            @RequestParam("action") String action) {
        final int affectedRows = jdbcTemplate.update(
                "UPDATE scripts SET name = ?, source = ? \n" +
                        "WHERE author_id = ?::uuid AND id = ?::uuid",
                name, source, DbMigrator.USER_ID, id);
        if (affectedRows == 0) throw new ResponseStatusException(NOT_FOUND, "No script with this ID");
        return "redirect:/scripts/" + id.toString() + "/edit";
    }
}
