package fr.fbernard.newsapp.backend

import fr.fbernard.grpc.demo.news.v1.NewsServiceGrpc
import fr.fbernard.newsapp.backend.data.NewsStore
import io.grpc.Grpc
import io.grpc.InsecureServerCredentials
import io.grpc.Server
import io.grpc.ServerBuilder
import io.grpc.health.v1.HealthCheckResponse.ServingStatus
import io.grpc.protobuf.services.HealthStatusManager
import io.grpc.protobuf.services.ProtoReflectionServiceV1
import java.util.concurrent.TimeUnit

const val DEFAULT_PORT = 6565

/** Port from the first argument, else the PORT environment variable, else [DEFAULT_PORT]. */
fun main(args: Array<String>) {
    val port = args.firstOrNull()?.toIntOrNull()
        ?: System.getenv("PORT")?.toIntOrNull()
        ?: DEFAULT_PORT

    val server = newsServer(Grpc.newServerBuilderForPort(port, InsecureServerCredentials.create())).start()
    println("News gRPC server listening on port ${server.port}")

    Runtime.getRuntime().addShutdownHook(
        Thread {
            server.shutdown()
            if (!server.awaitTermination(5, TimeUnit.SECONDS)) server.shutdownNow()
            println("News gRPC server stopped")
        },
    )
    server.awaitTermination()
}

/**
 * Builds the server with the news service, plus the standard health and reflection services
 * (so tools like `grpcurl` and `grpc_health_probe` work out of the box).
 */
fun newsServer(builder: ServerBuilder<*>, store: NewsStore = NewsStore()): Server {
    val health = HealthStatusManager()
    health.setStatus(NewsServiceGrpc.SERVICE_NAME, ServingStatus.SERVING)
    return builder
        .addService(NewsService(store))
        .addService(health.healthService)
        .addService(ProtoReflectionServiceV1.newInstance())
        .build()
}
