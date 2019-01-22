//
//  NewsDataService.swift
//  ios
//
//  Created by florian bernard on 28/09/2018.
//  Copyright © 2018 florian bernard. All rights reserved.
//

import Foundation
import SwiftGRPC

final class NewsDataService{
    
    static let shared = NewsDataService()
    
    private let grpcService : Demo_NewsServiceServiceClient
    private var addNewsEvent : Demo_NewsServicesubscribeCall?
    private var isSubscriptionActive = false
    
    private init(){
         grpcService = Demo_NewsServiceServiceClient(address:"localhost:6565",secure:false)
    }

    
    func getNews(topic:Demo_Topic) -> [Demo_News]?{
        do {
            var request = Demo_NewsRequest()
            request.topic = topic
            let response = try grpcService.getNews(request)
            return response.news
        } catch  {
           return nil
        }
    }
    
    
    func subscribe(topic:Demo_Topic,completion: @escaping (Demo_News) -> Void ){
        do{
            cancelSubscription()
            var request = Demo_SubscribeRequest()
            request.topic = topic
            addNewsEvent = try grpcService.subscribe(request) { (CallResult) in
                self.isSubscriptionActive = false
            }
            isSubscriptionActive = true
            DispatchQueue.global().async {
                do {
                    while (self.isSubscriptionActive) {
                        if let news = try self.addNewsEvent?.receive() {
                            DispatchQueue.main.async {
                                completion(news)
                            }
                        }
                    }
                }
                catch {
                }
            }
        } catch{
        }
    }
    
    func cancelSubscription() {
        addNewsEvent?.cancel()
    }
    
}
