
var fs = require('fs');
var webpack = require('webpack');


var bannerExcludeFiles = [];

var banner = '// { "framework": "Vue" }\n'

var bannerPlugin = new webpack.BannerPlugin(banner, {
  raw: true,
  exclude: bannerExcludeFiles
})

module.exports = {
  entry: "./js/src/index.js",
  output : {
    path: './js/build/',
    filename: '[name].js'
  },
  module: {
    loaders: [
      {
        test: /\.(we|vue)(\?[^?]+)?$/,
        loader: 'weex'
      },
      {
        test: /\.js(\?[^?]+)?$/,
        exclude: /node_modules/,
        loader: 'babel-loader?presets[]=es2015',
      },
      {
        test: /\.css(\?[^?]+)?$/,
        loader: 'style-loader!css-loader'
      }
    ]
  },
  plugins: [bannerPlugin]
}




