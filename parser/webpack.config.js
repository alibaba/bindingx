var webpack = require('webpack');

module.exports = {
  plugins: [
  ],
  entry: {
    index: './src/index.js'
  },
  output: {
    path: __dirname + '/lib/',
    filename: '[name].js',
    libraryTarget:'umd'
  },
  devServer: {
    contentBase: "./demo",
    historyApiFallback: true,
    inline: true,
    publicPath: '/dist/'
  },
  module: {
    loaders: [
      {
        test: /\.css$/,
        loader: 'style-loader!css-loader'
      },
      // {
      //   test: /\.js$/,
      //   loader: 'jsx-loader?harmony'
      // },
      {
        test: /\.(png|jpg)$/,
        loader: 'url-loader?limit=8192'
      },
      {
        test: /\.jsx?$/,
        loader: 'babel-loader',
        exclude: /node_modules/,
        query: {
          presets: ['es2015','stage-0']
        }
      }
    ]
  }
};