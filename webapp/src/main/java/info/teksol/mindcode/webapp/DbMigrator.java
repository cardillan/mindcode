package info.teksol.mindcode.webapp;

import org.springframework.jdbc.core.JdbcTemplate;

public class DbMigrator {
    public static final String USER_ID = "37cc9dec-0644-44d2-8f90-c40c3030d386";

    void migrate(JdbcTemplate template) {
        template.execute("""
                CREATE TABLE IF NOT EXISTS public.sources (
                  id         uuid primary key,
                  source     text                     not null,
                  created_at timestamp with time zone not null default current_timestamp
                )""");

        template.execute("ALTER TABLE public.sources ALTER COLUMN id SET DEFAULT gen_random_uuid()");

        template.execute("""
                CREATE TABLE IF NOT EXISTS public.users (
                  id              uuid primary key                  default gen_random_uuid(),
                  username        text                     not null unique check (username ~ '^[a-zA-Z][A-Za-z0-9]+$'),
                  hashed_password text                     not null,
                  registered_at   timestamp with time zone not null default current_timestamp
                )""");

        template.execute("""
                CREATE TABLE IF NOT EXISTS public.scripts (
                  id             uuid primary key                  default gen_random_uuid(),
                  published      boolean                  not null default false,
                  name           text                     not null check(length(name) > 0),
                  source         text                     not null,
                  forked_from_id uuid                         null references public.scripts on update cascade on delete set null,
                  author_id      uuid                     not null references public.users on update cascade on delete cascade,
                  recorded_at    timestamp with time zone not null default current_timestamp
                )""");

        template.execute("""
                CREATE TABLE IF NOT EXISTS public.script_versions (
                  id           bigint unique            not null generated always as identity,
                  script_id    uuid                     not null references public.scripts on update cascade on delete cascade,
                  name         text                     not null,
                  source       text                     not null,
                  version      integer                  not null check (version > 0),
                  version_slug text                     not null,
                  committed_at timestamp with time zone not null default current_timestamp,
                  unique (script_id, version),
                  unique (version_slug)
                )""");

        template.execute("""
                CREATE TABLE IF NOT EXISTS public.collections (
                  id            uuid primary key         not null                  default gen_random_uuid(),
                  owner_id      uuid                     not null references public.users on update cascade on delete cascade,
                  name          text                     not null check (length(name) > 0),
                  registered_at timestamp with time zone not null                  default current_timestamp
                )""");

        template.execute("""
                CREATE TABLE IF NOT EXISTS public.collection_scripts (
                  id            bigint primary key       not null generated always as identity,
                  collection_id uuid                     not null references public.collections on update cascade on delete cascade,
                  script_id     uuid                     not null references public.scripts on update cascade on delete cascade,
                  added_at      timestamp with time zone not null default current_timestamp
                )""");

        template.execute("ALTER TABLE public.scripts DROP COLUMN IF EXISTS published");
        template.execute("ALTER TABLE public.script_versions ADD COLUMN IF NOT EXISTS published boolean not null default false");
    }
}
