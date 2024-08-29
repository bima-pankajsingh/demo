package com.example.demo.configs;

import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.eureka.EurekaClientConfigBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import java.util.List;

@Primary
@Profile("!default")
@Configuration
@EnableEurekaClient
public class EurekaClientConfig extends EurekaClientConfigBean {

    /**
     * Assert only one zone: defaultZone, but multiple environments.
     */
    public List<String> getEurekaServerServiceUrls(String myZone) {
        return super.getEurekaServerServiceUrls(myZone);
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

}
