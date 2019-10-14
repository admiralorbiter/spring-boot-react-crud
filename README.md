# Assignment

## Building & running

Build prerequisites:

* JDK 11
* maven v3.6+
* npm v6+

Building & running with _initial data_:

```bash
$ mvn clean package
$ java -jar -Dspring.profiles.active=initial-data target/assignment.jar
```

React application is built with Maven as well. (Maven triggers npm scripts)

## Implementation notes

* When`initial-data` profile is given 2 sample users is created when the app started. 
* Sorting is handled in frontend and validation is handled in backend.
  * names are case sensitive.
  * Normally e-mail would be unique. However documentation explicitly requires the full name to be unique and says nothing about email. Hence, email is not made unique on purpose.
  * first and last name are required as they form a unique key.
  * address is made required but e-mail and birth date are not made required just to show different implementation.
* Note that `destan@dev` or `destan@localhost` are valid email addresses as `dev` and `localhost` maybe valid domain names.
* We could use `fullName` as the id as its already unique however that would make checking for existing users when updating impossible and we would 
end up with an API which mistakinly creates new users via `PUT /users` endpoint.

### React libraries

* https://material-ui.com
* https://github.com/mbrn/material-table
* https://downshift.netlify.com