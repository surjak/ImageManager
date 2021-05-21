package com.image.manager.loadbalancer.edgewebclient;

import lombok.Getter;
import org.springframework.web.reactive.function.client.WebClient;

@Getter
public class EdgeWebClient {

    public static EdgeWebClient fromHost(String host) {
        return new EdgeWebClient(host, WebClient.create(host));
    }

    private final String host;
    private final WebClient webClient;

    private EdgeWebClient(String host, WebClient webClient) {
        this.host = host;
        this.webClient = webClient;
    }

}
