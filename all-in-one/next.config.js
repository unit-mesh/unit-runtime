const { InjectManifest, GenerateSW } = require("workbox-webpack-plugin");

/** @type {import('next').NextConfig} */
const nextConfig = {
  experimental: {
    appDir: true,
  },

  // https://github.com/vercel/next.js/issues/33863#issuecomment-1140518693
  webpack: (config, { isServer }) => {
    if (!isServer) {
      config.plugins.push(
        new InjectManifest({
          swSrc: "./sw.ts",
          swDest: "../public/sw.js",
          include: ["__nothing__"],
        })
      );

      config.plugins.push(
        new webpack.DefinePlugin({
          HOST: "http://localhost:9000",
          BASE_URL: "/hmr"
        })
      );
    }

    return config
  }
}

module.exports = nextConfig
