package main

import (
	"bytes"
	"context"
	"net"
	"testing"

	newsv1 "github.com/fb64/grpc-fullstack-demo/newscli/gen/demo/news/v1"
	"google.golang.org/grpc"
	"google.golang.org/grpc/codes"
	"google.golang.org/grpc/credentials/insecure"
	"google.golang.org/grpc/status"
	"google.golang.org/grpc/test/bufconn"
)

// fakeServer records the requests it receives and answers with canned news.
type fakeServer struct {
	newsv1.UnimplementedNewsServiceServer
	lastTopic newsv1.Topic
	lastPost  *newsv1.PostNewsRequest
}

func (s *fakeServer) GetNews(_ context.Context, req *newsv1.GetNewsRequest) (*newsv1.GetNewsResponse, error) {
	s.lastTopic = req.GetTopic()
	return &newsv1.GetNewsResponse{News: []*newsv1.News{
		{Id: "sport-0", Title: "Match won", Topic: newsv1.Topic_TOPIC_SPORT},
		{Id: "sport-1", Title: "Record broken", Topic: newsv1.Topic_TOPIC_SPORT},
	}}, nil
}

func (s *fakeServer) PostNews(_ context.Context, req *newsv1.PostNewsRequest) (*newsv1.PostNewsResponse, error) {
	if req.GetTitle() == "" {
		return nil, status.Error(codes.InvalidArgument, "title must not be blank")
	}
	s.lastPost = req
	return &newsv1.PostNewsResponse{News: &newsv1.News{Id: "economy-10", Title: req.GetTitle(), Topic: req.GetTopic()}}, nil
}

func (s *fakeServer) Subscribe(req *newsv1.SubscribeRequest, stream grpc.ServerStreamingServer[newsv1.SubscribeResponse]) error {
	s.lastTopic = req.GetTopic()
	for _, title := range []string{"First", "Second"} {
		if err := stream.Send(&newsv1.SubscribeResponse{News: &newsv1.News{Id: "tech-9", Title: title, Topic: newsv1.Topic_TOPIC_TECH}}); err != nil {
			return err
		}
	}
	return nil // ends the stream
}

// run executes the CLI with args against an in-memory fakeServer and returns its output.
func run(t *testing.T, args ...string) (*fakeServer, string, error) {
	t.Helper()
	listener := bufconn.Listen(1 << 20)
	fake := &fakeServer{}
	server := grpc.NewServer()
	newsv1.RegisterNewsServiceServer(server, fake)
	go server.Serve(listener)
	t.Cleanup(server.Stop)

	dial := func(string) (*grpc.ClientConn, error) {
		return grpc.NewClient("passthrough:///bufnet",
			grpc.WithContextDialer(func(ctx context.Context, _ string) (net.Conn, error) { return listener.DialContext(ctx) }),
			grpc.WithTransportCredentials(insecure.NewCredentials()),
		)
	}
	var out bytes.Buffer
	app := newApp(dial)
	app.Writer = &out
	err := app.Run(context.Background(), append([]string{"newscli"}, args...))
	return fake, out.String(), err
}

func TestList(t *testing.T) {
	fake, out, err := run(t, "list", "--topic", "SPORT")
	if err != nil {
		t.Fatal(err)
	}
	if fake.lastTopic != newsv1.Topic_TOPIC_SPORT {
		t.Errorf("requested topic = %v, want TOPIC_SPORT", fake.lastTopic)
	}
	want := "sport-0      sport    Match won\nsport-1      sport    Record broken\n"
	if out != want {
		t.Errorf("output =\n%s\nwant\n%s", out, want)
	}
}

func TestListDefaultsToAllTopics(t *testing.T) {
	fake, _, err := run(t, "list")
	if err != nil {
		t.Fatal(err)
	}
	if fake.lastTopic != newsv1.Topic_TOPIC_UNSPECIFIED {
		t.Errorf("requested topic = %v, want TOPIC_UNSPECIFIED", fake.lastTopic)
	}
}

func TestAdd(t *testing.T) {
	fake, out, err := run(t, "add", "-t", "economy", "--title", "Markets up")
	if err != nil {
		t.Fatal(err)
	}
	if fake.lastPost.GetTitle() != "Markets up" || fake.lastPost.GetTopic() != newsv1.Topic_TOPIC_ECONOMY {
		t.Errorf("posted %v", fake.lastPost)
	}
	if want := "economy-10   economy  Markets up\n"; out != want {
		t.Errorf("output = %q, want %q", out, want)
	}
}

func TestAddReportsServerError(t *testing.T) {
	_, _, err := run(t, "add", "--title", "")
	if err == nil || err.Error() != "post news: title must not be blank" {
		t.Errorf("error = %v, want the server's message", err)
	}
}

func TestSubscribePrintsStreamUntilServerCloses(t *testing.T) {
	fake, out, err := run(t, "subscribe", "-t", "tech")
	if err != nil {
		t.Fatal(err)
	}
	if fake.lastTopic != newsv1.Topic_TOPIC_TECH {
		t.Errorf("subscribed topic = %v, want TOPIC_TECH", fake.lastTopic)
	}
	if want := "tech-9       tech     First\ntech-9       tech     Second\n"; out != want {
		t.Errorf("output = %q, want %q", out, want)
	}
}

func TestUnknownTopic(t *testing.T) {
	for _, topic := range []string{"weather", "unspecified"} {
		if _, _, err := run(t, "list", "-t", topic); err == nil {
			t.Errorf("topic %q: expected an error", topic)
		}
	}
}
