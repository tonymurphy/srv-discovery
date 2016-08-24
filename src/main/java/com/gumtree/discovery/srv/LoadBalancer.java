package com.gumtree.discovery.srv;

import com.spotify.dns.DnsSrvResolver;
import com.spotify.dns.DnsSrvResolvers;
import com.spotify.dns.LookupResult;
import com.spotify.dns.statistics.DnsReporter;
import com.spotify.dns.statistics.DnsTimingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class LoadBalancer {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoadBalancer.class);

    private static final DnsReporter REPORTER = new StdoutReporter();

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
                .metered(REPORTER)
                .dnsLookupTimeoutMillis(1000)
                .build();
        return new LoadBalancer(strategy, resolver);
    }

    public static class StdoutReporter implements DnsReporter {
        @Override
        public DnsTimingContext resolveTimer() {
            return new DnsTimingContext() {
                private final long start = System.currentTimeMillis();

                @Override
                public void stop() {
                    final long now = System.currentTimeMillis();
                    final long diff = now - start;
                    LOGGER.info("Request took " + diff + "ms");
                }
            };
        }

        @Override
        public void reportFailure(Throwable error) {
            LOGGER.error("Error when resolving: ",error);
       }

        @Override
        public void reportEmpty() {
            LOGGER.error("Empty response from server.");
            System.out.println();
        }
    }
}
