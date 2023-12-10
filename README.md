# uritemplate

---

## About this project

This library is a Java 8 implementation of [RFC 6570](https://www.rfc-editor.org/rfc/rfc6570) (URI template)  with extra 
features like a convenient URI and URI template builder, ':' operator and partial expansion.
The library is available in Maven Central repository and can be included as a dependency in your projects.

See also list of changes in the [release notes](RELEASE_NOTES.md).

## Examples
### 1. RFC 6570 support

Example 1.1: basic template parsing and expansion
```java
	String s = new URITemplate("http://www.example.com/api{?param*}")
			.expand("param", asList("1","2"))
			.toString();
    assertEquals("http://www.example.com/api?param=1&param=2", s);
```

Example 1.2: expansion with array
```java
	String s = new URITemplate("http://www.example.com/api{?param*}")
			.expand(asList("1","2"))
			.toString();
    assertEquals("http://www.example.com/api?param=1&param=2", s);
```

### 2. URI Builder

Example 2.1: URI builder DSL - paths, variables and expansions
```java
String s2 = new URIBuilder("http://www.example.com")
		.relative("api", "items", var("name"))
		.asTemplate()
		.expand("name", "1")
		.toString();
assertEquals("http://www.example.com/api/items/1", s2);
```

Example 2.2: URI builder DSL - pre-encoded variables
```java
String s3 = new URIBuilder("http://www.example")
		.append(var("tld").preEncoded())
		.toString();
assertEquals("http://www.example{+tld}", s3);
	
String expanded = new URITemplate("http://www.example{+tld}")
		.expand("tld", "{.domain}")
		.toString();
assertEquals("http://www.example{.domain}", expanded);
```

Example 2.3: URI builder DSL - path variables
```java
String s4 = new URIBuilder("http://www.example.com")
		.append(pathVariable("name"))
		.toString();
assertEquals("http://www.example.com{/name}", s4);
```

Example 2.4: URI builder DSL - escaping in path
```java
String s5 = new URIBuilder("http://www.example.com")
		.relative("api", "items", "{name}")
		.toString();
assertEquals("http://www.example.com/api/items/%7Bname%7D", s5);
```

Example 2.5: escaping of query parameters
```java
String s6 = new URIBuilder("http://www.example.com?val1=%25")
		.queryParam("val2", "%", "$", "#")
		.queryParam("val3", "hello")
		.toString();
assertEquals("http://www.example.com?val1=%25&val2=%25&val2=$&val2=%23&val3=hello", s6);
```

Example 2.6: advanced composition of URI components with appropriate delimiters - `join()` method
```java
String s = new URIBuilder("http://www.example.com?val1=%25")
                .path().join("api", "subpath")
                .toString();
assertEquals("http://www.example.com/api/subpath?val1=%25", s);
```
The approach above works with `host()`, `path()`, `query()` and `fragment()` methods.

Example 2.7: advanced composition of URI components - `append()` method
```java
String s = new URIBuilder("http://www.example.com?val1=%25")
                .path().append("/api", "subpath")
                .toString();
assertEquals("http://www.example.com/apisubpath?val1=%25", s);
```

### 3. Extras

Example 3.1: ':' as supported delimiter and URI template operator
```java
String s = new URITemplate("rel{:param*}")
		.expand("param", asList("1","2"))
		.toString();
assertEquals("rel:1:2", s);
```

Example 3.2: partial template expansion and URITemplateParser
```java
String uri = "http://www.example.com{?p1,p2,p3*}";
String expected = "http://www.example.com?p2=v2&p2=v3{&p1,p3*}";

Map<String, Object> map = new HashMap<>();
map.put("p2", asList("v2", "v3"));

String result = URITemplateParser.parseAndExpand(uri, true, map);

assertEquals(expected, result);
```
