<script lang="ts">
  import { Topic } from '../gen/demo/news/v1/news_service_pb.ts'
  import { type NewsFeed, topicLabel } from './feed.svelte.ts'

  let { feed }: { feed: NewsFeed } = $props()

  const topics = [Topic.TECH, Topic.SPORT, Topic.ECONOMY]
  let title = $state('')
  let description = $state('')
  let topic = $state(Topic.TECH)
  let sending = $state(false)
  let error = $state<string | null>(null)

  async function submit(event: SubmitEvent) {
    event.preventDefault()
    sending = true
    const seed = Math.random().toString(36).slice(2)
    error = await feed.post({ title, description, topic, imageUrl: `https://picsum.photos/seed/${seed}/400/400` })
    sending = false
    if (!error) {
      title = ''
      description = ''
    }
  }
</script>

<details>
  <summary>Publish a news item</summary>
  <form onsubmit={submit}>
    <input name="title" placeholder="Title" aria-label="Title" bind:value={title} required />
    <textarea name="description" placeholder="Description" aria-label="Description" bind:value={description}></textarea>
    <div role="group">
      <select name="topic" aria-label="Topic" bind:value={topic}>
        {#each topics as value (value)}
          <option {value}>{topicLabel(value)}</option>
        {/each}
      </select>
      <button type="submit" aria-busy={sending} disabled={sending}>Publish</button>
    </div>
    {#if error}
      <small class="error" role="alert">{error}</small>
    {/if}
  </form>
</details>

<style>
  .error {
    color: var(--pico-del-color);
  }
</style>
