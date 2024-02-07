package io.mustelidae.grantotter.domain.spec

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.Test
import org.springdoc.core.properties.SpringDocConfigProperties

/**
 * TestSet https://github.com/OAI/OpenAPI-Specification/tree/main/examples
 */
class SpecConvertorTest {

    @Test
    fun convertTest() {
        // Given
        val apiV2 = """
            {
              "swagger": "2.0",
              "info": {
                "title": "Simple API overview",
                "version": "v2"
              },
              "paths": {
                "/": {
                  "get": {
                    "operationId": "listVersionsv2",
                    "summary": "List API versions",
                    "produces": [
                      "application/json"
                    ],
                    "responses": {
                      "200": {
                        "description": "200 300 response",
                        "examples": {
                          "application/json": "{\n    \"versions\": [\n        {\n            \"status\": \"CURRENT\",\n            \"updated\": \"2011-01-21T11:33:21Z\",\n            \"id\": \"v2.0\",\n            \"links\": [\n                {\n                    \"href\": \"http://127.0.0.1:8774/v2/\",\n                    \"rel\": \"self\"\n                }\n            ]\n        },\n        {\n            \"status\": \"EXPERIMENTAL\",\n            \"updated\": \"2013-07-23T11:33:21Z\",\n            \"id\": \"v3.0\",\n            \"links\": [\n                {\n                    \"href\": \"http://127.0.0.1:8774/v3/\",\n                    \"rel\": \"self\"\n                }\n            ]\n        }\n    ]\n}"
                        }
                      },
                      "300": {
                        "description": "200 300 response",
                        "examples": {
                          "application/json": "{\n    \"versions\": [\n        {\n            \"status\": \"CURRENT\",\n            \"updated\": \"2011-01-21T11:33:21Z\",\n            \"id\": \"v2.0\",\n            \"links\": [\n                {\n                    \"href\": \"http://127.0.0.1:8774/v2/\",\n                    \"rel\": \"self\"\n                }\n            ]\n        },\n        {\n            \"status\": \"EXPERIMENTAL\",\n            \"updated\": \"2013-07-23T11:33:21Z\",\n            \"id\": \"v3.0\",\n            \"links\": [\n                {\n                    \"href\": \"http://127.0.0.1:8774/v3/\",\n                    \"rel\": \"self\"\n                }\n            ]\n        }\n    ]\n}"
                        }
                      }
                    }
                  }
                },
                "/v2": {
                  "get": {
                    "operationId": "getVersionDetailsv2",
                    "summary": "Show API version details",
                    "produces": [
                      "application/json"
                    ],
                    "responses": {
                      "200": {
                        "description": "200 203 response",
                        "examples": {
                          "application/json": "{\n    \"version\": {\n        \"status\": \"CURRENT\",\n        \"updated\": \"2011-01-21T11:33:21Z\",\n        \"media-types\": [\n            {\n                \"base\": \"application/xml\",\n                \"type\": \"application/vnd.openstack.compute+xml;version=2\"\n            },\n            {\n                \"base\": \"application/json\",\n                \"type\": \"application/vnd.openstack.compute+json;version=2\"\n            }\n        ],\n        \"id\": \"v2.0\",\n        \"links\": [\n            {\n                \"href\": \"http://127.0.0.1:8774/v2/\",\n                \"rel\": \"self\"\n            },\n            {\n                \"href\": \"http://docs.openstack.org/api/openstack-compute/2/os-compute-devguide-2.pdf\",\n                \"type\": \"application/pdf\",\n                \"rel\": \"describedby\"\n            },\n            {\n                \"href\": \"http://docs.openstack.org/api/openstack-compute/2/wadl/os-compute-2.wadl\",\n                \"type\": \"application/vnd.sun.wadl+xml\",\n                \"rel\": \"describedby\"\n            },\n            {\n              \"href\": \"http://docs.openstack.org/api/openstack-compute/2/wadl/os-compute-2.wadl\",\n              \"type\": \"application/vnd.sun.wadl+xml\",\n              \"rel\": \"describedby\"\n            }\n        ]\n    }\n}"
                        }
                      },
                      "203": {
                        "description": "200 203 response",
                        "examples": {
                          "application/json": "{\n    \"version\": {\n        \"status\": \"CURRENT\",\n        \"updated\": \"2011-01-21T11:33:21Z\",\n        \"media-types\": [\n            {\n                \"base\": \"application/xml\",\n                \"type\": \"application/vnd.openstack.compute+xml;version=2\"\n            },\n            {\n                \"base\": \"application/json\",\n                \"type\": \"application/vnd.openstack.compute+json;version=2\"\n            }\n        ],\n        \"id\": \"v2.0\",\n        \"links\": [\n            {\n                \"href\": \"http://23.253.228.211:8774/v2/\",\n                \"rel\": \"self\"\n            },\n            {\n                \"href\": \"http://docs.openstack.org/api/openstack-compute/2/os-compute-devguide-2.pdf\",\n                \"type\": \"application/pdf\",\n                \"rel\": \"describedby\"\n            },\n            {\n                \"href\": \"http://docs.openstack.org/api/openstack-compute/2/wadl/os-compute-2.wadl\",\n                \"type\": \"application/vnd.sun.wadl+xml\",\n                \"rel\": \"describedby\"\n            }\n        ]\n    }\n}"
                        }
                      }
                    }
                  }
                }
              },
              "consumes": [
                "application/json"
              ]
            }        
        """

        val apiV3 = """
        {
          "openapi" : "3.0.1",
          "info" : {
            "title" : "Simple API overview",
            "version" : "v2"
          },
          "servers" : [ {
            "url" : "/"
          } ],
          "paths" : {
            "/" : {
              "get" : {
                "summary" : "List API versions",
                "operationId" : "listVersionsv2",
                "responses" : {
                  "200" : {
                    "description" : "200 300 response",
                    "content" : {
                      "application/json" : {
                        "example" : "{\n    \"versions\": [\n        {\n            \"status\": \"CURRENT\",\n            \"updated\": \"2011-01-21T11:33:21Z\",\n            \"id\": \"v2.0\",\n            \"links\": [\n                {\n                    \"href\": \"http://127.0.0.1:8774/v2/\",\n                    \"rel\": \"self\"\n                }\n            ]\n        },\n        {\n            \"status\": \"EXPERIMENTAL\",\n            \"updated\": \"2013-07-23T11:33:21Z\",\n            \"id\": \"v3.0\",\n            \"links\": [\n                {\n                    \"href\": \"http://127.0.0.1:8774/v3/\",\n                    \"rel\": \"self\"\n                }\n            ]\n        }\n    ]\n}"
                      }
                    }
                  },
                  "300" : {
                    "description" : "200 300 response",
                    "content" : {
                      "application/json" : {
                        "example" : "{\n    \"versions\": [\n        {\n            \"status\": \"CURRENT\",\n            \"updated\": \"2011-01-21T11:33:21Z\",\n            \"id\": \"v2.0\",\n            \"links\": [\n                {\n                    \"href\": \"http://127.0.0.1:8774/v2/\",\n                    \"rel\": \"self\"\n                }\n            ]\n        },\n        {\n            \"status\": \"EXPERIMENTAL\",\n            \"updated\": \"2013-07-23T11:33:21Z\",\n            \"id\": \"v3.0\",\n            \"links\": [\n                {\n                    \"href\": \"http://127.0.0.1:8774/v3/\",\n                    \"rel\": \"self\"\n                }\n            ]\n        }\n    ]\n}"
                      }
                    }
                  }
                }
              }
            },
            "/v2" : {
              "get" : {
                "summary" : "Show API version details",
                "operationId" : "getVersionDetailsv2",
                "responses" : {
                  "200" : {
                    "description" : "200 203 response",
                    "content" : {
                      "application/json" : {
                        "example" : "{\n    \"version\": {\n        \"status\": \"CURRENT\",\n        \"updated\": \"2011-01-21T11:33:21Z\",\n        \"media-types\": [\n            {\n                \"base\": \"application/xml\",\n                \"type\": \"application/vnd.openstack.compute+xml;version=2\"\n            },\n            {\n                \"base\": \"application/json\",\n                \"type\": \"application/vnd.openstack.compute+json;version=2\"\n            }\n        ],\n        \"id\": \"v2.0\",\n        \"links\": [\n            {\n                \"href\": \"http://127.0.0.1:8774/v2/\",\n                \"rel\": \"self\"\n            },\n            {\n                \"href\": \"http://docs.openstack.org/api/openstack-compute/2/os-compute-devguide-2.pdf\",\n                \"type\": \"application/pdf\",\n                \"rel\": \"describedby\"\n            },\n            {\n                \"href\": \"http://docs.openstack.org/api/openstack-compute/2/wadl/os-compute-2.wadl\",\n                \"type\": \"application/vnd.sun.wadl+xml\",\n                \"rel\": \"describedby\"\n            },\n            {\n              \"href\": \"http://docs.openstack.org/api/openstack-compute/2/wadl/os-compute-2.wadl\",\n              \"type\": \"application/vnd.sun.wadl+xml\",\n              \"rel\": \"describedby\"\n            }\n        ]\n    }\n}"
                      }
                    }
                  },
                  "203" : {
                    "description" : "200 203 response",
                    "content" : {
                      "application/json" : {
                        "example" : "{\n    \"version\": {\n        \"status\": \"CURRENT\",\n        \"updated\": \"2011-01-21T11:33:21Z\",\n        \"media-types\": [\n            {\n                \"base\": \"application/xml\",\n                \"type\": \"application/vnd.openstack.compute+xml;version=2\"\n            },\n            {\n                \"base\": \"application/json\",\n                \"type\": \"application/vnd.openstack.compute+json;version=2\"\n            }\n        ],\n        \"id\": \"v2.0\",\n        \"links\": [\n            {\n                \"href\": \"http://23.253.228.211:8774/v2/\",\n                \"rel\": \"self\"\n            },\n            {\n                \"href\": \"http://docs.openstack.org/api/openstack-compute/2/os-compute-devguide-2.pdf\",\n                \"type\": \"application/pdf\",\n                \"rel\": \"describedby\"\n            },\n            {\n                \"href\": \"http://docs.openstack.org/api/openstack-compute/2/wadl/os-compute-2.wadl\",\n                \"type\": \"application/vnd.sun.wadl+xml\",\n                \"rel\": \"describedby\"\n            }\n        ]\n    }\n}"
                      }
                    }
                  }
                }
              }
            }
          },
          "components" : { },
          "x-original-swagger-version" : "2.0"
        }
        """.trimIndent()

        val specConvertor = SpecConvertor(SwaggerSpec.Type.JSON, apiV2)
        val springDocConfigProperties = SpringDocConfigProperties()
        val result = specConvertor.convertToV3(springDocConfigProperties)

        result shouldBe apiV3
    }

