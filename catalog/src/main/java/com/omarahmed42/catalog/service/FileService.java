package com.omarahmed42.catalog.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.CopyOption;
import java.nio.file.Path;

public interface FileService {
    long copy(InputStream in, Path target, CopyOption... options) throws IOException;
}
