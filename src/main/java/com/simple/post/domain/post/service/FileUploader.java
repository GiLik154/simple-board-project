package com.simple.post.domain.post.service;

import com.simple.post.exception.NotFoundFileException;

import java.io.File;
import java.io.IOException;

public interface FileUploader { // move
    /**
     * 파일을 서버에 업로드 한다.
     * 업로드 할 때 디렉토리는 '/files/회사명/저장시간.확장자' 이다.
     * 디렉토리가 없을때는 디렉토리를 생성한다.
     * 파일이 없을때에는 {@link NotFoundFileException}을 발생시킨다.
     *
     * @param company 저장한 회사의 이름
     * @param file    저장된 파일
     * @return 저장된 파일의 Path
     * @throws NotFoundFileException 파일이 없을 때 발생하는 익셉션
     */
    String upload(String company, File file) throws IOException;
}
