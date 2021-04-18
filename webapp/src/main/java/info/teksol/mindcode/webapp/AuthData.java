package info.teksol.mindcode.webapp;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class AuthData {
    @NotBlank
    @Size(min = 3, max = 50)
    private final String username;

    @NotBlank
    @Size(min = 10, max = 200)
    private final String password;

    public AuthData(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
