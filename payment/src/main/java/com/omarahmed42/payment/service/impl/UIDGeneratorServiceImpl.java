package com.omarahmed42.payment.service.impl;

import org.springframework.stereotype.Service;

import com.google.protobuf.Int64Value;
import com.omarahmed42.payment.client.grpc.UIDGrpc;
import com.omarahmed42.payment.client.grpc.Uid;
import com.omarahmed42.payment.exception.InvalidIdException;
import com.omarahmed42.payment.service.IdGeneratorService;

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