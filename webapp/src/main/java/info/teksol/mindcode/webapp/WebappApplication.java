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

        template.execute("CREATE TABLE IF NOT EXISTS public.users (\n" +
                "  id            uuid primary key default gen_random_uuid(),\n" +
                "  email_hash    text                     not null unique,\n" +
                "  password_hash text                     not null,\n" +
                "  registered_at timestamp with time zone not null default current_timestamp\n" +
                ")");

        template.execute("CREATE TABLE IF NOT EXISTS public.scripts (\n" +
                "  id             uuid primary key                  default gen_random_uuid(),\n" +
                "  forked_from_id uuid                     null references public.scripts on update cascade on delete set null,\n" +
                "  author_id      uuid                     not null references public.users on update cascade on delete cascade,\n" +
                "  recorded_at    timestamp with time zone not null default current_timestamp\n" +
                ")");

        template.execute("CREATE TABLE IF NOT EXISTS public.script_sources (\n" +
                "  id          bigint unique            not null generated always as identity,\n" +
                "  script_id   uuid                     not null references public.scripts on update cascade on delete cascade,\n" +
                "  version     integer                  not null check (version > 0),\n" +
                "  authored_at timestamp with time zone not null default current_timestamp,\n" +
                "  unique (script_id, version)\n" +
                ")");

        template.execute("CREATE TABLE IF NOT EXISTS public.collections (\n" +
                "  id            uuid primary key         not null                  default gen_random_uuid(),\n" +
                "  owner_id      uuid                     not null references public.users on update cascade on delete cascade,\n" +
                "  name          text                     not null check (length(name) > 0),\n" +
                "  registered_at timestamp with time zone not null                  default current_timestamp\n" +
                ")");

        template.execute("CREATE TABLE IF NOT EXISTS public.collection_scripts (\n" +
                "  id            bigint primary key       not null generated always as identity,\n" +
                "  collection_id uuid                     not null references public.collections on update cascade on delete cascade,\n" +
                "  script_id     uuid                     not null references public.scripts on update cascade on delete cascade,\n" +
                "  added_at      timestamp with time zone not null default current_timestamp\n" +
                ")");
    }
}
