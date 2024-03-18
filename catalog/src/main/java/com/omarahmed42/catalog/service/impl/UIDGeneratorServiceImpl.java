package com.omarahmed42.catalog.service.impl;

import org.springframework.stereotype.Service;

import com.google.protobuf.Int64Value;
import com.omarahmed42.catalog.client.grpc.UIDGrpc;
import com.omarahmed42.catalog.client.grpc.Uid;
import com.omarahmed42.catalog.exception.InvalidIdException;
import com.omarahmed42.catalog.service.IdGeneratorService;

import net.devh.boot.grpc.client.inject.GrpcClient;

@Service
public class UIDGeneratorServiceImpl implements IdGeneratorService<Long> {

    @GrpcClient("uid-generator")
    private UIDGrpc.UIDBlockingStub uidGeneratorStub;

    @Override
    public Long generateId() {
        Int64Value uid = uidGeneratorStub.generateUid(Uid.Empty.getDefaultInstance());
        if (uid.getValue() <= 0)
            throw new InvalidIdException("Invalid generated ID");

        return uid.getValue();
    }

}