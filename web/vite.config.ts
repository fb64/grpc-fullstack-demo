import { svelte } from '@sveltejs/vite-plugin-svelte'
import { defineConfig } from 'vite'

const grpcTarget = process.env.GRPC_TARGET ?? 'http://localhost:6565'

export default defineConfig({
  plugins: [
    svelte(),
    {
      // In development, the Vite dev server itself proxies grpc-web calls to the gRPC server,
      // using the (req, res, next) flavour of grpc-web-middleware. Production uses server/index.ts.
      name: 'grpc-web-proxy',
      async configureServer(server) {
        const { grpcWebMiddleware } = await import('grpc-web-middleware')
        const middleware = grpcWebMiddleware({ target: grpcTarget, prefix: '/grpc' })
        server.middlewares.use((req, res, next) => middleware(req, res, next))
        server.httpServer?.once('close', () => middleware.close())
      },
    },
  ],
})
