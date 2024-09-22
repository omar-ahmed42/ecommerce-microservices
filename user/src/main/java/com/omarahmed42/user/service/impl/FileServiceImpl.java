package com.omarahmed42.user.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.stereotype.Service;

import com.omarahmed42.user.service.FileService;

@Service
public class FileServiceImpl implements FileService {
    public long copy(InputStream in, Path target, CopyOption... options) throws IOException {
        if (Files.notExists(target.getParent())) Files.createDirectories(target.getParent());
        return Files.copy(in, target, options);
    }
}
