# Release Notes

## Version 2.0.0
This is a major update to the library with some breaking changes: please read the list of changes carefully 
and update your code!

### List of changes
* Implemented 100% coverage of test cases from RFC specification
* **Breaking change**: methods `URITemplate::expand` perform full expansion now, removing missing values.
* New group of methods `URITemplate::expandPartial` added to support partial expansion
* Partial expansion logic now depends on type of the operator: path, matrix and domain operators are expanded 
  hierarchically, missing variables for other operators are appended after the substituted values.

  Example:

  | Template                             | Substitution | Result                                  |
  |--------------------------------------|--------------|-----------------------------------------|
  | `https://www.example.com{/p1,p2,p3}` | p2=v2        | `https://www.example.com{/p1}/v2{/p3}`  |
  | `https://www.example.com{?p1,p2,p3}` | p2=v2        | `https://www.example.com?p2=v2{&p1,p3}` |

### Add to your project
#### Maven
```xml
    <dependency>
        <groupId>com.github.hal4j</groupId>
        <artifactId>uritemplate</artifactId>
        <version>2.0.0</version>
    </dependency>
```