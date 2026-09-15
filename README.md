# giant-otter
This API application collects and shows several swagger apis.

[![Build Status](https://github.com/stray-cat-developers/giant-otter/actions/workflows/gradle.yml/badge.svg)](https://github.com/stray-cat-developers/giant-otter)

## New Features!
### v1.1.2
- Accessing the host root (e.g. `http://127.0.0.1:6200/`) now redirects to `/swagger-ui/index.html`.
- Fixed the swagger list sort key to use the display name instead of the original document URL.
- Fixed swagger-ui listing still showing an entry after its DB record was deleted and flushed.
- Fixed swagger specs sharing the same group being deduplicated against each other in the UI list.
- Fixed local profile's `ddl-auto` wiping data on every app restart.
- Added standalone restart and macOS autostart registration scripts.
- `docker-compose up` now waits for the Docker daemon to be ready before starting.
- Patched dependency vulnerabilities that didn't change application behavior.
### v1.1.1
- Fixed a 400 error (`No enum constant ... Method.undefined`) when swagger-ui's own spec/definition-loading requests (e.g. the "Select a definition" dropdown) went through `/swagger-proxy`, since those requests carry no HTTP method.
- Fixed a 500 error (`MalformedURLException: no protocol`) where `/swagger-proxy` incorrectly proxied same-origin, relative-path requests (like the docket endpoint below) instead of only proxying external, cross-origin spec URLs.
- Fixed docket lookups (`/swagger/specifications/{id}/docket`) failing with `state should be: hexString has 24 characters`, caused by the cache building the docket URL from the database's numeric primary key instead of the intended ObjectId.
- MySQL data is now persisted with a named Docker volume, so it no longer disappears when the `docker-compose` container is recreated.
- Patched dependency vulnerabilities (Spring Boot, httpclient5, MySQL driver, log4j2, and a few transitive libraries) to the extent that didn't change application behavior.
### v1.0.1
- Fixed the 'org.springframework.hateoas.mediatype.PropertyUtils' cannot find read-write method error
### v1.0.0
- You can view API documentation from multiple API servers in one place using the OpenAPI Spec.
- Automatically convert API server documentation with 2.x Spec to 3.x Spec.
- Update the API Spec every 5 minutes by crawling for changes.
- APIs can be queried by group.


# Installation
### Quick start
Java, Docker must be installed before starting.
Standalone system using embedded mongo database.

```sh
git clone https://github.com/stray-cat-developers/giant-otter.git
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
  
