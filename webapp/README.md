# Mindcode webapp

The frontend for mindcode is built using [svelte kit](https://svelte.dev/docs/kit/introduction).

## Development (backend)

Look at the [contribution guide](../CONTRIBUTING.markdown).

As you will see in [deployment](#deployment), building the backend will also build the frontend, so you don't need to setup node if you only want to edit backend code.

To prevent the frontend from being built unecessarily, you may use `-DskipFrontend=true` on the maven command, or set the `SKIP_FRONTEND` environment variable when running the backend through docker.

## Development (frontend)

If you need to make changes to the frontend, run

```sh
cd webapp/frontend
npm run dev
```

This will open a development server on `http://localhost:5173`, which provides hot reload for the pages, and proxies request to `/api/**` to `http://localhost:8080/api/**` (the url where the mindcode webapp server runs during development, the mindcode server needs to be run separatedly).

## Deployment

When building the webapp, [`frontend-maven-plugin`](https://github.com/eirslett/frontend-maven-plugin) will be invoked to build the frontend.

`webapp/frontend/svelte.config.js` contains the frontend configuration, it is configured so that:
- The frontend is built as a collection of html pages with client-side routing
- The build output is placed on `webapp/src/main/resources/static`

> [!WARNING]
> If the `HEROKU` environment variable is present, most folders and files in the project will be deleted,
> preserving only `webapp/target/mindcode-webapp.jar` and a few configuration files.