    @Test
    fun versionJson() {
        // Given
        val apiV2 = """
            {
              "swagger": "2.0",
              "info": {
                "title": "Simple API overview",
                "version": "v2"
              }
            }  
        """.trimIndent()

        val apiV3 = """
            {
              "openapi": "3.0.0",
              "info": {
                "title": "Simple API overview",
                "version": "2.0.0"
              }
            }              
        """.trimIndent()

        // When
        val v2Version = SpecConvertor(SwaggerSpec.Type.JSON, apiV2).getVersion()
        val v3Version = SpecConvertor(SwaggerSpec.Type.JSON, apiV3).getVersion()

        // Then
        v2Version shouldNotBe null
        v2Version shouldBe "2.0"

        v3Version shouldNotBe null
        v3Version shouldBe "3.0.0"
    }

    @Test
    fun versionYaml() {
        // Given
        val apiV2 = """
            swagger: "2.0"
            info:
              title: Simple API overview
              version: v2
        """.trimIndent()

        val apiV3 = """
        openapi: "3.0.0"
        info:
          title: Simple API overview
          version: 2.0.0            
        """.trimIndent()

        // When
        val v2Version = SpecConvertor(SwaggerSpec.Type.YAML, apiV2)
        val v3Version = SpecConvertor(SwaggerSpec.Type.YAML, apiV3)

        // Then
        v2Version.getVersion() shouldNotBe null
        v2Version.getVersion() shouldBe "2.0"

        v3Version.getVersion() shouldNotBe null
        v3Version.getVersion() shouldBe "3.0.0"
    }
}
