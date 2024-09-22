package com.omarahmed42.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.omarahmed42.user.model.Attachment;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, Long> {

}
