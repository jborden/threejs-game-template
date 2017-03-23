
# {{name}}

## Requirements

* JDK 1.8+ (figwheel will complain with JDK < 1.8, but will still work)
* Leiningen 2.7.1
* node.js 5.1.1 [This is done to match the verion of node.js being used in Electron v1.6.0]

On Mac/Linux, installing node.js using [Node Version Manager](https://github.com/creationix/nvm) is recommended.

## Initial setup

Compile the development version of the application:

```bash
$ lein cljsbuild once dev
```

Run the node server:
```bash
$ node server.js
```

Point your browser to the url indicated on the command line.

## Figwheel

Figwheel can be used to run the project and to provide a repl

```bash
$ rlwrap lein figwheel
```

After figwheel compiles the code, run the node server in another terminal and visit the url indicated on the command line.
The fighweel repl will connect after the url is loaded in the browser.

The file src/cljs/dev.cljs contains on-jsload function that figwheel calls each time it detects a file change in src/cljs.

