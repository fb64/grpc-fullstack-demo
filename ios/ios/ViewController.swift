//
//  ViewController.swift
//  ios
//
//  Created by florian bernard on 27/09/2018.
//  Copyright © 2018 florian bernard. All rights reserved.
//

import UIKit
import MaterialComponents.MaterialSnackbar

class ViewController: UIViewController, UITableViewDataSource, UITabBarDelegate {
    @IBOutlet weak var tableView: UITableView!
    @IBOutlet weak var titleLabel: UILabel!
    @IBOutlet weak var techButton: UITabBarItem!
    @IBOutlet weak var sportButton: UITabBarItem!
    @IBOutlet weak var economyButton: UITabBarItem!
    @IBOutlet weak var tabBar: UITabBar!

    var newsList = [Fr_Fbernard_Grpc_News_News]()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
        tabBar.selectedItem = techButton
        changeTopic(topic: Fr_Fbernard_Grpc_News_Topic.tech)
    }
    
    
    override func viewWillDisappear(_ animated: Bool) {
        NewsDataService.shared.cancelSubscription()
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let theNews = newsList[indexPath.row]
        let cell = tableView.dequeueReusableCell(withIdentifier: "newsCell", for: indexPath) as! NewsTableViewCell
        
        let url = URL(string:theNews.imageURL)
        if let imageUrl = url{
            URLSession.shared.dataTask(with: imageUrl) { data, response, error in
                guard let data = data, error == nil else { return }
                DispatchQueue.main.async() {
                    cell.photo.image = UIImage(data: data)
                }
                }.resume()
        }
        
        cell.descriptionLabel.text = theNews.description_p
        cell.titleLabel.text = theNews.title
        return cell
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return newsList.count
    }
    
    
    func tabBar(_ tabBar: UITabBar, didSelect item: UITabBarItem){
        
        var topic = Fr_Fbernard_Grpc_News_Topic.tech
        
        switch item.tag {
        case 0:
            topic = Fr_Fbernard_Grpc_News_Topic.tech
            titleLabel.text = "Tech news"
        case 1:
            topic = Fr_Fbernard_Grpc_News_Topic.sport
            titleLabel.text = "Sport news"
        case 2:
            topic = Fr_Fbernard_Grpc_News_Topic.economy
            titleLabel.text = "Economy news"
        default:
            topic = Fr_Fbernard_Grpc_News_Topic.tech
            titleLabel.text = "Tech news"
        }
        
        changeTopic(topic: topic)
    }
    
    
    private func changeTopic(topic:Fr_Fbernard_Grpc_News_Topic){
        NewsDataService.shared.subscribe(topic: topic) { addedNews in
            self.newsList.append(addedNews)
            self.tableView.reloadData()
            self.showSnackBar()
        }
        newsList = NewsDataService.shared.getNews(topic: topic) ?? [Fr_Fbernard_Grpc_News_News]()
        tableView.reloadData()
    }
    
    
    private func showSnackBar(){
        let message = MDCSnackbarMessage()
        message.text = "Breaking news"
        
        let action = MDCSnackbarMessageAction()
        let actionHandler = {() in
            let indexPath = IndexPath(row: self.newsList.count-1, section: 0)
            self.tableView.scrollToRow(at: indexPath, at: .bottom, animated: true)
        }
        action.handler = actionHandler
        action.title = "show"
        message.action = action
        MDCSnackbarManager.show(message)
    }
    

}
