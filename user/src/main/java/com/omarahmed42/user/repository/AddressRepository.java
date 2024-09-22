package com.omarahmed42.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.omarahmed42.user.model.Address;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    java.util.List<Address> findAllByUser_Id(java.util.UUID userId);
}
