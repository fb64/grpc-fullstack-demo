package fr.fbernard.grpc.demo.news.v1;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 * <pre>
 * NewsService lets clients read, publish and follow news by topic.
 * </pre>
 */
@io.grpc.stub.annotations.GrpcGenerated
public final class NewsServiceGrpc {

  private NewsServiceGrpc() {}

  public static final java.lang.String SERVICE_NAME = "demo.news.v1.NewsService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<fr.fbernard.grpc.demo.news.v1.GetNewsRequest,
      fr.fbernard.grpc.demo.news.v1.GetNewsResponse> getGetNewsMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetNews",
      requestType = fr.fbernard.grpc.demo.news.v1.GetNewsRequest.class,
      responseType = fr.fbernard.grpc.demo.news.v1.GetNewsResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<fr.fbernard.grpc.demo.news.v1.GetNewsRequest,
      fr.fbernard.grpc.demo.news.v1.GetNewsResponse> getGetNewsMethod() {
    io.grpc.MethodDescriptor<fr.fbernard.grpc.demo.news.v1.GetNewsRequest, fr.fbernard.grpc.demo.news.v1.GetNewsResponse> getGetNewsMethod;
    if ((getGetNewsMethod = NewsServiceGrpc.getGetNewsMethod) == null) {
      synchronized (NewsServiceGrpc.class) {
        if ((getGetNewsMethod = NewsServiceGrpc.getGetNewsMethod) == null) {
          NewsServiceGrpc.getGetNewsMethod = getGetNewsMethod =
              io.grpc.MethodDescriptor.<fr.fbernard.grpc.demo.news.v1.GetNewsRequest, fr.fbernard.grpc.demo.news.v1.GetNewsResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetNews"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.lite.ProtoLiteUtils.marshaller(
                  fr.fbernard.grpc.demo.news.v1.GetNewsRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.lite.ProtoLiteUtils.marshaller(
                  fr.fbernard.grpc.demo.news.v1.GetNewsResponse.getDefaultInstance()))
              .build();
        }
      }
    }
    return getGetNewsMethod;
  }

  private static volatile io.grpc.MethodDescriptor<fr.fbernard.grpc.demo.news.v1.SubscribeRequest,
      fr.fbernard.grpc.demo.news.v1.SubscribeResponse> getSubscribeMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Subscribe",
      requestType = fr.fbernard.grpc.demo.news.v1.SubscribeRequest.class,
      responseType = fr.fbernard.grpc.demo.news.v1.SubscribeResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
  public static io.grpc.MethodDescriptor<fr.fbernard.grpc.demo.news.v1.SubscribeRequest,
      fr.fbernard.grpc.demo.news.v1.SubscribeResponse> getSubscribeMethod() {
    io.grpc.MethodDescriptor<fr.fbernard.grpc.demo.news.v1.SubscribeRequest, fr.fbernard.grpc.demo.news.v1.SubscribeResponse> getSubscribeMethod;
    if ((getSubscribeMethod = NewsServiceGrpc.getSubscribeMethod) == null) {
      synchronized (NewsServiceGrpc.class) {
        if ((getSubscribeMethod = NewsServiceGrpc.getSubscribeMethod) == null) {
          NewsServiceGrpc.getSubscribeMethod = getSubscribeMethod =
              io.grpc.MethodDescriptor.<fr.fbernard.grpc.demo.news.v1.SubscribeRequest, fr.fbernard.grpc.demo.news.v1.SubscribeResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "Subscribe"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.lite.ProtoLiteUtils.marshaller(
                  fr.fbernard.grpc.demo.news.v1.SubscribeRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.lite.ProtoLiteUtils.marshaller(
                  fr.fbernard.grpc.demo.news.v1.SubscribeResponse.getDefaultInstance()))
              .build();
        }
      }
    }
    return getSubscribeMethod;
  }

  private static volatile io.grpc.MethodDescriptor<fr.fbernard.grpc.demo.news.v1.PostNewsRequest,
      fr.fbernard.grpc.demo.news.v1.PostNewsResponse> getPostNewsMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "PostNews",
      requestType = fr.fbernard.grpc.demo.news.v1.PostNewsRequest.class,
      responseType = fr.fbernard.grpc.demo.news.v1.PostNewsResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<fr.fbernard.grpc.demo.news.v1.PostNewsRequest,
      fr.fbernard.grpc.demo.news.v1.PostNewsResponse> getPostNewsMethod() {
    io.grpc.MethodDescriptor<fr.fbernard.grpc.demo.news.v1.PostNewsRequest, fr.fbernard.grpc.demo.news.v1.PostNewsResponse> getPostNewsMethod;
    if ((getPostNewsMethod = NewsServiceGrpc.getPostNewsMethod) == null) {
      synchronized (NewsServiceGrpc.class) {
        if ((getPostNewsMethod = NewsServiceGrpc.getPostNewsMethod) == null) {
          NewsServiceGrpc.getPostNewsMethod = getPostNewsMethod =
              io.grpc.MethodDescriptor.<fr.fbernard.grpc.demo.news.v1.PostNewsRequest, fr.fbernard.grpc.demo.news.v1.PostNewsResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "PostNews"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.lite.ProtoLiteUtils.marshaller(
                  fr.fbernard.grpc.demo.news.v1.PostNewsRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.lite.ProtoLiteUtils.marshaller(
                  fr.fbernard.grpc.demo.news.v1.PostNewsResponse.getDefaultInstance()))
              .build();
        }
      }
    }
    return getPostNewsMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static NewsServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<NewsServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<NewsServiceStub>() {
        @java.lang.Override
        public NewsServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new NewsServiceStub(channel, callOptions);
        }
      };
    return NewsServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports all types of calls on the service
   */
  public static NewsServiceBlockingV2Stub newBlockingV2Stub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<NewsServiceBlockingV2Stub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<NewsServiceBlockingV2Stub>() {
        @java.lang.Override
        public NewsServiceBlockingV2Stub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new NewsServiceBlockingV2Stub(channel, callOptions);
        }
      };
    return NewsServiceBlockingV2Stub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static NewsServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<NewsServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<NewsServiceBlockingStub>() {
        @java.lang.Override
        public NewsServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new NewsServiceBlockingStub(channel, callOptions);
        }
      };
    return NewsServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static NewsServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<NewsServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<NewsServiceFutureStub>() {
        @java.lang.Override
        public NewsServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new NewsServiceFutureStub(channel, callOptions);
        }
      };
    return NewsServiceFutureStub.newStub(factory, channel);
  }

  /**
   * <pre>
   * NewsService lets clients read, publish and follow news by topic.
   * </pre>
   */
  public interface AsyncService {

    /**
     * <pre>
     * GetNews returns every news item stored for a topic.
     * </pre>
     */
    default void getNews(fr.fbernard.grpc.demo.news.v1.GetNewsRequest request,
        io.grpc.stub.StreamObserver<fr.fbernard.grpc.demo.news.v1.GetNewsResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetNewsMethod(), responseObserver);
    }

    /**
     * <pre>
     * Subscribe streams news items as they are published for a topic.
     * </pre>
     */
    default void subscribe(fr.fbernard.grpc.demo.news.v1.SubscribeRequest request,
        io.grpc.stub.StreamObserver<fr.fbernard.grpc.demo.news.v1.SubscribeResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getSubscribeMethod(), responseObserver);
    }

    /**
     * <pre>
     * PostNews publishes a news item and returns it with its server-assigned id.
     * </pre>
     */
    default void postNews(fr.fbernard.grpc.demo.news.v1.PostNewsRequest request,
        io.grpc.stub.StreamObserver<fr.fbernard.grpc.demo.news.v1.PostNewsResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getPostNewsMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service NewsService.
   * <pre>
   * NewsService lets clients read, publish and follow news by topic.
   * </pre>
   */
  public static abstract class NewsServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return NewsServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service NewsService.
   * <pre>
   * NewsService lets clients read, publish and follow news by topic.
   * </pre>
   */
  public static final class NewsServiceStub
      extends io.grpc.stub.AbstractAsyncStub<NewsServiceStub> {
    private NewsServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected NewsServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new NewsServiceStub(channel, callOptions);
    }

    /**
     * <pre>
     * GetNews returns every news item stored for a topic.
     * </pre>
     */
    public void getNews(fr.fbernard.grpc.demo.news.v1.GetNewsRequest request,
        io.grpc.stub.StreamObserver<fr.fbernard.grpc.demo.news.v1.GetNewsResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetNewsMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Subscribe streams news items as they are published for a topic.
     * </pre>
     */
    public void subscribe(fr.fbernard.grpc.demo.news.v1.SubscribeRequest request,
        io.grpc.stub.StreamObserver<fr.fbernard.grpc.demo.news.v1.SubscribeResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncServerStreamingCall(
          getChannel().newCall(getSubscribeMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * PostNews publishes a news item and returns it with its server-assigned id.
     * </pre>
     */
    public void postNews(fr.fbernard.grpc.demo.news.v1.PostNewsRequest request,
        io.grpc.stub.StreamObserver<fr.fbernard.grpc.demo.news.v1.PostNewsResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getPostNewsMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service NewsService.
   * <pre>
   * NewsService lets clients read, publish and follow news by topic.
   * </pre>
   */
  public static final class NewsServiceBlockingV2Stub
      extends io.grpc.stub.AbstractBlockingStub<NewsServiceBlockingV2Stub> {
    private NewsServiceBlockingV2Stub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected NewsServiceBlockingV2Stub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new NewsServiceBlockingV2Stub(channel, callOptions);
    }

    /**
     * <pre>
     * GetNews returns every news item stored for a topic.
     * </pre>
     */
    public fr.fbernard.grpc.demo.news.v1.GetNewsResponse getNews(fr.fbernard.grpc.demo.news.v1.GetNewsRequest request) throws io.grpc.StatusException {
      return io.grpc.stub.ClientCalls.blockingV2UnaryCall(
          getChannel(), getGetNewsMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Subscribe streams news items as they are published for a topic.
     * </pre>
     */
    @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/10918")
    public io.grpc.stub.BlockingClientCall<?, fr.fbernard.grpc.demo.news.v1.SubscribeResponse>
        subscribe(fr.fbernard.grpc.demo.news.v1.SubscribeRequest request) {
      return io.grpc.stub.ClientCalls.blockingV2ServerStreamingCall(
          getChannel(), getSubscribeMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * PostNews publishes a news item and returns it with its server-assigned id.
     * </pre>
     */
    public fr.fbernard.grpc.demo.news.v1.PostNewsResponse postNews(fr.fbernard.grpc.demo.news.v1.PostNewsRequest request) throws io.grpc.StatusException {
      return io.grpc.stub.ClientCalls.blockingV2UnaryCall(
          getChannel(), getPostNewsMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do limited synchronous rpc calls to service NewsService.
   * <pre>
   * NewsService lets clients read, publish and follow news by topic.
   * </pre>
   */
  public static final class NewsServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<NewsServiceBlockingStub> {
    private NewsServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected NewsServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new NewsServiceBlockingStub(channel, callOptions);
    }

    /**
     * <pre>
     * GetNews returns every news item stored for a topic.
     * </pre>
     */
    public fr.fbernard.grpc.demo.news.v1.GetNewsResponse getNews(fr.fbernard.grpc.demo.news.v1.GetNewsRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetNewsMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Subscribe streams news items as they are published for a topic.
     * </pre>
     */
    public java.util.Iterator<fr.fbernard.grpc.demo.news.v1.SubscribeResponse> subscribe(
        fr.fbernard.grpc.demo.news.v1.SubscribeRequest request) {
      return io.grpc.stub.ClientCalls.blockingServerStreamingCall(
          getChannel(), getSubscribeMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * PostNews publishes a news item and returns it with its server-assigned id.
     * </pre>
     */
    public fr.fbernard.grpc.demo.news.v1.PostNewsResponse postNews(fr.fbernard.grpc.demo.news.v1.PostNewsRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getPostNewsMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service NewsService.
   * <pre>
   * NewsService lets clients read, publish and follow news by topic.
   * </pre>
   */
  public static final class NewsServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<NewsServiceFutureStub> {
    private NewsServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected NewsServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new NewsServiceFutureStub(channel, callOptions);
    }

    /**
     * <pre>
     * GetNews returns every news item stored for a topic.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<fr.fbernard.grpc.demo.news.v1.GetNewsResponse> getNews(
        fr.fbernard.grpc.demo.news.v1.GetNewsRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetNewsMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * PostNews publishes a news item and returns it with its server-assigned id.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<fr.fbernard.grpc.demo.news.v1.PostNewsResponse> postNews(
        fr.fbernard.grpc.demo.news.v1.PostNewsRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getPostNewsMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_GET_NEWS = 0;
  private static final int METHODID_SUBSCRIBE = 1;
  private static final int METHODID_POST_NEWS = 2;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final AsyncService serviceImpl;
    private final int methodId;

    MethodHandlers(AsyncService serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_GET_NEWS:
          serviceImpl.getNews((fr.fbernard.grpc.demo.news.v1.GetNewsRequest) request,
              (io.grpc.stub.StreamObserver<fr.fbernard.grpc.demo.news.v1.GetNewsResponse>) responseObserver);
          break;
        case METHODID_SUBSCRIBE:
          serviceImpl.subscribe((fr.fbernard.grpc.demo.news.v1.SubscribeRequest) request,
              (io.grpc.stub.StreamObserver<fr.fbernard.grpc.demo.news.v1.SubscribeResponse>) responseObserver);
          break;
        case METHODID_POST_NEWS:
          serviceImpl.postNews((fr.fbernard.grpc.demo.news.v1.PostNewsRequest) request,
              (io.grpc.stub.StreamObserver<fr.fbernard.grpc.demo.news.v1.PostNewsResponse>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  public static final io.grpc.ServerServiceDefinition bindService(AsyncService service) {
    return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
        .addMethod(
          getGetNewsMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              fr.fbernard.grpc.demo.news.v1.GetNewsRequest,
              fr.fbernard.grpc.demo.news.v1.GetNewsResponse>(
                service, METHODID_GET_NEWS)))
        .addMethod(
          getSubscribeMethod(),
          io.grpc.stub.ServerCalls.asyncServerStreamingCall(
            new MethodHandlers<
              fr.fbernard.grpc.demo.news.v1.SubscribeRequest,
              fr.fbernard.grpc.demo.news.v1.SubscribeResponse>(
                service, METHODID_SUBSCRIBE)))
        .addMethod(
          getPostNewsMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              fr.fbernard.grpc.demo.news.v1.PostNewsRequest,
              fr.fbernard.grpc.demo.news.v1.PostNewsResponse>(
                service, METHODID_POST_NEWS)))
        .build();
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (NewsServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .addMethod(getGetNewsMethod())
              .addMethod(getSubscribeMethod())
              .addMethod(getPostNewsMethod())
              .build();
        }
      }
    }
    return result;
  }
}
