package fr.fbernard.grpc.demo.news.v1

import fr.fbernard.grpc.demo.news.v1.NewsServiceGrpc.getServiceDescriptor
import io.grpc.CallOptions
import io.grpc.CallOptions.DEFAULT
import io.grpc.Channel
import io.grpc.Metadata
import io.grpc.MethodDescriptor
import io.grpc.ServerServiceDefinition
import io.grpc.ServerServiceDefinition.builder
import io.grpc.ServiceDescriptor
import io.grpc.Status.UNIMPLEMENTED
import io.grpc.StatusException
import io.grpc.kotlin.AbstractCoroutineServerImpl
import io.grpc.kotlin.AbstractCoroutineStub
import io.grpc.kotlin.ClientCalls.serverStreamingRpc
import io.grpc.kotlin.ClientCalls.unaryRpc
import io.grpc.kotlin.ServerCalls.serverStreamingServerMethodDefinition
import io.grpc.kotlin.ServerCalls.unaryServerMethodDefinition
import io.grpc.kotlin.StubFor
import kotlin.String
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic
import kotlinx.coroutines.flow.Flow

/**
 * Holder for Kotlin coroutine-based client and server APIs for demo.news.v1.NewsService.
 */
public object NewsServiceGrpcKt {
  public const val SERVICE_NAME: String = NewsServiceGrpc.SERVICE_NAME

  @JvmStatic
  public val serviceDescriptor: ServiceDescriptor
    get() = getServiceDescriptor()

  public val getNewsMethod: MethodDescriptor<GetNewsRequest, GetNewsResponse>
    @JvmStatic
    get() = NewsServiceGrpc.getGetNewsMethod()

  public val subscribeMethod: MethodDescriptor<SubscribeRequest, SubscribeResponse>
    @JvmStatic
    get() = NewsServiceGrpc.getSubscribeMethod()

  public val postNewsMethod: MethodDescriptor<PostNewsRequest, PostNewsResponse>
    @JvmStatic
    get() = NewsServiceGrpc.getPostNewsMethod()

  /**
   * A stub for issuing RPCs to a(n) demo.news.v1.NewsService service as suspending coroutines.
   */
  @StubFor(NewsServiceGrpc::class)
  public class NewsServiceCoroutineStub @JvmOverloads constructor(
    channel: Channel,
    callOptions: CallOptions = DEFAULT,
  ) : AbstractCoroutineStub<NewsServiceCoroutineStub>(channel, callOptions) {
    override fun build(channel: Channel, callOptions: CallOptions): NewsServiceCoroutineStub = NewsServiceCoroutineStub(channel, callOptions)

    /**
     * Executes this RPC and returns the response message, suspending until the RPC completes
     * with [`Status.OK`][io.grpc.Status].  If the RPC completes with another status, a corresponding
     * [StatusException] is thrown.  If this coroutine is cancelled, the RPC is also cancelled
     * with the corresponding exception as a cause.
     *
     * @param request The request message to send to the server.
     *
     * @param headers Metadata to attach to the request.  Most users will not need this.
     *
     * @return The single response from the server.
     */
    public suspend fun getNews(request: GetNewsRequest, headers: Metadata = Metadata()): GetNewsResponse = unaryRpc(
      channel,
      NewsServiceGrpc.getGetNewsMethod(),
      request,
      callOptions,
      headers
    )

    /**
     * Returns a [Flow] that, when collected, executes this RPC and emits responses from the
     * server as they arrive.  That flow finishes normally if the server closes its response with
     * [`Status.OK`][io.grpc.Status], and fails by throwing a [StatusException] otherwise.  If
     * collecting the flow downstream fails exceptionally (including via cancellation), the RPC
     * is cancelled with that exception as a cause.
     *
     * @param request The request message to send to the server.
     *
     * @param headers Metadata to attach to the request.  Most users will not need this.
     *
     * @return A flow that, when collected, emits the responses from the server.
     */
    public fun subscribe(request: SubscribeRequest, headers: Metadata = Metadata()): Flow<SubscribeResponse> = serverStreamingRpc(
      channel,
      NewsServiceGrpc.getSubscribeMethod(),
      request,
      callOptions,
      headers
    )

    /**
     * Executes this RPC and returns the response message, suspending until the RPC completes
     * with [`Status.OK`][io.grpc.Status].  If the RPC completes with another status, a corresponding
     * [StatusException] is thrown.  If this coroutine is cancelled, the RPC is also cancelled
     * with the corresponding exception as a cause.
     *
     * @param request The request message to send to the server.
     *
     * @param headers Metadata to attach to the request.  Most users will not need this.
     *
     * @return The single response from the server.
     */
    public suspend fun postNews(request: PostNewsRequest, headers: Metadata = Metadata()): PostNewsResponse = unaryRpc(
      channel,
      NewsServiceGrpc.getPostNewsMethod(),
      request,
      callOptions,
      headers
    )
  }

