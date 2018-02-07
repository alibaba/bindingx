var path = require('path');
var fs = require('fs');
var webpack = require('webpack');

var entry = {};
var bannerExcludeFiles = [];

function walk(dir) {
  dir = dir || '.'
  var directory = path.join(__dirname, '../examples', dir);
  fs.readdirSync(directory)
      .forEach(function(file) {
        var fullpath = path.join(directory, file);
        var stat = fs.statSync(fullpath);
        var extname = path.extname(fullpath);
        if (stat.isFile() && (extname === '.we' || extname === '.vue')) {
          var name = path.join('examples', 'build', dir, path.basename(file, extname));
          entry[name] = fullpath + '?entry=true';
          if (extname === '.we') {
            bannerExcludeFiles.push(name + '.js')
          }
        } else if (stat.isDirectory() && file !== 'build' && file !== 'include') {
          var subdir = path.join(dir, file);
          walk(subdir);
        }
      });
}

walk();

var buildBrowserPlugin  = 'playground/browser/build/pluginInstall'
var  browserPlugin = path.join(__dirname,  '../playground/browser/pluginInstall.js')
entry[buildBrowserPlugin] =  browserPlugin + '?entry=true';


var banner = '// { "framework": "Vue" }\n'

var bannerPlugin = new webpack.BannerPlugin(banner, {
  raw: true,
  exclude: bannerExcludeFiles
})

module.exports = {
  entry: entry,
  output : {
    path: '.',
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
