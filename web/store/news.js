const { NewsRequest, SubscribeRequest } = require('./grpc/news_service_pb.js')
const { NewsServiceClient } = require('./grpc/news_service_grpc_web_pb.js')
const newsService = new NewsServiceClient('http://localhost:3000/grpc')
let currentSubscription = null

export const state = () => ({
  newsList: [],
  topic: null
})

export const mutations = {
  loadNews(state, newsList) {
    state.newsList = newsList
  },
  pushNews(state, news) {
    state.newsList.push(news)
  },
  updateTopic(state, topic) {
    state.topic = topic
  }
}

export const actions = {
  getNews({ commit }, topic) {
    const request = new NewsRequest()
    request.setTopic(topic)
    newsService.getNews(request, {}, function(err, response) {
      if (!err) {
        commit('updateTopic', topic)
        commit('loadNews', response.getNewsList())
      }
    })
  },
  subscribe({ commit }, topic) {
    const subscribeRequest = new SubscribeRequest()
    subscribeRequest.setTopic(topic)
    if (currentSubscription !== null) currentSubscription.cancel()
    currentSubscription = newsService.subscribe(subscribeRequest, {})
    currentSubscription.on('data', function(response) {
      commit('pushNews', response)
    })
  }
}
