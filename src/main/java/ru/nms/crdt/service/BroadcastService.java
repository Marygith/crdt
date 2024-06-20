package ru.nms.crdt.service;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.nms.crdt.tree.InternalPosition;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BroadcastService {

    private final DiscoveryClient discoveryClient;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${spring.application.name}")
    private String serviceId;

    @Value("${server.port}")
    private int port;

    public void send(InternalPosition position) {
        List<ServiceInstance> instances = discoveryClient.getInstances(serviceId);
        instances.forEach(instance -> {
            if (instance.getPort() != port) {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);

                HttpEntity<InternalPosition> request = new HttpEntity<>(position, headers);

                restTemplate.postForObject(createUrl(instance), request, Void.class);
            }
        });
    }

    private String createUrl(ServiceInstance instance) {
        return "http://" + instance.getHost() + ":" + instance.getPort() + "/receive";
    }
}
