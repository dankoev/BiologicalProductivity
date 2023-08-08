const path = require('path')
const HTMLWebpackPlug = require('html-webpack-plugin')
const MiniCssExtractPlugin = require("mini-css-extract-plugin");
const {SourceMapDevToolPlugin} = require("webpack");
const CopyWebpackPlugin = require('copy-webpack-plugin');

module.exports = {
  watch: true,
  entry: {
    main: "./src/main.js"
  },
  module: {
    rules: [
      {
        test: /\.html$/i,
        loader: 'html-loader',
      },
      {
        test: /\.css$/i,
        use: [MiniCssExtractPlugin.loader, 'css-loader'],
      },
      {
        test: /\.m?js$/,
        enforce: 'pre',
        use: ['source-map-loader'],
      }
    ],
  },
  output: {
    filename: 'bundle.js',
    path: path.resolve(__dirname, 'dist'),
  },
  plugins: [
    new HTMLWebpackPlug({
      template: "./src/index.html"
    }),
    new MiniCssExtractPlugin(),
    new SourceMapDevToolPlugin({
      filename: "[file].map"
    }),
    new CopyWebpackPlugin({
      patterns: [
        {
          from: './src/data',
          to: './data'
        },
        {
          from: './src/mapInfo',
          to: './mapInfo'
        }
      ]
    })
  ],
  externals: {
    ymaps: "ymaps",
  }
}