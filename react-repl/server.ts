
import { createServer } from 'http'
import { parse } from 'url'
import next from 'next'

// refs:
// - https://github.com/vercel/next.js/blob/canary/examples/custom-server/server.ts
// - https://nextjs.org/docs/advanced-features/custom-server
// - https://github.com/websockets/ws

const port = parseInt(process.env.PORT || '3000', 10)
const dev = process.env.NODE_ENV !== 'production'
const app = next({ dev })
const handle = app.getRequestHandler()

app.prepare().then(() => {
    createServer((req, res) => {
        const parsedUrl = parse(req.url!, true)
        handle(req, res, parsedUrl)
    }).listen(port)

    console.log(
        `> Server listening at http://localhost:${port} as ${
            dev ? 'development' : process.env.NODE_ENV
        }`
    )
})
