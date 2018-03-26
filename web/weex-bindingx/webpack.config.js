const webpack = require('webpack');
const RaxPlugin = require('rax-webpack-plugin');
// const PlatformLoader = require('rax-webpack-plugin/lib/PlatformLoader');

let indexConfig = {
  plugins: [
    new webpack.ProgressPlugin(),
    new RaxPlugin({
      // Target format: `bundle`, `cmd`, `umd` or `factory`(build for builtin module format), default is umd
      target: 'umd',
      // Only for `bundle` target, default is '// {"framework" : "Rax"}'
      // frameworkComment: '// {"framework" : "Rax"}',
      // component mode build config
      // moduleName: 'rax',
      // globalName: 'Rax',
      // Enable external builtin modules, default is false
      // externalBuiltinModules: false,
      // Config which builtin modules should external, default config is define in `RaxPlugin.BuiltinModules`
      // builtinModules: RaxPlugin.BuiltinModules,
      // Enable include polyfill files
      // includePolyfills: false,
      // Config which polyfill should include, defaut is empty
      // polyfillModules: [],
      // Check duplicate dependencies, default is ['rax']
      // duplicateCheck: ['rax'],
    })
  ],
  entry: {
    index: './src/index.js'
  },
  output: {
    path: __dirname + '/lib/',
    filename: '[name].js'
  },
  module: {
    rules: [
      {
        test: /\.js$/,
        use: {
          loader: 'babel-loader',
          options: {
            presets: ['es2015', 'rax']
          }
        },
        exclude: /node_modules/,
      }
    ]
  }
};


let weexConfig = {
  plugins: [
    new webpack.ProgressPlugin(),
    new RaxPlugin({
      target: 'umd'
    })
  ],
  entry: {
    'index.weex': './src/index.js',
    'index.native': './src/index.js'
  },
  output: {
    path: __dirname + '/lib/',
    filename: '[name].js'
  },
  module: {
    rules: [
      {
        test: /\.js$/,
        use: {
          loader: 'babel-loader',
          options: {
            presets: ['es2015', 'rax'],
            plugins: [require('babel-plugin-minify-dead-code-elimination')]
          }
        },
        exclude: /node_modules/,
      },
      {
        enforce: 'pre',
        test: /\.js$/,
        exclude: /node_modules/,
        use: {
          loader: require.resolve('rax-webpack-plugin/lib/PlatformLoader') + '?platform=weex&name[]=universal-env'
        }
      }
    ]
  }
};

let webConfig = {
  plugins: [
    new webpack.ProgressPlugin(),
    new RaxPlugin({
      target: 'umd'
    })
  ],
  entry: {
    index: './src/index.js'
  },
  output: {
    path: __dirname + '/lib/',
    filename: '[name].web.js'
  },
  module: {
    rules: [
      {
        test: /\.js$/,
        use: {
          loader: 'babel-loader',
          options: {
            presets: ['es2015', 'rax'],
            plugins: [require('babel-plugin-minify-dead-code-elimination')]
          }
        },
        exclude: /node_modules/,
      },
      {
        enforce: 'pre',
        test: /\.js$/,
        exclude: /node_modules/,
        use: {
          loader: require.resolve('rax-webpack-plugin/lib/PlatformLoader') + '?platform=web&name[]=universal-env'
        }
      }
    ]
  }
};


const compiler = webpack([indexConfig, webConfig, weexConfig]);

compiler.run((err, state) => {
  if (err) {
    console.error('compile err:', err)
  }
});

