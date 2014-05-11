(function (appDir, baseUrl, dir, paths, buildWriter) {

  paths['routes'] = 'empty:';

  return {
    appDir: appDir,
    baseUrl: baseUrl,
    dir: dir,
    generateSourceMaps: true,
    mainConfigFile: appDir + "/" + baseUrl + "/shop.js",
    modules: [
      {
        name: "shop"
      }
    ],
    onBuildWrite: buildWriter,
    optimize: "uglify2",
    paths: paths,
    preserveLicenseComments: false
  }
}(undefined, undefined, undefined, undefined, undefined))