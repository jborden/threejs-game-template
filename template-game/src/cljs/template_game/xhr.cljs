(ns template-game.xhr
  (:require [goog.net.XhrIo]))

(defn send-xhr
  "Send a xhr to url using callback and HTTP method."
  [url callback method & [data headers timeout]]
  (.send goog.net.XhrIo url callback method data headers timeout))

(defn xhrio-wrapper
  "A callback for processing the xhrio response event. If
  response.target.isSuccess() is true, call f on the json response"
  [f response]
  (let [target (.-target response)]
    (if (.isSuccess target)
      (f (.getResponseJson target))
      (.log js/console
            (str "xhrio-wrapper error:" (aget target "lastError_"))))))

(defn retrieve-url
  "Retrieve and process json response with f from url using HTTP method and json
  data. Optionally, define a timeout in ms."
  [url method data f & [timeout]]
  (let [header (clj->js {"Content-Type" "application/json"})]
    (send-xhr url f method data header timeout)))

(defn process-json
  "Take a response, convert it a clj map and call f on the resulting map."
  [f response]
  (f (js->clj response :keywordize-keys true)))

(defn process-json-response
  "Assuming the server will respond with JSON, convert the response to JSON
  and call f on it."
  [f]
  (partial xhrio-wrapper (partial process-json f)))
