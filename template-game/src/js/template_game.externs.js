// because cljsjs/three has its own non-exhaustive externs, any ones needed
// in addition to those at: https://github.com/cljsjs/packages/blob/master/three/resources/cljsjs/three/common/three.ext.js
// must be appened to the THREE object
THREE.Camera.lookAt = function () {};
THREE.Renderer.setSize = function () {};
THREE.Renderer.domElement = {};
THREE.Mesh.translateX = function () {};
THREE.Mesh.translateY = function () {};
