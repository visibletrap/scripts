#!/usr/bin/env lumo

(ns me.brew-upgrade
  (:require [clojure.string :as str]
            [cljs.core :refer [*command-line-args*]]))

(when-not *command-line-args*
  (println "Usage: ./brew-upgrade.cljs <comma-sparated to-be-upgraded brew package names>")
  (println "Example: ./brew-upgrade.cljs clojure,git,tmux")
  (.exit js/process))

(def interested-packages
  (-> *command-line-args*
      first
      (str/split ",")
      set))

(def spawn-sync (.-spawnSync (js/require "child_process")))

(println "brew update ...")
(spawn-sync "brew" #js ["update"])

(println "brew outdated ...")
(def brew-outdated-result (.-stdout (spawn-sync "brew" #js ["outdated"])))

(def outdated-packages (str/split-lines brew-outdated-result))

(println "Outdated packages:" (into [] (sort outdated-packages)))

(def to-update-packages (filter interested-packages outdated-packages))

(if (empty? to-update-packages)
  (println "Nothing to upgrade")
  (do (println "To be upgraded:" to-update-packages)
      (println "brew upgrade ...")
      (spawn-sync "brew" (apply array "upgrade" "--cleanup" to-update-packages))))

(println "Done")
