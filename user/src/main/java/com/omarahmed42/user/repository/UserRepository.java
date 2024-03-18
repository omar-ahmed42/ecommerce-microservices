package com.omarahmed42.user.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.omarahmed42.user.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

}
