# Dev Guide

This is a Clojure server and ClojureScript frontend with Postgres database. The app is an internal tool for finding sales leads using an ingested dataset on hospices in the US.

## Workflow

To support multiple Agents working at the same time follow these rules.

- Use git worktrees to make a branch for feature development.
- Update the `config.edn.dev` for the server with an open port in the 3000-4000 range. If modifying the database schema, also change database dbname to avoid conflict with other developers. To apply the config copy it with `cp config.edn.dev resources/config.edn`.
- If dbname was changed in the config and doesn't exist, create the db with `createdb dbname`. Use `pg_dump -d ccsdemo --no-owner --no-privileges -f ccsdemo.sql` to export the base db and `psql -d dbname -f ccsdemo.sql` to import into the new db.
- Run the server with `clj -M:dev`, it supports hot reloading of code so only needs to be run once.
- For the frontend, don't use `shadow-cljs watch ccsdemo`, instead use `shadow-cljs compile ccsdemo` to build each time changes are made. This is to avoid port conflicts with frontend hot reloading.
- The CSS is built with `sass --load-path=node_modules/@picocss/pico/scss src/scss/ccsdemo.scss resources/public/css/ccsdemo.css`. If changes are made and it needs to be rebuilt delete the CSS first with `rm resources/public/css/ccsdemo.css`.
- Use `pnpm` for node dependencies.
- Once finished make a PR on github.
