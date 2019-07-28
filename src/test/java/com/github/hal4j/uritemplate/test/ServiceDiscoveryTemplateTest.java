package com.github.hal4j.uritemplate.test;

import com.github.hal4j.uritemplate.ParamHolder;
import com.github.hal4j.uritemplate.URIBuilder;
import com.github.hal4j.uritemplate.URITemplate;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;

import static com.github.hal4j.uritemplate.ParamHolder.prefixed;
import static com.github.hal4j.uritemplate.URIBuilder.uri;
import static com.github.hal4j.uritemplate.URIFactory.templateUri;
import static com.github.hal4j.uritemplate.URITemplateVariable.template;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ServiceDiscoveryTemplateTest {

    private static final Map<String, String> SERVICE_REGISTRY = Collections.singletonMap("myservice", "i2.example.com");
    public static final ParamHolder SERVICES = prefixed("discovery", SERVICE_REGISTRY);

    @Test
    void serviceShouldBuildTemplateBasedOnIncomingRequest() {
        URITemplate template = uri("http", "i1.example.com", 8480) // request
                .host(template("discovery.myservice")) // allow use of discovery
                .path().join("api/v1", "100") // set up API endpoint reference
                .asTemplate();
        assertEquals("http://{discovery.myservice}:8480/api/v1/100", template.toString());
    }

    @Test
    void internalClientsShouldExpandHostname() {
        URITemplate template = templateUri("http://{discovery.myservice}:8480/api/v1/100");
        URITemplate expanded = template.expand(SERVICES);
        assertEquals("http://i2.example.com:8480/api/v1/100", expanded.toString());
    }

    @Test
    void externalClientsShouldMapTheEndpoint() {
        URITemplate template = templateUri("http://{discovery.myservice}:8480/api/v1/100");
        URIBuilder builder = template.expand(SERVICES)
                .toBuilder()
                .scheme("https")
                .host("api.example.com")
                .defaultPort()
                .path().replace("api", "myservice");
        assertEquals("https://api.example.com/myservice/v1/100", builder.toString());
    }

}
