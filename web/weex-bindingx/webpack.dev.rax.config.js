const webpack = require('webpack');
const WebpackDevServer = require('webpack-dev-server');
const RaxWebpackPlugin = require('rax-webpack-plugin');
const fs = require('fs');
const colors = require('chalk');

function getEntries() {
  let result = {};
  let dirs = fs.readdirSync(`${__dirname}/demo/rax`);
  dirs.forEach((dirName) => {
    let matched = dirName.match(/(.+)\.js/);
    if (matched && matched[1]) {
      result['demo/rax/'+ matched[1] + '.bundle'] = `./demo/rax/${matched[1]}.js`
    }
  });
  return result;
}

var config = {
  target: 'node',
  // devtool: '#inline-source-map',
  entry: getEntries(),
  output: {
    path: __dirname,
    filename: '[name].js'
  },
  plugins: [
    new webpack.DefinePlugin({
      'process.env.NODE_ENV': JSON.stringify('development'),
    }),
    new webpack.NoEmitOnErrorsPlugin(),
    new webpack.ProgressPlugin(),
    new RaxWebpackPlugin({
      frameworkComment: true,
      platforms: []
    }),
  ],
  module: {
    loaders: [{
      test: /\.jsx?$/,
      //exclude: /(node_modules|bower_components)/,
      loader: 'babel-loader', // 'babel-loader' is also a legal name to reference
      query: {
        presets: ['es2015', 'rax']
      }
    }]
  }
};

var compiler = webpack(config);
var server = new WebpackDevServer(compiler, {
  publicPath: config.output.publicPath,
  public: '0.0.0.0',
  disableHostCheck: true,
  stats: {
    colors: true,
    chunks: false,
    errorDetails: true,
  },
});


server.listen(9999, function() {
  console.log(colors.green('\n  Open http://localhost:9999/demo/ \n'));
});