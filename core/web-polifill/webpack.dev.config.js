const fs = require('fs');

function getEntries() {
  let result = {};
  let dirs = fs.readdirSync(`${__dirname}/demo`);
  dirs.forEach((dirName) => {
    let matched = dirName.match(/(.+)\.js/);
    if (matched && matched[1]) {
      result[matched[1]] = `./demo/${matched[1]}.js`
    }
  });
  return result;
}


module.exports = {
  plugins: [],
  entry: getEntries(),
  output: {
    path: __dirname + '/dist/',
    filename: '[name].js'
  },
  devServer: {
    contentBase: "./demo",
    historyApiFallback: true,
    inline: true,
    public: '0.0.0.0',
    publicPath: '/dist/',
    port: 8088
  },
  module: {
    loaders: [
      {
        test: /\.css$/,
        loader: 'style-loader!css-loader'
      },
      {
        test: /\.(png|jpg)$/,
        loader: 'url-loader?limit=8192'
      },
      {
        test: /\.jsx?$/,
        loader: 'babel-loader',
        exclude: /node_modules/,
        query: {
          presets: ['es2015', 'stage-0']
        }
      }
    ]
  }
};