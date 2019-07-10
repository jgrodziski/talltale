process.env.CHROMIUM_BIN = process.env.CHROMIUM_BIN || require('puppeteer').executablePath();
console.log('Chrome path: ' + process.env.CHROMIUM_BIN);

module.exports = function (config) {
    config.set({
        browsers: ['ChromiumHeadlessCustom'],
        customLaunchers: {
            ChromiumHeadlessCustom: {
                base: 'ChromiumHeadless',
                flags: [
                    '--no-sandbox',
                    '--disable-setuid-sandbox',
                    '--disable-gpu'
                ]
            }
        },
        basePath: '.',
        files: ['target/test.js'],
        frameworks: ['cljs-test'],
        plugins: ['karma-cljs-test', 'karma-chrome-launcher'],
        colors: true,
        logLevel: config.LOG_INFO,
        client: {
            args: ["shadow.test.karma.init"],
            singleRun: true
        }})}
