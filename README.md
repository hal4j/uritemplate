# uritemplate
This library is a Java implementation of RFC 6570 (URI template).
Usages:
```java
String uriString = URIFactory.template("http://www.example.com/api{?param*}")
               .expand("param", asList("1","2"))
                .toString();
```