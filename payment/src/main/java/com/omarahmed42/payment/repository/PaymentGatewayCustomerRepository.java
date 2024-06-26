package com.omarahmed42.payment.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.omarahmed42.payment.model.PaymentGatewayCustomer;

@Repository
public interface PaymentGatewayCustomerRepository extends JpaRepository<PaymentGatewayCustomer, Long> {

    Optional<PaymentGatewayCustomer> findByUserId(String userId);

}
