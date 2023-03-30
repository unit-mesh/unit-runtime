/** @type {import('next').NextConfig} */
const nextConfig = {
  experimental: {
    appDir: true,
  },

  // https://github.com/vercel/next.js/issues/33863#issuecomment-1140518693
  webpack: (config, { isServer }) => {
    if (!isServer) {

    }

    return config
  }
}

module.exports = nextConfig
