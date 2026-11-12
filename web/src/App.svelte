<script lang="ts">
  import { Topic } from './gen/demo/news/v1/news_service_pb.ts'
  import { NewsFeed, topicLabel } from './lib/feed.svelte.ts'
  import PostForm from './lib/PostForm.svelte'

  const tabs = [Topic.UNSPECIFIED, Topic.TECH, Topic.SPORT, Topic.ECONOMY]
  const feed = new NewsFeed()
  feed.select(Topic.UNSPECIFIED)
</script>

<main class="container">
  <header>
    <h1>gRPC News</h1>
    <p>Svelte and Connect-ES, talking grpc-web to a Kotlin gRPC server.</p>
  </header>

  <div role="group" aria-label="Topics">
    {#each tabs as topic (topic)}
      <button
        class:outline={feed.topic !== topic}
        aria-current={feed.topic === topic}
        onclick={() => feed.select(topic)}
      >
        {topic === Topic.UNSPECIFIED ? 'All' : topicLabel(topic)}
      </button>
    {/each}
  </div>

  <PostForm {feed} />

  {#if feed.error}
    <article class="error" role="alert">
      {feed.error}
      <button class="secondary" onclick={() => feed.select(feed.topic)}>Retry</button>
    </article>
  {/if}

  {#each feed.news as news (news.id)}
    <article id={news.id} class="news">
      <img src={news.imageUrl} alt="" width="100" height="100" loading="lazy" />
      <div>
        <h2>{news.title}</h2>
        <p>{news.description}</p>
        <small>{topicLabel(news.topic)}</small>
      </div>
    </article>
  {/each}

  {#if feed.breaking}
    <div class="breaking" role="status">
      <a href="#{feed.breaking.id}">Breaking news: {feed.breaking.title}</a>
    </div>
  {/if}
</main>

<style>
  main {
    max-width: 720px;
  }

  [aria-label='Topics'] button {
    flex: 1 1 0;
    min-width: 0;
    padding-inline: 0.25rem;
  }

  .news {
    display: grid;
    grid-template-columns: 100px minmax(0, 1fr);
    gap: var(--pico-spacing);
    align-items: start;
    overflow-wrap: anywhere;
  }

  .news img {
    border-radius: var(--pico-border-radius);
    object-fit: cover;
    background: var(--pico-muted-border-color);
  }

  .news h2 {
    font-size: 1.25rem;
    margin-bottom: 0.25rem;
  }

  .news p {
    margin-bottom: 0.25rem;
  }

  .error {
    display: flex;
    justify-content: space-between;
    align-items: center;
    gap: var(--pico-spacing);
    color: var(--pico-del-color);
  }

  .error button {
    width: auto;
    margin: 0;
  }

  .breaking {
    position: fixed;
    top: 1rem;
    left: 50%;
    transform: translateX(-50%);
    max-width: calc(100% - 2rem);
    padding: 0.75rem 1.25rem;
    border-radius: var(--pico-border-radius);
    background: var(--pico-primary-background);
    color: var(--pico-primary-inverse);
    box-shadow: var(--pico-card-box-shadow);
    font-weight: 600;
  }

  .breaking a {
    color: inherit;
    text-decoration: none;
  }
</style>
