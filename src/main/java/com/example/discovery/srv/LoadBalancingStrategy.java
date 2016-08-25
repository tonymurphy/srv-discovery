package com.example.discovery.srv;

import com.spotify.dns.LookupResult;

import java.util.List;
import java.util.Optional;

public interface LoadBalancingStrategy {

    Optional<LookupResult> next(List<LookupResult> nodes);
}
