package com.omarahmed42.order.message.worker;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.omarahmed42.order.dto.response.OrderDetails;
import com.omarahmed42.order.service.OrderService;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CompleteOrderAdapter {

    private final OrderService orderService;

    @JobWorker(autoComplete = true, type = "complete-order", fetchVariables = {"orderId", "correlationId"}, maxJobsActive = 15)
    public Map<String, String> handle(ActivatedJob job) {
        OrderDetails orderDetails = orderService.completeOrder(Long.valueOf((String) job.getVariable("orderId")));

        Map<String, String> result = orderDetails.asMap();
        result.put("correlationId", (String) job.getVariable("correlationId"));
        return result;
    }
}
