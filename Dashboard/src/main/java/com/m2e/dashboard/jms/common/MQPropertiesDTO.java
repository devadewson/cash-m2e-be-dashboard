package com.m2e.dashboard.jms.common;

import lombok.Data;

import java.io.Serializable;

@Data
public class MQPropertiesDTO implements Serializable {
    private QueueManager queueManager;
    private Ccdt ccdt;
    private Queue myQueue;
    private Queue myv23Queue;
    private Queue sgQueue;
    private Long timeout;
    private String env;
    private String myver;
    private String ouver;

    @Data
    public static class QueueManager {
        private String node;
    }

    @Data
    public static class Ccdt {
        private String url;
    }

    @Data
    public static class Queue {
        private String request;
        private String response;
    }
}
