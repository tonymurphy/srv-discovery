package com.gumtree.discovery.srv;

import com.spotify.dns.LookupResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        LoadBalancer loadBalancer = LoadBalancer.buildLoadBalancer();
        Optional<LookupResult> lookupResult = loadBalancer.getSrvAddress("bapi");
        if(lookupResult.isPresent()) {
            LOGGER.info("LookupResult {}", lookupResult.get());
        } else {
            LOGGER.error("Failed to lookup service");
        }
    }

}
