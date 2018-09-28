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
    
    private let grpcService :Fr_Fbernard_Grpc_News_NewsServiceServiceClient
    private var addNewsEvent : Fr_Fbernard_Grpc_News_NewsServicesubscribeCall?
    private var isSubscriptionActive = true
    
    private init(){
         grpcService = Fr_Fbernard_Grpc_News_NewsServiceServiceClient(address:"localhost:6565",secure:false)
    }

    
    func getNews(topic:Fr_Fbernard_Grpc_News_Topic) -> [Fr_Fbernard_Grpc_News_News]?{
        do {
            var request = Fr_Fbernard_Grpc_News_NewsRequest()
            request.topic = topic
            let response = try grpcService.getNews(request)
            return response.news
        } catch  {
           return nil
        }
    }
    
    
    func subscribe(topic:Fr_Fbernard_Grpc_News_Topic,completion: @escaping (Fr_Fbernard_Grpc_News_News) -> Void ){
        do{
            var request = Fr_Fbernard_Grpc_News_SubscribeRequest()
            request.topic = topic
            addNewsEvent = try grpcService.subscribe(request) { (CallResult) in
                self.isSubscriptionActive = false
            }
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
