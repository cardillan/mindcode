package info.teksol.mindcode.webapp;

import org.springframework.jdbc.core.JdbcTemplate;

public class DbMigrator {
    public static final String USER_ID = "37cc9dec-0644-44d2-8f90-c40c3030d386";

    void migrate(JdbcTemplate template) {
        template.execute("CREATE TABLE IF NOT EXISTS public.sources (\n" +
                "  id         uuid primary key,\n" +
                "  source     text                     not null,\n" +
                "  created_at timestamp with time zone not null default current_timestamp\n" +
                ")  ");

        template.execute("ALTER TABLE public.sources ALTER COLUMN id SET DEFAULT gen_random_uuid()");

        template.execute("CREATE TABLE IF NOT EXISTS public.users (\n" +
                "  id              uuid primary key                  default gen_random_uuid(),\n" +
                "  username        text                     not null unique check (username ~ '^[a-zA-Z][A-Za-z0-9]+$'),\n" +
                "  hashed_password text                     not null,\n" +
                "  registered_at   timestamp with time zone not null default current_timestamp\n" +
                ")");

        template.execute("CREATE TABLE IF NOT EXISTS public.scripts (\n" +
                "  id             uuid primary key                  default gen_random_uuid(),\n" +
                "  published      boolean                  not null default false,\n" +
                "  name           text                     not null check(length(name) > 0),\n" +
                "  source         text                     not null,\n" +
                "  forked_from_id uuid                         null references public.scripts on update cascade on delete set null,\n" +
                "  author_id      uuid                     not null references public.users on update cascade on delete cascade,\n" +
                "  recorded_at    timestamp with time zone not null default current_timestamp\n" +
                ")");

        template.execute("CREATE TABLE IF NOT EXISTS public.script_versions (\n" +
                "  id           bigint unique            not null generated always as identity,\n" +
                "  script_id    uuid                     not null references public.scripts on update cascade on delete cascade,\n" +
                "  name         text                     not null,\n" +
                "  source       text                     not null,\n" +
                "  version      integer                  not null check (version > 0),\n" +
                "  version_slug text                     not null,\n" +
                "  committed_at timestamp with time zone not null default current_timestamp,\n" +
                "  unique (script_id, version),\n" +
                "  unique (version_slug)\n" +
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

        template.execute("ALTER TABLE public.scripts DROP COLUMN IF EXISTS published");
        template.execute("ALTER TABLE public.script_versions ADD COLUMN IF NOT EXISTS published boolean not null default false");
    }
}
