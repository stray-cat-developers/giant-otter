# giant-otter
This API application collects and shows several swagger apis.

[![Build Status](https://github.com/stray-cat-developers/giant-otter/actions/workflows/gradle.yml/badge.svg)](https://github.com/stray-cat-developers/giant-otter)

# New Features!

# Installation
### Quick start
Java, Docker must be installed before starting.
Standalone system using embedded mongo database.

```sh
git clone https://github.com/fennec-fox/grant-otter.git
./quick-start.sh
```
Swagger api page is http://localhost:6200/swagger-ui.html

# How to use
*  run ./quick-start.sh
*  http://localhost:6200/swagger-ui.html
*  add another swagger spec         
    * use api
        ```js
        curl -X POST "http://localhost:6200/swagger/specifications?category=PET&description=pet%20store%20sample&name=Pet%20Store&type=JSON&url=https%3A%2F%2Fpetstore.swagger.io%2Fv2%2Fswagger.json&version=2.0" -H "accept: */*"
        ```
    or 
    
    * use swagger doc     
        open http://localhost:6200/swagger-ui.html#/Management/addUsingPOST
    
        input form
        - category:  Group name to classify
        - description: description
        - name: api system name
        - type: api system swagger type (Json or yaml)
        - headers: headers required when calling api ex) authentication, sample user id, etc...
        - version: api system swagger document spec version 
* crawling page 
    ```js
      curl -X PUT "http://localhost:6200/crawling/manual" -H "accept: */*"
    ```
*  refresh http://localhost:6200/swagger-ui.html 
  
  
# Note

To do the api test in giant-otter, you need to do the following:
1. Registering the api system should solve the cors problem.
2. The api spec is automatically crawled every 5 minutes.
  
