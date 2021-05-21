package com.image.manager.loadbalancer.resolver;

import com.image.manager.loadbalancer.edgewebclient.EdgeWebClient;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

@Primary
@Component
@Profile("random")
public class RandomRoutingResolver extends AbstractRoutingResolver {

    private static final Random random = new Random(2);

    public RandomRoutingResolver(List<EdgeWebClient> clients) {
        super(clients);
    }

    @Override
    public EdgeWebClient resolve(String filename) {
        return super.clients.get(random.nextInt(this.size()));
    }

}
