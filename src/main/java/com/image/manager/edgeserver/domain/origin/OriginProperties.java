package com.image.manager.edgeserver.domain.origin;

import lombok.Data;

import java.util.List;

@Data
public class OriginProperties {

    private List<OriginHost> hosts;

    @Data
    public static class OriginHost {

        private String hostname;
        private String ip;
        private int maxConcurrentConnections;

    }

}
