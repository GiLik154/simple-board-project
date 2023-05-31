package com.simple.post.infrastructure.post;

import com.simple.post.domain.post.service.FileDeleter;
import com.simple.post.exception.NotFoundFileException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FileDeleterTest {
    private final FileDeleter fileDeleter;

    @Autowired
    public FileDeleterTest(FileDeleter fileDeleter) {
        this.fileDeleter = fileDeleter;
    }

    @Test
    void 파일_삭제_정상작동() throws IOException {
        Files.createDirectories(Path.of("files/testCompany/"));
        Files.createFile(Path.of("files/testCompany/test.jpg"));

        String path = "files/testCompany/";
        File savedFileFolder = new File(path);

        fileDeleter.delete("files/testCompany/test.jpg");

        assertEquals(0, savedFileFolder.listFiles().length);
    }

    @Test
    void 파일_삭제_파일_없음() throws IOException {
        NotFoundFileException e = assertThrows(NotFoundFileException.class, () ->
                fileDeleter.delete("files/testCompany/test.jpg"));

        assertEquals("Not Found File", e.getMessage());
    }
}