# gRPC Client (Go command line)

Command line client for `demo.news.v1.NewsService` ([proto](../proto/demo/news/v1/news_service.proto)),
built with [grpc-go](https://github.com/grpc/grpc-go) and [urfave/cli](https://github.com/urfave/cli).

## Install

```sh
go install github.com/fb64/grpc-fullstack-demo/newscli@latest
```

Or run it from the repository root without installing (Go is pinned by [mise](https://mise.jdx.dev/)):

```sh
mise run cli -- list -t tech
```

Start the server first (see the [server project](../backends/kotlin-graal)).

## Usage

```sh
newscli list                          # news of every topic
newscli list -t sport                 # news of one topic: tech, sport or economy
newscli add -t tech --title "Hello"   # publish a news item (also --description, --image-url)
newscli subscribe -t tech             # print news as they are published, until Ctrl+C
newscli subscribe --for 30s           # stop after 30 seconds
newscli --help
```

The server address defaults to `localhost:6565`; change it with `-a host:port` or the `NEWS_ADDRESS` environment variable.

## Development

| Command             | What it does                    |
|---------------------|---------------------------------|
| `mise run cli:test` | Run the tests (in-memory gRPC server, no network) |
| `mise run cli:lint` | `gofmt` check and `go vet`      |

`gen/` is produced by `mise run generate` (`buf generate`, see [buf.gen.yaml](../buf.gen.yaml))
and committed, so `go install` works. Do not edit it by hand.
