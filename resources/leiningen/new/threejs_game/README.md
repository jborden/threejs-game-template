
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

Point your browser to the port indicated on the command line.

