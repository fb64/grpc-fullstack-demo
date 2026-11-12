// Command newscli is a command line client for the demo.news.v1.NewsService gRPC API.
package main

import (
	"context"
	"errors"
	"fmt"
	"io"
	"os"
	"os/signal"
	"strings"
	"syscall"
	"time"

	newsv1 "github.com/fb64/grpc-fullstack-demo/newscli/gen/demo/news/v1"
	"github.com/urfave/cli/v3"
	"google.golang.org/grpc"
	"google.golang.org/grpc/credentials/insecure"
	"google.golang.org/grpc/status"
)

func main() {
	ctx, stop := signal.NotifyContext(context.Background(), os.Interrupt, syscall.SIGTERM)
	defer stop()

	if err := newApp(dialInsecure).Run(ctx, os.Args); err != nil {
		fmt.Fprintln(os.Stderr, "error:", err)
		os.Exit(1)
	}
}

// dialer opens a client connection to the news server at address.
type dialer func(address string) (*grpc.ClientConn, error)

func dialInsecure(address string) (*grpc.ClientConn, error) {
	return grpc.NewClient(address, grpc.WithTransportCredentials(insecure.NewCredentials()))
}

func newApp(dial dialer) *cli.Command {
	// withClient opens the connection, runs action with a NewsService client, then closes it.
	withClient := func(action func(context.Context, *cli.Command, newsv1.NewsServiceClient) error) cli.ActionFunc {
		return func(ctx context.Context, cmd *cli.Command) error {
			conn, err := dial(cmd.String("address"))
			if err != nil {
				return fmt.Errorf("connect to %s: %w", cmd.String("address"), err)
			}
			defer conn.Close()
			return action(ctx, cmd, newsv1.NewNewsServiceClient(conn))
		}
	}

	return &cli.Command{
		Name:    "newscli",
		Usage:   "command line client for the gRPC news server",
		Version: "2.0.0",
		Flags: []cli.Flag{
			&cli.StringFlag{
				Name:    "address",
				Aliases: []string{"a"},
				Value:   "localhost:6565",
				Usage:   "address of the news server",
				Sources: cli.EnvVars("NEWS_ADDRESS"),
			},
		},
		Commands: []*cli.Command{
			{
				Name:   "list",
				Usage:  "list the news of a topic",
				Flags:  []cli.Flag{topicFlag("all")},
				Action: withClient(listNews),
			},
			{
				Name:  "add",
				Usage: "publish a news item",
				Flags: []cli.Flag{
					topicFlag("tech"),
					&cli.StringFlag{Name: "title", Value: "Title from Go", Usage: "news title"},
					&cli.StringFlag{Name: "description", Value: "Description from Go", Usage: "news description"},
					&cli.StringFlag{Name: "image-url", Value: "https://picsum.photos/400/400/?random", Usage: "news image URL"},
				},
				Action: withClient(addNews),
			},
			{
				Name:  "subscribe",
				Usage: "print news as they are published, until Ctrl+C",
				Flags: []cli.Flag{
					topicFlag("all"),
					&cli.DurationFlag{Name: "for", Usage: "stop after this duration (e.g. 30s); 0 means never"},
				},
				Action: withClient(subscribe),
			},
		},
	}
}

func topicFlag(defaultValue string) cli.Flag {
	return &cli.StringFlag{
		Name:    "topic",
		Aliases: []string{"t"},
		Value:   defaultValue,
		Usage:   "topic: tech, sport, economy or all",
	}
}

func listNews(ctx context.Context, cmd *cli.Command, client newsv1.NewsServiceClient) error {
	topic, err := parseTopic(cmd.String("topic"))
	if err != nil {
		return err
	}
	ctx, cancel := context.WithTimeout(ctx, 10*time.Second)
	defer cancel()

	resp, err := client.GetNews(ctx, &newsv1.GetNewsRequest{Topic: topic})
	if err != nil {
		return fmt.Errorf("get news: %s", status.Convert(err).Message())
	}
	for _, news := range resp.GetNews() {
		printNews(cmd.Root().Writer, news)
	}
	return nil
}

func addNews(ctx context.Context, cmd *cli.Command, client newsv1.NewsServiceClient) error {
	topic, err := parseTopic(cmd.String("topic"))
	if err != nil {
		return err
	}
	ctx, cancel := context.WithTimeout(ctx, 10*time.Second)
	defer cancel()

	resp, err := client.PostNews(ctx, &newsv1.PostNewsRequest{
		Title:       cmd.String("title"),
		Description: cmd.String("description"),
		ImageUrl:    cmd.String("image-url"),
		Topic:       topic,
	})
	if err != nil {
		return fmt.Errorf("post news: %s", status.Convert(err).Message())
	}
	printNews(cmd.Root().Writer, resp.GetNews())
	return nil
}

func subscribe(ctx context.Context, cmd *cli.Command, client newsv1.NewsServiceClient) error {
	topic, err := parseTopic(cmd.String("topic"))
	if err != nil {
		return err
	}
	if d := cmd.Duration("for"); d > 0 {
		var cancel context.CancelFunc
		ctx, cancel = context.WithTimeout(ctx, d)
		defer cancel()
	}

	stream, err := client.Subscribe(ctx, &newsv1.SubscribeRequest{Topic: topic})
	if err != nil {
		return fmt.Errorf("subscribe: %s", status.Convert(err).Message())
	}
	for {
		resp, err := stream.Recv()
		switch {
		case errors.Is(err, io.EOF):
			return nil // the server closed the stream
		case err != nil && ctx.Err() != nil:
			return nil // Ctrl+C or --for elapsed
		case err != nil:
			return fmt.Errorf("subscribe: %s", status.Convert(err).Message())
		}
		printNews(cmd.Root().Writer, resp.GetNews())
	}
}

// parseTopic maps "tech", "sport", "economy" (any case) to a Topic, and "all" or "" to TOPIC_UNSPECIFIED.
func parseTopic(value string) (newsv1.Topic, error) {
	if value == "" || strings.EqualFold(value, "all") {
		return newsv1.Topic_TOPIC_UNSPECIFIED, nil
	}
	topic, ok := newsv1.Topic_value["TOPIC_"+strings.ToUpper(value)]
	if !ok || topic == int32(newsv1.Topic_TOPIC_UNSPECIFIED) {
		return 0, fmt.Errorf("unknown topic %q: use tech, sport, economy or all", value)
	}
	return newsv1.Topic(topic), nil
}

func printNews(w io.Writer, news *newsv1.News) {
	topic := strings.ToLower(strings.TrimPrefix(news.GetTopic().String(), "TOPIC_"))
	fmt.Fprintf(w, "%-12s %-8s %s\n", news.GetId(), topic, news.GetTitle())
}
