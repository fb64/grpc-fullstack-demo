import { createClient } from '@connectrpc/connect'
import { createGrpcWebTransport } from '@connectrpc/connect-web'
import { NewsService } from '../gen/demo/news/v1/news_service_pb.ts'

/** NewsService client speaking grpc-web to the same origin; the server proxies /grpc to the gRPC server. */
export const newsClient = createClient(NewsService, createGrpcWebTransport({ baseUrl: '/grpc' }))
