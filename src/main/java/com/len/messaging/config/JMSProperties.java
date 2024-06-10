package com.len.messaging.config;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties(prefix="jms")
public class JMSProperties {

    private String queue;
    private String rateQueue;
    private String rateReplyQueue;
    private String topic;
    private String brokerUrl;
    private String user;
    private String password;

}