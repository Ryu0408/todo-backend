package com.todo.backend.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

@EnableRabbit
@Configuration
public class RabbitConfig {

    // application.yml 의 app.rabbit.* 값을 주입
    @Value("${app.rabbit.exchange:file.exchange}")
    private String exchangeName;

    @Value("${app.rabbit.queue:file.queue}")
    private String queueName;

    @Value("${app.rabbit.routing-key:file.routing}")
    private String routingKey;

    @Bean
    public DirectExchange fileExchange() {
        return ExchangeBuilder.directExchange(exchangeName).durable(true).build();
    }

    @Bean
    public Queue fileQueue() {
        return QueueBuilder.durable(queueName).build();
    }

    @Bean
    public Binding fileBinding(DirectExchange fileExchange, Queue fileQueue) {
        return BindingBuilder.bind(fileQueue).to(fileExchange).with(routingKey);
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        // RabbitTemplate / @RabbitListener 가 이 컨버터를 사용해 DTO <-> JSON 변환
        return new Jackson2JsonMessageConverter();
    }
}
