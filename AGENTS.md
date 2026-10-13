# Dev Guide

This is a Clojure server and ClojureScript frontend with Postgres database. The app is for finding sales leads using an ingested dataset on hospices in the US.

## Workflow

To support multiple Agents working at the same time follow these rules.

- Use git worktrees to make a branch for feature development.
- Update the `config.edn.dev` with an incremented port and change the dbname to avoid conflict with other developers. Then `cp config.edn.dev resources/config.edn` so that config is used by the server.
- Use `pg_dump -d ccsdemo --no-owner --no-privileges -f ccsdemo.sql` to export the db and `psql -d dbname -f ccsdemo.sql` to import the db.
- Run the server with `clj -M:dev`, it supports hot reloading of code so only needs to be run once.
- For the frontend, don't use `shadow-cljs watch ccsdemo`, instead use `shadow-cljs compile ccsdemo` to build each time changes are made. This is to avoid port conflicts with frontend hot reloading.
- Use `pnpm` for node dependencies.
- Once finished make a PR on github.
