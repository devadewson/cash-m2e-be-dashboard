package com.m2e.dashboard.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class TaskActionResolverFactory {

    private final Map<String, TaskActionResolver<?, ?, ?>> registry = new HashMap<>();

    @Autowired
    public TaskActionResolverFactory(List<TaskActionResolver<?, ?, ?>> resolvers) {
        resolvers.forEach(resolver ->
                registry.put(resolver.getSupportedTaskType(), resolver)
        );
    }

    @SuppressWarnings("unchecked")
    public <S, A, D> Optional<TaskActionResolver<S, A, D>> getResolver(String taskType) {
        return Optional.ofNullable((TaskActionResolver<S, A, D>) registry.get(taskType));
    }
}
