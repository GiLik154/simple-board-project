package com.simple.post.application;

import com.simple.post.controller.FileConverter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MultipartToFileConverterTest {
    private final FileConverter fileConverter;

    @Autowired
    public MultipartToFileConverterTest(FileConverter fileConverter) {
        this.fileConverter = fileConverter;
    }

    @Test
    void 파일_컨버팅_정상작동() throws IOException {
        byte[] fileByte = "test-image".getBytes();
        MockMultipartFile testFile = new MockMultipartFile("multipartFile", "test.jpg", "image/jpeg", fileByte);

        File file = fileConverter.convert(testFile);

        assertEquals("test.jpg", file.getPath());
    }

    @Test
    void 파일_컨버팅_null() throws IOException {
        MockMultipartFile testFile = new MockMultipartFile("multipartFile", "test.jpg", "image/jpeg", (byte[]) null);

        File file = fileConverter.convert(testFile);

        assertNull(file);
    }
}