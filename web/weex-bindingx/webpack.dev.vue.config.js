const webpack = require('webpack');
const WebpackDevServer = require('webpack-dev-server');
const pathTo = require('path');
const fs = require('fs-extra');
const colors = require('chalk');


let entry = {};
let weexEntry = {};

let fileType = '';

function walk(dir) {
  dir = dir || '.';
  const directory = pathTo.join(__dirname, 'demo/vue', dir);
  fs.readdirSync(directory)
    .forEach((file) => {
      const fullpath = pathTo.join(directory, file);
      const stat = fs.statSync(fullpath);
      const extname = pathTo.extname(fullpath);
      if (stat.isFile() && extname === '.vue') {
        if (!fileType) {
          fileType = extname;
        }
        const name = pathTo.join(dir, pathTo.basename(file, extname));
        entry['demo/vue/' + name + '.bundle'] = fullpath.replace(/\.vue/, '.js') + '?entry=true';
        weexEntry['demo/vue/' + name + '.bundle'] = fullpath + '?entry=true';
      } else if (stat.isDirectory() && file !== 'build' && file !== 'include') {
        const subdir = pathTo.join(dir, file);
        walk(subdir);
      }
    });
}

walk();


let webConfig = {
  // devtool: '#inline-source-map',
  entry,
  output: {
    path: __dirname,
    filename: '[name].js'
  },
  plugins: [
    new webpack.DefinePlugin({
      'process.env.NODE_ENV': JSON.stringify('development'),
    }),
    new webpack.NoEmitOnErrorsPlugin(),
    new webpack.ProgressPlugin()
  ],
  module: {
    loaders: [
      {
        test: /\.css$/,
        use: [
          'vue-style-loader',
          'css-loader'
        ],
      },
      {
        test: /\.vue$/,
        loader: 'vue-loader',
        options: {
          optimizeSSR: false,
          postcss: [
            // to convert weex exclusive styles.
            require('postcss-plugin-weex')(),
            require('autoprefixer')({
              browsers: ['> 0.1%', 'ios >= 8', 'not ie < 12']
            }),
            require('postcss-plugin-px2rem')({
              // base on 750px standard.
              rootValue: 75,
              // to leave 1px alone.
              minPixelValue: 1.01
            })
          ],
          compilerModules: [
            {
              postTransformNode: el => {
                // to convert vnode for weex components.
                require('weex-vue-precompiler')()(el)
              }
            }
          ]
        }
      },
      {
        test: /\.js$/,
        loader: 'babel-loader',
        exclude: /node_modules/
      },
      {
        test: /\.(png|jpg|gif|svg)$/,
        loader: 'file-loader',
        options: {
          name: '[name].[ext]?[hash]'
        }
      }]
  }
};

let weexConfig = {
  // devtool: '#inline-source-map',
  entry: weexEntry,
  // entry: getEntries(),
  output: {
    path: __dirname,
    filename: '[name].js'
  },
  plugins: [
    new webpack.DefinePlugin({
      'process.env.NODE_ENV': JSON.stringify('development'),
    }),
    new webpack.BannerPlugin({
      banner: '// { "framework": "Vue" }\n',
      raw: true,
      exclude: 'Vue'
    }),
    new webpack.NoEmitOnErrorsPlugin(),
    new webpack.ProgressPlugin()
  ],
  module: {
    loaders: [
      {
        test: /\.css$/,
        use: [
          'vue-style-loader',
          'css-loader'
        ],
      },
      {
        test: /\.vue(\?[^?]+)?$/,
        use: [{
          loader: 'weex-loader',
          options: {}
        }]
      },
      {
        test: /\.js$/,
        loader: 'babel-loader',
        exclude: /node_modules/
      },
      {
        test: /\.(png|jpg|gif|svg)$/,
        loader: 'file-loader',
        options: {
          name: '[name].[ext]?[hash]'
        }
      }]
  }
};

var webServer = new WebpackDevServer(webpack(webConfig), {
  publicPath: webConfig.output.publicPath,
  public: '0.0.0.0',
  disableHostCheck: true,
  stats: {
    colors: true,
    chunks: false,
    errorDetails: true,
  },
});


webServer.listen(9998, function () {
  console.log(colors.green('\n  Open http://localhost:9998/demo/ \n'));
});


let weexServer = new WebpackDevServer(webpack(weexConfig), {
  publicPath: weexConfig.output.publicPath,
  public: '0.0.0.0',
  disableHostCheck: true,
  stats: {
    colors: true,
    chunks: false,
    errorDetails: true,
  },
});


weexServer.listen(9997, function () {
  console.log(colors.green('\n  Open http://localhost:9997/demo/ \n'));
});