# **Diary API**

API Reference

Keeping updating .....

## **Users and accounts**
### Manage friends
* [GET api/users/:id/followers](./test.md)
* [GET api/users/:id/following]()
* [POST api/users/:id/following]()
* [DELETE api/users/:source_id/following/:target_id]()
* [GET api/users/:id/pending_friends]()
* [POST api/users/:source_id/send_request/:target_id]()


### Account settings and info
* [GET api/account/:id/profile]()
* [GET api/account/:id/profile_image]()
* [GET api/account/:id/background_image]()
* [GET api/account/:id/description]()
* [POST api/account/update_profile_image]()
* [POST api/account/update_background_image]()
* [POST api/account/update_description]()

## **Posts**
### Feeds
* [GET api/users/:id/userfeed]()
* [GET api/users/:id/homefeed]()

### Manage posts
* [GET api/post/:id}]()
* [DELETE api/post/:id]()
* [POST api/user/:id/post]()

### Posts info
* [GET api/post/:id/liking_users]()
* [GET api/post/:id/replies]()

