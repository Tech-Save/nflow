// conf.js
exports.config = {
  baseUrl: 'http://localhost:9001',
  directConnect: true,
  specs: ['it/**/*.spec.js'],

  capabilities: {
    browserName: 'chrome'
  },

  jasmineNodeOpts: {
    onComplete: null,
    isVerbose: true,
    showColors: false,
    includeStackTrace: true,
    showTiming: true
  }
};
