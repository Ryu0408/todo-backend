// src/main/java/com/todo/backend/config/RabbitConfig.java
package com.todo.backend.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitConfig {

    @Bean
    public DirectExchange fileExchange(){
        return ExchangeBuilder.directExchange("file.exchange").durable(true).build();
    }

    @Bean
    public Queue fileQueue(){
        return QueueBuilder.durable("file.queue").build();
    }

    @Bean
    public Binding fileBinding(DirectExchange fileExchange, Queue fileQueue){
        return BindingBuilder.bind(fileQueue).to(fileExchange).with("file.routing");
    }

    @Bean
    public org.springframework.amqp.support.converter.MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }
}
