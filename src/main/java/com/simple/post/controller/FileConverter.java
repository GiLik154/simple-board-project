package com.simple.post.controller;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

public interface FileConverter {
    File convert(MultipartFile multipartFile) throws IOException;
}
