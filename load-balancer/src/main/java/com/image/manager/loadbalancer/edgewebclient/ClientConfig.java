package com.image.manager.loadbalancer.edgewebclient;
import lombok.Data;
/**
 * Created by surjak on 07.06.2021
 */
@Data
public class ClientConfig {
    private int pendingAcquireMaxCount;
    private int maxConnections;
    private int pendingAcquireTimeout;
    private int maxInMemorySize;
}
