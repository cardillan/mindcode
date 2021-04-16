package info.teksol.mindcode.webapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootApplication
public class WebappApplication implements CommandLineRunner {

    @Autowired
    private JdbcTemplate template;

    public static void main(String[] args) {
        SpringApplication.run(WebappApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        template.execute("CREATE TABLE IF NOT EXISTS public.sources (\n" +
                "  id         uuid primary key,\n" +
                "  source     text                     not null,\n" +
                "  created_at timestamp with time zone not null default current_timestamp\n" +
                ")  ");

        template.execute("ALTER TABLE public.sources ALTER COLUMN id SET DEFAULT gen_random_uuid()");
    }
}
