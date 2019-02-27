/**
 * @fileoverview gRPC-Web generated client stub for demo
 * @enhanceable
 * @public
 */

// GENERATED CODE -- DO NOT EDIT!



const grpc = {};
grpc.web = require('grpc-web');

const proto = {};
proto.demo = require('./news_service_pb.js');

/**
 * @param {string} hostname
 * @param {?Object} credentials
 * @param {?Object} options
 * @constructor
 * @struct
 * @final
 */
proto.demo.NewsServiceClient =
    function(hostname, credentials, options) {
  if (!options) options = {};
  options['format'] = 'text';

  /**
   * @private @const {!grpc.web.GrpcWebClientBase} The client
   */
  this.client_ = new grpc.web.GrpcWebClientBase(options);

  /**
   * @private @const {string} The hostname
   */
  this.hostname_ = hostname;

  /**
   * @private @const {?Object} The credentials to be used to connect
   *    to the server
   */
  this.credentials_ = credentials;

  /**
   * @private @const {?Object} Options for the client
   */
  this.options_ = options;
};


/**
 * @param {string} hostname
 * @param {?Object} credentials
 * @param {?Object} options
 * @constructor
 * @struct
 * @final
 */
proto.demo.NewsServicePromiseClient =
    function(hostname, credentials, options) {
  if (!options) options = {};
  options['format'] = 'text';

  /**
   * @private @const {!proto.demo.NewsServiceClient} The delegate callback based client
   */
  this.delegateClient_ = new proto.demo.NewsServiceClient(
      hostname, credentials, options);

};


/**
 * @const
 * @type {!grpc.web.AbstractClientBase.MethodInfo<
 *   !proto.demo.NewsRequest,
 *   !proto.demo.NewsResponse>}
 */
const methodInfo_getNews = new grpc.web.AbstractClientBase.MethodInfo(
  proto.demo.NewsResponse,
  /** @param {!proto.demo.NewsRequest} request */
  function(request) {
    return request.serializeBinary();
  },
  proto.demo.NewsResponse.deserializeBinary
);


/**
 * @param {!proto.demo.NewsRequest} request The
 *     request proto
 * @param {!Object<string, string>} metadata User defined
 *     call metadata
 * @param {function(?grpc.web.Error, ?proto.demo.NewsResponse)}
 *     callback The callback function(error, response)
 * @return {!grpc.web.ClientReadableStream<!proto.demo.NewsResponse>|undefined}
 *     The XHR Node Readable Stream
 */
proto.demo.NewsServiceClient.prototype.getNews =
    function(request, metadata, callback) {
  return this.client_.rpcCall(this.hostname_ +
      '/demo.NewsService/getNews',
      request,
      metadata,
      methodInfo_getNews,
      callback);
};


/**
 * @param {!proto.demo.NewsRequest} request The
 *     request proto
 * @param {!Object<string, string>} metadata User defined
 *     call metadata
 * @return {!Promise<!proto.demo.NewsResponse>}
 *     The XHR Node Readable Stream
 */
proto.demo.NewsServicePromiseClient.prototype.getNews =
    function(request, metadata) {
  return new Promise((resolve, reject) => {
    this.delegateClient_.getNews(
      request, metadata, (error, response) => {
        error ? reject(error) : resolve(response);
      });
  });
};


/**
 * @const
 * @type {!grpc.web.AbstractClientBase.MethodInfo<
 *   !proto.demo.SubscribeRequest,
 *   !proto.demo.News>}
 */
const methodInfo_subscribe = new grpc.web.AbstractClientBase.MethodInfo(
  proto.demo.News,
  /** @param {!proto.demo.SubscribeRequest} request */
  function(request) {
    return request.serializeBinary();
  },
  proto.demo.News.deserializeBinary
);


/**
 * @param {!proto.demo.SubscribeRequest} request The request proto
 * @param {!Object<string, string>} metadata User defined
 *     call metadata
 * @return {!grpc.web.ClientReadableStream<!proto.demo.News>}
 *     The XHR Node Readable Stream
 */
proto.demo.NewsServiceClient.prototype.subscribe =
    function(request, metadata) {
  return this.client_.serverStreaming(this.hostname_ +
      '/demo.NewsService/subscribe',
      request,
      metadata,
      methodInfo_subscribe);
};


/**
 * @const
 * @type {!grpc.web.AbstractClientBase.MethodInfo<
 *   !proto.demo.News,
 *   !proto.demo.News>}
 */
const methodInfo_postNews = new grpc.web.AbstractClientBase.MethodInfo(
  proto.demo.News,
  /** @param {!proto.demo.News} request */
  function(request) {
    return request.serializeBinary();
  },
  proto.demo.News.deserializeBinary
);


/**
 * @param {!proto.demo.News} request The
 *     request proto
 * @param {!Object<string, string>} metadata User defined
 *     call metadata
 * @param {function(?grpc.web.Error, ?proto.demo.News)}
 *     callback The callback function(error, response)
 * @return {!grpc.web.ClientReadableStream<!proto.demo.News>|undefined}
 *     The XHR Node Readable Stream
 */
proto.demo.NewsServiceClient.prototype.postNews =
    function(request, metadata, callback) {
  return this.client_.rpcCall(this.hostname_ +
      '/demo.NewsService/postNews',
      request,
      metadata,
      methodInfo_postNews,
      callback);
};


/**
 * @param {!proto.demo.News} request The
 *     request proto
 * @param {!Object<string, string>} metadata User defined
 *     call metadata
 * @return {!Promise<!proto.demo.News>}
 *     The XHR Node Readable Stream
 */
proto.demo.NewsServicePromiseClient.prototype.postNews =
    function(request, metadata) {
  return new Promise((resolve, reject) => {
    this.delegateClient_.postNews(
      request, metadata, (error, response) => {
        error ? reject(error) : resolve(response);
      });
  });
};


module.exports = proto.demo;

