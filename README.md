# CCSDemo

Demo project in Clojure/ClojureScript with deploy to GCP

The project uses CMS data on Hospices to create a simple sales tool to find potential leads

Search starts by state with ordering by aggregate data on spend and number of patients, then can proceed to hopsices in that state with filtering by description. Once a hopsice is pinned it can be used to find potential contacts using a generated Google search.

## Data

Data is not commited, needs to be downloaded from CMS and put in `data/files`. Due to complexity of `.xslx` multi-page format a `.csv` needs to be made from the relevant page. This can be done in Google Sheets. See comments at end of `ingest.clj` to manage ingestion.

```
clj -M:ingest
=> (i/ t/ h/)  # Use ingest.clj, tablecloth, and honey
=> (c/refresh) # Load file changes for interactive development
```

## Run

### Setup

Install npm binaries and generate css.

```
npm install -g shadow-cljs sass
sass --load-path=node_modules/@picocss/pico/scss src/scss/ccsdemo.scss resources/public/css/ccsdemo.css
```

### DB

Using psql a table needs to be setup to work with the API

```
psql
=# \c ccsdemo
=# CREATE TABLE hospice_pins (enroll CHAR(15) NOT NULL PRIMARY_KEY);
```

### Dev

```
cp config.dev.edn resources/config.edn
clj -M:dev                # start backend first, hot reload
shadow-cljs watch ccsdemo # frontend, hot reload
```

go to `localhost:3001/index.html`

### Prod

Tag docker image with version and update in `deployment.yaml`. Fill in values in `config.edn.prod`.

```
cp config.prod.edn resources/config.edn
clj -T:build uber # Can test with java -jar target/name.jar

docker buildx build --platform linux/amd64 -t us-central1-docker.pkg.dev/ccsdemo-510302/ccsdemo/app:version .
docker push us-central1-docker.pkg.dev/ccsdemo-510302/ccsdemo/app:version

kubectl apply -f deployment.yaml
kubectl get service ccsdemo-app-service
```

go to `http://EXTERNAL-IP/index.html`

To save costs delete images from repository and downscale nodes

```
gcloud artifacts docker images delete us-central1-docker.pkg.dev/ccsdemo-510302/ccsdemo/app
gcloud container clusters resize ccsdemo --num-nodes=0
```

## Time Tracking

01h - Frontend Routing  
01h - Frontend Styling  
04h - Data Ingest  
01h - Server API routes  
01h - Server Search  
01h - Search View  
02h - Generalizing API and Views  
01h - Pins API and View  
01h - Cleanup Code  
01h - Google contact feature  
02h - Deploy/Add Config

Total 14 hours + Initial project setup

## Rationale

This is an internal tool so a rough UI that allows for quick iteration is allowed. Additional features like contact managment and lead pipelining were scrapped so that existing tools that do that better can be used. There is a non-trivial data component to the project in addition to the 3 layers of Frontend, API, and DB. That along with the deployment to GCP shows my full stack skillset.

## Todo

* Deploy with DB and config
