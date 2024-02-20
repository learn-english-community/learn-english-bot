package bot.consul;

import java.net.URI;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Component;

/** Wrapper component that provides extra functionality on top of a DiscoveryClient. */
@Component
public class ConsulDiscoveryClientWrapper {
    private final DiscoveryClient discoveryClient;

    @Autowired
    private ConsulDiscoveryClientWrapper(DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
    }

    /**
     * Gets a Consul service URI by a given service name.
     *
     * <p>Use this if you want to communicate with an external service inside the ecosystem.
     *
     * @param serviceName The service name to put
     * @return An optional instance of the service's URI
     */
    public Optional<URI> getService(String serviceName) {
        return discoveryClient.getInstances(serviceName).stream()
                .findFirst()
                .map(ServiceInstance::getUri);
    }
}
