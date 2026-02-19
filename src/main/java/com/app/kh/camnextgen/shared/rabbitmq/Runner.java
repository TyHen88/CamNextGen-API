package com.app.kh.camnextgen.shared.rabbitmq;

import com.app.kh.camnextgen.shared.config.RabbitConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class Runner implements CommandLineRunner {

    private final RabbitTemplate rabbitTemplate;
    private final Receiver receiver;

    public Runner(Receiver receiver, RabbitTemplate rabbitTemplate) {
        this.receiver = receiver;
        this.rabbitTemplate = rabbitTemplate;
    }
    @Override
    public void run(String... args) throws Exception {
        System.out.println("Sending message...");
        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE, "foo.bar.baz", "Hello from RabbitMQ!");
        boolean received = receiver.await(10, TimeUnit.SECONDS);
        if (!received) {
            System.out.println("No message received within timeout.");
        }
    }
}
