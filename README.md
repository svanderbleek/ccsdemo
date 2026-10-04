# CCSDemo

Demo project in Clojure/ClojureScript with deploy to GCP

The project uses CMS data on Hospices to create a simple sales tool to find potential leads

Search starts by state with ordering by aggregate data on spend and number of patients, then can proceed to hopsices in that state with filtering by description. Once a hopsice is pinned it can be used to find potential contacts and track the state of the lead, such as Open/Won/Lost, and state of contacts, such as Found/Talking/Unreachable.

## Data

Data is not commited, needs to be downloaded from CMS and put in `data/files`. Due to complexity of `.xslx` multi-page format a `.csv` needs to be made from the relevant page. This can be done in Google Sheets. See comments at end of `ingest.clj` to manage ingestion.

```
clj -M:ingest
=> (i/ t/ h/)  # Use ingest.clj, tablecloth, and honey
=> (c/refresh) # Load file changes for interactive development
```

## Run

### Setup

Need some npm binaries to build

```
npm install -g shadow-cljs sass
```

### Dev

```
# only need to run once
sass --load-path=node_modules/@picocss/pico/scss src/scss/ccsdemo.scss resources/public/css/ccsdemo.css
clj -M:run                # start backend first
shadow-cljs watch ccsdemo # frontend
```

go to `localhost:3001/index.html`

### Prod

Tag docker image with version and update in `deployment.yaml`

```
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

1h - Frontend Routing  
1h - Frontend Styling  
4h - Data Ingest

## Todo

* Ingest CMS backend data
* Search View
* Search DB
* Search API
* Pins View
* Pins DB
* Pins API
* Lead Stages and Lead Contacts features
* Enable https (optional)
