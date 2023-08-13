package com.simple.post.domain.post.service;

import com.simple.post.domain.post.service.command.PostCreatorCommand;

import java.io.File;
import java.io.IOException;

public interface PostCreator {
    /**
     * Post를 생성하는 서비스
     * {@link PostCreatorCommand}을 통해 생성할 정보를 받아와서 Post 생성
     * File이 null이 아닐 경우 파일을 업로드
     *
     * @param postCreatorCommand Post를 생성할 정보들이 담긴 커맨드
     * @param file               Post에 추가할 파일
     * @throws IOException FileUpload와 관련된 익셉션 발생 가능
     */
    void create(PostCreatorCommand postCreatorCommand, File file) throws IOException;
}
