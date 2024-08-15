package com.example.user_server.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConfiguration {

    @Bean
    public NewTopic feedTopic() {
        return new NewTopic("user.follow", 1, (short) 1);
    }
}
