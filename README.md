# 📖 Diary

📖 Diary is a life recording social media app allows you to capture all your delicate ideas💡
and precious moments 💐 in your life. You can share your stories, thoughts, brilliant ideas 
suddenly come out of your head publicly with others, or just treat it private, like your own valuable
 treasures. This is a **📖 Diary** for yourself and all your loved ones.


This is the backend implementation of **📖 Diary**. Currently support friendship management, 
user management, posting and reading feeds.
More features and functionalities are in development. 

#### 💻 Implementation details:
* Use **Spring Boot Model-View-Controller (MVC)** pattern to design 
three-layer backend architecture (Controller, Service and Repository). 
* **📖 Diary** relies on **PostgreSQL** database for data storage and Spring JPA to interact with database.
* **Redis** is used to improve performance and user experience. 
There are two caching strategies were implemented in this application: **read-through** and **write-through**. 
Naturally, social media application is usually read-heavy. Such caching design always keeps
cache up-to-date, thus improves data retrieval performance.


## API Reference
### Friendship management
#### Followers and followings
* [GET api/follower/:user_id]() 
* [GET api/following/:user_id]()
#### Follow and unfollow
* [POST api/relationship/:user_id/follow/:target_id]()
* [POST api/relationship/:user_id/unfollow/:target_id]()

[//]: # (* [GET api/users/:id/pending_friends]&#40;&#41;)

[//]: # (* [POST api/users/:source_id/send_request/:target_id]&#40;&#41;)

### Posts
#### Get feeds
* [GET api/user/:user_id/userfeed]()
* [GET api/user/:user_id/homefeed]()

#### Manage posts
* [GET api/post/:id}]()
* [DELETE api/post/:id]()
* [POST api/user/:id/post]()

#### Posts info
* [GET api/post/:id/likes]()
* [GET api/post/:id/likes_user]()
* [GET api/post/:id/replies]()

### Account settings and info
#### Get user info
* [GET api/account/:id/profile]()
* [GET api/account/:id/username]()
* [GET api/account/:id/profile_image]()
* [GET api/account/:id/background_image]() 
* [GET api/account/:id/description]()
#### Update user info
* [POST api/account/update_username]()
* [POST api/account/update_profile_image]()
* [POST api/account/update_background_image]()
* [POST api/account/update_description]()


### 📣 More features are coming soon ...



