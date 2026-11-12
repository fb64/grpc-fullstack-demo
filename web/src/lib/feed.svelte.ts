import { ConnectError } from '@connectrpc/connect'
import { type News, Topic } from '../gen/demo/news/v1/news_service_pb.ts'
import { newsClient } from './client.ts'

export interface NewsDraft {
  title: string
  description: string
  imageUrl: string
  topic: Topic
}

/** News of the selected topic, kept up to date by a Subscribe stream. */
export class NewsFeed {
  topic = $state(Topic.UNSPECIFIED)
  news = $state.raw<News[]>([])
  /** Latest news received from the stream, shown for a few seconds. */
  breaking = $state.raw<News | null>(null)
  error = $state<string | null>(null)

  #subscription: AbortController | undefined
  #breakingTimer: ReturnType<typeof setTimeout> | undefined

  /** Shows the news of `topic` (every topic for UNSPECIFIED) and follows new ones. */
  async select(topic: Topic): Promise<void> {
    this.#subscription?.abort()
    const subscription = new AbortController()
    this.#subscription = subscription
    this.topic = topic
    this.news = []
    this.error = null

    // Subscribe first, so nothing published while GetNews runs is missed.
    void this.#follow(topic, subscription.signal)
    try {
      const { news } = await newsClient.getNews({ topic }, { signal: subscription.signal })
      const ids = new Set(news.map((item) => item.id))
      this.news = [...news, ...this.news.filter((item) => !ids.has(item.id))]
    } catch (error) {
      if (!subscription.signal.aborted) this.error = `Could not load the news: ${messageOf(error)}`
    }
  }

  /** Publishes a news item; resolves to an error message, or null on success. */
  async post(draft: NewsDraft): Promise<string | null> {
    try {
      await newsClient.postNews(draft)
      return null
    } catch (error) {
      return messageOf(error)
    }
  }

  async #follow(topic: Topic, signal: AbortSignal): Promise<void> {
    try {
      for await (const { news } of newsClient.subscribe({ topic }, { signal })) {
        if (!news) continue
        this.news = [...this.news, news]
        this.#announce(news)
      }
      if (!signal.aborted) this.error = 'Live updates ended'
    } catch (error) {
      if (!signal.aborted) this.error = `Live updates stopped: ${messageOf(error)}`
    }
  }

  #announce(news: News): void {
    clearTimeout(this.#breakingTimer)
    this.breaking = news
    this.#breakingTimer = setTimeout(() => (this.breaking = null), 5_000)
  }
}

export function topicLabel(topic: Topic): string {
  const name = Topic[topic] ?? 'UNKNOWN'
  return name.charAt(0) + name.slice(1).toLowerCase()
}

function messageOf(error: unknown): string {
  return ConnectError.from(error).rawMessage
}
