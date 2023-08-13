package com.simple.post.domain.post.service;

import com.simple.post.exception.NotFoundFileException;

import java.io.IOException;

public interface FileDeleter { // move
    /**
     * 파일을 삭제한다.
     * FilePath에 파일이 없으면 {@link NotFoundFileException} 발생.
     *
     * @param filePath 삭제하려는 파일의 Path
     * @throws NotFoundFileException Path에 해당 파일이 없으면 발생.
     */
    void delete(String filePath) throws IOException;
}
