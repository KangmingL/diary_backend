# **Diary API**


## **Users and accounts**
### Manage friends
* [GET /users/:id/followers](./test.md)
* [GET /users/:id/following]()
* [POST /users/:id/following]()
* [DELETE /users/:source_id/following/:target_id]()
* [GET /users/:id/pending_friends]()
* [POST /users/:source_id/send_request/:target_id]()


### Account settings and info
* [GET /account/:id/profile]()
* [GET /account/:id/profile_image]()
* [GET /account/:id/background_image]()
* [GET /account/:id/description]()
* [POST /account/update_profile_image]()
* [POST /account/update_background_image]()
* [POST /account/update_description]()

## **Posts**
### Timelines
* [GET /users/:id/posts]()

### Manage posts
* [POST /posts]()
* [DELETE /posts/:id]()

### Posts info
* [GET /posts/:id/liking_users]()
* [GET /posts/:id/replies]()

### Interactions
* [POST /users/]
