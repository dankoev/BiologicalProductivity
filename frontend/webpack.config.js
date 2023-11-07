import * as path from "path"
import HtmlWebpackPlugin from "html-webpack-plugin";
import MiniCssExtractPlugin from "mini-css-extract-plugin";
import  webpack  from "webpack";
import CopyPlugin from "copy-webpack-plugin";

const __dirname = path.resolve(".")
const isProduction = process.env.NODE_ENV === 'production';

export default {
  watch: isProduction ? false: true,
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
        resolve: {
          fullySpecified:false
        },
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
    new HtmlWebpackPlugin({
      template: "./src/index.html"
    }),
    new MiniCssExtractPlugin(),
    new webpack.SourceMapDevToolPlugin({
      filename: "[file].map"
    }),
    new CopyPlugin({
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
