package com.omarahmed42.catalog.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import com.omarahmed42.catalog.dto.response.AttachmentResponse;
import com.omarahmed42.catalog.model.Attachment;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AttachmentMapper {
    Attachment toEntity(AttachmentResponse attachmentResponse);

    AttachmentResponse toAttachmentResponse(Attachment attachment);

    List<AttachmentResponse> toAttachmentResponseList(List<Attachment> attachments);
}
