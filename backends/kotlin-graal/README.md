# gRPC Server (Kotlin)

Kotlin implementation of `demo.news.v1.NewsService` ([proto](../../proto/demo/news/v1/news_service.proto)),
built with [grpc-kotlin](https://github.com/grpc/grpc-kotlin) coroutine stubs and compiled to a native binary
with [GraalVM](https://www.graalvm.org/).

Besides the news service, the server exposes the standard gRPC
[health](https://grpc.io/docs/guides/health-checking/) and
[reflection](https://grpc.io/docs/guides/reflection/) services, so tools like
[grpcurl](https://github.com/fullstorydev/grpcurl) work without the proto file.
News are kept in memory and seeded with 10 items per topic.

## Requirements

[mise](https://mise.jdx.dev/) installs the right JDK (GraalVM CE 25) from the repository root:

```sh
mise install
```

## Run

All commands run from the repository root. The server listens on port `6565`
(override it with the first argument or the `PORT` environment variable).

| Command                  | What it does                                       |
|--------------------------|----------------------------------------------------|
| `mise run server`        | Run on the JVM                                     |
| `mise run server:native` | Build the GraalVM native binary (a few minutes), then run it |
| `mise run server:test`   | Run the tests (in-process gRPC server)             |
| `mise run server:image`  | Build the `news-server` container image (native binary on distroless) |

Run the container image:

```sh
podman run --rm -p 6565:6565 news-server   # or docker
```

## Try it with grpcurl

```sh
grpcurl -plaintext localhost:6565 list
grpcurl -plaintext -d '{"topic":"TOPIC_TECH"}' localhost:6565 demo.news.v1.NewsService/GetNews
grpcurl -plaintext -d '{"topic":"TOPIC_TECH"}' localhost:6565 demo.news.v1.NewsService/Subscribe
grpcurl -plaintext -d '{"title":"Hello","topic":"TOPIC_TECH"}' localhost:6565 demo.news.v1.NewsService/PostNews
```

## Generated code

`src/generated` is produced by `mise run generate` (`buf generate`, see [buf.gen.yaml](../../buf.gen.yaml))
and committed. Do not edit it by hand.
