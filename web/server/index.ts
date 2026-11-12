// Production server: serves the built application (dist/) and proxies grpc-web calls
// under /grpc to the gRPC server, with the fetch-style handler of grpc-web-middleware.
import { serve } from '@hono/node-server'
import { serveStatic } from '@hono/node-server/serve-static'
import { Hono } from 'hono'
import { logger } from 'hono/logger'
import { createGrpcWebHandler } from 'grpc-web-middleware'

const port = Number(process.env.PORT ?? 3000)
const grpcTarget = process.env.GRPC_TARGET ?? 'http://localhost:6565'

const grpcWeb = createGrpcWebHandler({ target: grpcTarget, prefix: '/grpc' })

const app = new Hono()
app.use(logger())
app.use('/grpc/*', async (c, next) => (await grpcWeb(c.req.raw)) ?? next())
app.use('/*', serveStatic({ root: './dist' }))

const server = serve({ fetch: app.fetch, port }, (info) => {
  console.log(`News web app on http://localhost:${info.port}, gRPC server ${grpcTarget}`)
})

for (const signal of ['SIGINT', 'SIGTERM']) {
  process.once(signal, () => {
    grpcWeb.close()
    server.close(() => process.exit(0))
    if ('closeAllConnections' in server) server.closeAllConnections() // end live subscriptions
  })
}