  /**
   * Skeletal implementation of the demo.news.v1.NewsService service based on Kotlin coroutines.
   */
  public abstract class NewsServiceCoroutineImplBase(
    coroutineContext: CoroutineContext = EmptyCoroutineContext,
  ) : AbstractCoroutineServerImpl(coroutineContext) {
    /**
     * Returns the response to an RPC for demo.news.v1.NewsService.GetNews.
     *
     * If this method fails with a [StatusException], the RPC will fail with the corresponding
     * [io.grpc.Status].  If this method fails with a [java.util.concurrent.CancellationException], the RPC will fail
     * with status `Status.CANCELLED`.  If this method fails for any other reason, the RPC will
     * fail with `Status.UNKNOWN` with the exception as a cause.
     *
     * @param request The request from the client.
     */
    public open suspend fun getNews(request: GetNewsRequest): GetNewsResponse = throw StatusException(UNIMPLEMENTED.withDescription("Method demo.news.v1.NewsService.GetNews is unimplemented"))

    /**
     * Returns a [Flow] of responses to an RPC for demo.news.v1.NewsService.Subscribe.
     *
     * If creating or collecting the returned flow fails with a [StatusException], the RPC
     * will fail with the corresponding [io.grpc.Status].  If it fails with a
     * [java.util.concurrent.CancellationException], the RPC will fail with status `Status.CANCELLED`.  If creating
     * or collecting the returned flow fails for any other reason, the RPC will fail with
     * `Status.UNKNOWN` with the exception as a cause.
     *
     * @param request The request from the client.
     */
    public open fun subscribe(request: SubscribeRequest): Flow<SubscribeResponse> = throw StatusException(UNIMPLEMENTED.withDescription("Method demo.news.v1.NewsService.Subscribe is unimplemented"))

    /**
     * Returns the response to an RPC for demo.news.v1.NewsService.PostNews.
     *
     * If this method fails with a [StatusException], the RPC will fail with the corresponding
     * [io.grpc.Status].  If this method fails with a [java.util.concurrent.CancellationException], the RPC will fail
     * with status `Status.CANCELLED`.  If this method fails for any other reason, the RPC will
     * fail with `Status.UNKNOWN` with the exception as a cause.
     *
     * @param request The request from the client.
     */
    public open suspend fun postNews(request: PostNewsRequest): PostNewsResponse = throw StatusException(UNIMPLEMENTED.withDescription("Method demo.news.v1.NewsService.PostNews is unimplemented"))

    final override fun bindService(): ServerServiceDefinition = builder(getServiceDescriptor())
      .addMethod(unaryServerMethodDefinition(
      context = this.context,
      descriptor = NewsServiceGrpc.getGetNewsMethod(),
      implementation = ::getNews
    ))
      .addMethod(serverStreamingServerMethodDefinition(
      context = this.context,
      descriptor = NewsServiceGrpc.getSubscribeMethod(),
      implementation = ::subscribe
    ))
      .addMethod(unaryServerMethodDefinition(
      context = this.context,
      descriptor = NewsServiceGrpc.getPostNewsMethod(),
      implementation = ::postNews
    )).build()
  }
}
