package main

import (
	"context"
	"io"
	"log"
	"time"

	pb "github.com/fb64/grpc-fullstack-demo/cli/news_service"
	"google.golang.org/grpc"
)

const (
	address = "localhost:6565"
)

func main() {

	//Configure connection (insecure for the moment)
	conn, err := grpc.Dial(address, grpc.WithInsecure())
	if err != nil {
		log.Fatalf("did not connect: %v", err)
	}
	defer conn.Close()
	client := pb.NewNewsServiceClient(conn)

	//TODO use cmd arguments to switch operation
	listNews(client, pb.Topic_TECH)
	//subscribeTo(client, pb.Topic_TECH)
	//postNews(client, &pb.News{Title: "Title From GO", ImageUrl: "https://picsum.photos/400/400/?random", Description: "Description From GO", Topic: pb.Topic_TECH})
}

func listNews(client pb.NewsServiceClient, topic pb.Topic) {
	ctx, cancel := context.WithTimeout(context.Background(), 10*time.Second)
	defer cancel()

	r, err := client.GetNews(ctx, &pb.NewsRequest{Topic: topic})
	if err != nil {
		log.Fatalf("could get news: %v", err)
	}
	log.Printf("Greeting: %s", r.News)
}

func subscribeTo(client pb.NewsServiceClient, topic pb.Topic) {

	ctx, cancel := context.WithTimeout(context.Background(), 10*time.Second)
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
