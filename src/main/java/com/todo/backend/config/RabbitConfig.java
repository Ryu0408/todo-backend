package com.todo.backend.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

@Configuration
@EnableRabbit
public class RabbitConfig {

    @Bean
    public DirectExchange fileExchange(@Value("${app.rabbit.exchange:file.exchange}") String exchangeName) {
        return ExchangeBuilder.directExchange(exchangeName).durable(true).build();
    }

    @Bean
    public Queue fileQueue(@Value("${app.rabbit.queue:file.queue}") String queueName) {
        return QueueBuilder.durable(queueName).build();
    }

    @Bean
    public Binding fileBinding(DirectExchange fileExchange, Queue fileQueue,
                               @Value("${app.rabbit.routing-key:file.routing}") String routingKey) {
        return BindingBuilder.bind(fileQueue).to(fileExchange).with(routingKey);
    }

    // ✅ 이름을 messageConverter로
    @Bean
    public org.springframework.amqp.support.converter.MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // ✅ RabbitTemplate에 컨버터 주입
    @Bean
    public org.springframework.amqp.rabbit.core.RabbitTemplate rabbitTemplate(
            org.springframework.amqp.rabbit.connection.ConnectionFactory cf,
            org.springframework.amqp.support.converter.MessageConverter mc) {
        var rt = new org.springframework.amqp.rabbit.core.RabbitTemplate(cf);
        rt.setMessageConverter(mc);
        return rt;
    }

    // (선택) Listener 쪽에도 확실히 적용
    @Bean
    public org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            org.springframework.amqp.rabbit.connection.ConnectionFactory cf,
            org.springframework.amqp.support.converter.MessageConverter mc) {
        var f = new org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory();
        f.setConnectionFactory(cf);
        f.setMessageConverter(mc);
        return f;
    }
}
