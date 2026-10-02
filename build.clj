(ns build
  (:require [clojure.tools.build.api :as b]
            [shadow.cljs.devtools.api :as shadow]))

(def lib 'ccsdemo)
(def version "0.1.0")
(def class-dir "target/classes")
(def uber-file (format "target/%s-%s-standalone.jar" (name lib) version))

(defn clean [_]
  (println "** Cleaning prior build")
  (b/delete {:path "target"})
  (b/delete {:path "resources/public/js"}))

(defn compile-cljs [_]
  (println "** Compiling cljs")
  (shadow/release :ccsdemo))

(defn uber [_]
  (println "** Building uber jar")
  (clean nil)
  (compile-cljs nil)
  (let [basis (b/create-basis {:project "deps.edn"})]
    (b/copy-dir {:src-dirs ["src/clj" "resources"] :target-dir class-dir})
    (b/compile-clj {:basis basis :ns-compile '[ccsdemo.main] :class-dir class-dir})
    (b/uber {:class-dir class-dir :uber-file uber-file :basis basis :main 'ccsdemo.main}))
  (println "** Built" uber-file))
