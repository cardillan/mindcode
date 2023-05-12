package info.teksol.mindcode.webapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Controller
public class AuthenticationController {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PasswordAuthenticator passwordAuthenticator;

    @GetMapping("/register")
    public ModelAndView register() {
        return new ModelAndView(
                "register",
                "model",
                new AuthData("", "")
        );
    }

    @PostMapping("/register")
    public ModelAndView doRegister(@RequestParam("username") String username,
                                   @RequestParam("password") String password,
                                   HttpSession session) {
        final UUID id = UUID.randomUUID();

        final String hashedPassword = passwordAuthenticator.hash(password.toCharArray());
        jdbcTemplate.update(
                "INSERT INTO users(id, username, hashed_password) VALUES (?::uuid, ?, ?)",
                id, username, hashedPassword
        );

        session.setAttribute("userId", id.toString());
        return new ModelAndView("redirect:/scripts");
    }

    @GetMapping("/login")
    public ModelAndView login() {
        return new ModelAndView(
                "login",
                "model",
                new AuthData("", "")
        );
    }

    @PostMapping("/login")
    public ModelAndView doLogin(@RequestParam("username") String username,
                                @RequestParam("password") String password,
                                HttpSession session) {
        final Map<String, String> row = jdbcTemplate.queryForObject(
                "SELECT id, username, hashed_password FROM users WHERE username = ?",
                (rs, rowNum) -> Map.of(
                        "id", rs.getString("id"),
                        "username", rs.getString("username"),
                        "hashed_password", rs.getString("hashed_password")
                ),
                username);

        if (row == null) {
            return new ModelAndView(
                    "login",
                    "model",
                    new AuthData(username, null)
            );
        } else {
            if (passwordAuthenticator.authenticate(password.toCharArray(), row.get("hashed_password"))) {
                session.setAttribute("userId", row.get("id"));
                return new ModelAndView("redirect:/scripts");
            } else {
                return new ModelAndView(
                        "login",
                        "model",
                        new AuthData(username, null)
                );
            }
        }
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute("userId");
        session.invalidate();
        return "redirect:/";
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return errors;
    }
}
