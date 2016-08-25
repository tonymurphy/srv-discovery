package com.example.discovery.srv;

import com.spotify.dns.LookupResult;

import java.util.List;
import java.util.Optional;
import java.util.Random;

public class RandomLoadBalancingStrategy implements LoadBalancingStrategy {

    @Override
    public Optional<LookupResult> next(List<LookupResult> nodes) {
        if(nodes.isEmpty()) {
            return Optional.empty();
        }
        Random rand = new Random();
        int n = rand.nextInt(nodes.size());
        return Optional.of(nodes.get(n));
    }
}
