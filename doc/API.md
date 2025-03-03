|Request URL|Parameters|Returns|Method|
|-----------|----------|-------|------|
|/api/users | username | OK with user data for users with name matching the pattern ``*username*``, or every user with priviledges of COMPLEX or higher if not provided. |POST|
|/api/users/action|id|OK if action was executed for user with id ``id``, BADREQUEST if user was not found.|POST
