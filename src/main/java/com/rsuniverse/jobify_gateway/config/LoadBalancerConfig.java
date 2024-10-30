package com.rsuniverse.jobify_gateway.config;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import reactor.core.publisher.Flux;

import java.util.List;

//@Configuration
public class LoadBalancerConfig implements ServiceInstanceListSupplier {

    @Override
    public String getServiceId() {
        return "";
    }

    @Override
    public Flux<List<ServiceInstance>> get() {
        return null;
    }
}
