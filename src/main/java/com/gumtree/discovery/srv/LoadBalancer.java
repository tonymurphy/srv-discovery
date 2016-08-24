package com.gumtree.discovery.srv;

import com.spotify.dns.DnsSrvResolver;
import com.spotify.dns.DnsSrvResolvers;
import com.spotify.dns.LookupResult;

import java.util.List;
import java.util.Optional;

public class LoadBalancer {

    private LoadBalancingStrategy loadBalancingStrategy;
    private DnsSrvResolver dnsSrvResolver;

    public LoadBalancer(LoadBalancingStrategy loadBalancingStrategy, DnsSrvResolver dnsSrvResolver) {
        this.loadBalancingStrategy = loadBalancingStrategy;
        this.dnsSrvResolver = dnsSrvResolver;
    }

    public Optional<LookupResult> getSrvAddress(String serviceName) {
        String srvName = serviceName + ".service.consul";
        List<LookupResult> nodes = dnsSrvResolver.resolve(srvName);
        return loadBalancingStrategy.next(nodes);
    }

    public static LoadBalancer buildLoadBalancer() {
        LoadBalancingStrategy strategy = new RandomLoadBalancingStrategy();
        DnsSrvResolver resolver = DnsSrvResolvers.newBuilder()
                .cachingLookups(true)
                .retainingDataOnFailures(true)
                //.metered(new CodahaleSpringBootReporter(metricsRegistry))
                .dnsLookupTimeoutMillis(1000)
                .build();
        return new LoadBalancer(strategy, resolver);
    }

}
