package com.omarahmed42.order.message.worker;

import org.springframework.stereotype.Component;

import com.omarahmed42.order.dto.message.Message;
import com.omarahmed42.order.dto.response.OrderDetails;
import com.omarahmed42.order.message.producer.MessageSender;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class OrderCompletedAdapter {

    private final MessageSender messageSender;

    @JobWorker(autoComplete = true, type = "order-completed", maxJobsActive = 15)
    public void handle(ActivatedJob job) {
        log.info("Processing Order completed in workflow");
        OrderDetails orderDetails = OrderDetails.fromMap(job.getVariablesAsMap());

        Message<OrderDetails> message = new Message<>("OrderCompletedEvent", orderDetails,
                (String) job.getVariable("correlationId"));

        messageSender.send(message);
    }
}
