package fr.fbernard.newsapp.backend

import io.grpc.Server
import io.grpc.ServerBuilder
import sun.misc.Signal
import java.io.IOException

class NewsAppServer {

    companion object {
        @Throws(IOException::class, InterruptedException::class)
        @JvmStatic
        fun main(args: Array<String>) {
            val server = NewsAppServer()
            args.firstOrNull()?.toIntOrNull()?.let {
                server.start(it)
            } ?: server.start()
            server.blockUntilShutdown()
        }
    }

    private var server:Server?=null

    @Throws(IOException::class)
    private fun start(port:Int=6565) {
        Signal.handle(Signal("INT")) { System.exit(0) }
        server = ServerBuilder.forPort(port)
            .addService(NewsService())
            .build()
            .start()
        println("News GRPC server started, listening on $port")
        Runtime.getRuntime().addShutdownHook(object : Thread() {
            override fun run() {
                this@NewsAppServer.stop()
                println("News GRPC server shut down")
            }
        })
    }

    private fun stop() {
        server?.shutdown()
    }


    @Throws(InterruptedException::class)
    private fun blockUntilShutdown() {
        server?.awaitTermination()
    }

}