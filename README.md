# CCSDemo

Demo project to develop proficiency with Clojure/ClojureScript

## Run

### Dev

```
clj -M:run                # start backend first
shadow-cljs watch ccsdemo # frontend
```

go to `localhost:3001/index.html`

### Prod

```
clj -T:build uber 
# Can test with java -jar target/name.jar
docker buildx build --platform linux/amd64 -t us-central1-docker.pkg.dev/ccsdemo-510302/ccsdemo/app:version .
# update version in deployment.yaml
docker push us-central1-docker.pkg.dev/ccsdemo-510302/ccsdemo/app:version
kubectl apply -f deployment.yaml
kubectl get service ccsdemo-app-service
```

go to `http://EXTERNAL-IP/index.html`

[running](http://35.239.99.168/index.html) on GCP

to save costs turn on and off with `num-nodes`

```
gcloud container clusters resize ccsdemo --num-nodes=0,1
```

## Todo

* Enable https
* Connect server to Postgres
* Display data in client
