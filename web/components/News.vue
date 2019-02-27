<template>
  <div class="VueToNews">
    <h1>
      News List
    </h1>
    <nav class="nav mb-3">
      <ul class="nav nav-tabs">
        <li v-for="item in navItems" :key="item.title" class="nav-item">
          <a class="nav-link" :class="{ active : topicActive(item.topic) }" @click="switchTopic(item)">{{ item.title }}</a>
        </li>
      </ul>
    </nav>
    <div v-for="item in newsList" :key="item.getNewsid()" class="media mb-3">
      <img class="align-self-start" :src="item.getImageurl()" width="100px"></img>
      <div class="media-body">
        <h5 class="mt-0">
          {{ item.getTitle() }}
        </h5>
        {{ item.getDescription() }}
      </div>
    </div>
  </div>
</template>


<script>
// import { mapMutations } from 'vuex'
import { Topic } from '../store/grpc/news_service_pb'

export default {
  data: function() {
    return {
      navItems: [
        {
          title: 'Sport',
          topic: Topic.SPORT,
          isActive: false
        },
        {
          title: 'Tech',
          topic: Topic.TECH,
          isActive: false
        },
        {
          title: 'Economy',
          topic: Topic.ECONOMY,
          isActive: false
        }
      ]
    }
  },
  computed: {
    newsList() {
      return this.$store.state.news.newsList
    }
  },
  methods: {
    switchTopic: function(navItem) {
      this.$store.dispatch('news/getNews', navItem.topic)
      this.$store.dispatch('news/subscribe', navItem.topic)
    },
    topicActive(topic) {
      return this.$store.state.news.topic === topic
    }
  }
}
</script>
