package com.simple.post.application;

import com.simple.post.controller.FileConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Component
public class MultipartToFileConverter implements FileConverter {
    /**
     * {@link MultipartFile}을 {@link File}로 변환하는 로직
     * File을 임시 저장 한 후 삭제함.
     */
    @Override
    public File convert(MultipartFile multipartFile) throws IOException {
        if (multipartFile != null && !multipartFile.isEmpty() && multipartFile.getOriginalFilename() != null) {
            File file = new File(multipartFile.getOriginalFilename());
            file.createNewFile();

            try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
                fileOutputStream.write(multipartFile.getBytes());
            } catch (Exception e) {
                e.printStackTrace();
            }

            return file;
        }

        return null;
    }
}
