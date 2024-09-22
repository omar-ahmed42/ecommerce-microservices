package com.omarahmed42.payment.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.omarahmed42.payment.model.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    @Query(value = "SELECT p FROM Payment p LEFT JOIN PaymentGatewayCustomer pgc ON p.gatewayCustomer.id = pgc.id WHERE p.id = :payment_id")
    Optional<Payment> findOne(@Param("payment_id") UUID paymentId);

    List<Payment> findAllByUserId(String userId);

}
