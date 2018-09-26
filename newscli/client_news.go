package main

import (
	"context"
	"io"
	"log"
	"os"
	"time"

	pb "github.com/fb64/grpc-fullstack-demo/newscli/news_service"
	"google.golang.org/grpc"
	"gopkg.in/urfave/cli.v1"
)

var client pb.NewsServiceClient
var connection *grpc.ClientConn
var connectionError error

func main() {

	var topicString string
	var address string

	defer closeConnection()

	app := cli.NewApp()
	app.Name = "client_news"
	app.Usage = "news grpc client (command line interface)"
	app.Version = "1.0.0"

	app.Flags = []cli.Flag{
		cli.StringFlag{
			Name:        "topic, t",
			Value:       "TECH",
			Usage:       "Topic to use : TECH, SPOR, ECONOMY",
			Destination: &topicString,
		},
		cli.StringFlag{
			Name:        "address, a",
			Value:       "localhost:6565",
			Usage:       "address of the news server",
			Destination: &address,
		},
	}

	app.Commands = []cli.Command{
		{
			Name:  "list",
			Usage: "list news of a topic",
			Action: func(c *cli.Context) error {
				initClient(address)
				topic := stringToTopic(topicString)
				listNews(client, topic)
				return nil
			},
		},
		{
			Name:  "add",
			Usage: "add a news in a topic",
			Action: func(c *cli.Context) error {
				initClient(address)
				topic := stringToTopic(topicString)
				postNews(client, &pb.News{Title: "Title From GO", ImageUrl: "https://picsum.photos/400/400/?random", Description: "Description From GO", Topic: topic})
				return nil
			},
		},
		{
			Name:  "subscribe",
			Usage: "subscribe to a news's topic for 30s",
			Action: func(c *cli.Context) error {
				initClient(address)
				topic := stringToTopic(topicString)
				subscribeTo(client, topic)
				return nil
			},
		},
	}

	appErr := app.Run(os.Args)
	if appErr != nil {
		log.Fatal(appErr)
	}
}

func initClient(serverAddress string) {
	connection, connectionError = grpc.Dial(serverAddress, grpc.WithInsecure())
	if connectionError != nil {
		log.Fatalf("did not connect: %v", connectionError)
	}
	client = pb.NewNewsServiceClient(connection)
}

func closeConnection() {
	if connection != nil {
		connection.Close()
	}

}

func stringToTopic(topicString string) pb.Topic {
	topicValue := pb.Topic_value[topicString]
	return pb.Topic(topicValue)
}

func listNews(client pb.NewsServiceClient, topic pb.Topic) {
	ctx, cancel := context.WithTimeout(context.Background(), 10*time.Second)
	defer cancel()

	r, err := client.GetNews(ctx, &pb.NewsRequest{Topic: topic})
	if err != nil {
		log.Fatalf("could get news: %v", err)
	}
	log.Printf("%s", r.News)
}

func subscribeTo(client pb.NewsServiceClient, topic pb.Topic) {

	ctx, cancel := context.WithTimeout(context.Background(), 30*time.Second)
	defer cancel()

	stream, err := client.Subscribe(ctx, &pb.SubscribeRequest{Topic: pb.Topic_TECH})
	if err != nil {
		log.Fatalf("could subscribe: %v", err)
	}

	waitc := make(chan struct{})
	go func() {
		for {
			news, err := stream.Recv()
			if err == io.EOF {
				close(waitc)
				return
			}
			if err != nil {
				log.Fatalf("%v.Subscribe(_) = _, %v", client, err)
			}
			log.Println(news)
		}
	}()
	<-waitc
}

func postNews(client pb.NewsServiceClient, newsToAdd *pb.News) {
	ctx, cancel := context.WithTimeout(context.Background(), 10*time.Second)
	defer cancel()

	newsSaved, err := client.PostNews(ctx, newsToAdd)
	if err != nil {
		log.Fatalf("could post news: %v", err)
	}
	log.Printf("news saved: %s", newsSaved)
}
