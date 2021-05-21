package com.image.manager.loadbalancer.edgewebclient;

import lombok.Data;

import java.util.List;

@Data
public class EdgeWebClientProperties {

    private List<String> clientsIps;

}
