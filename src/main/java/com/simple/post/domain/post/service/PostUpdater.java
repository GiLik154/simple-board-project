package com.simple.post.domain.post.service;

import com.simple.post.domain.post.service.command.PostUpdaterCommand;
import com.simple.post.exception.NotMatchPasswordException;

import java.io.File;
import java.io.IOException;

public interface PostUpdater {
    /**
     * PostId를 통해 찾은 Post의 정보를 {@link PostUpdaterCommand}의 정보로 업데이트 하는 서비스
     * 찾은 Post의 password와 입력한 password 일치해야 업데이트
     * 일치하지 않을 경우 {@link NotMatchPasswordException} 발생
     * File이 null이 아닐 경우 File을 업로드 하고 Path 변경
     *
     * @param postId             수정할 Post의 고유키
     * @param password           수정자가 입력한 패스워드
     * @param postUpdaterCommand 업데이트할 정보가 담긴 커멘드
     * @param file               업데이트할 파일
     * @throws NotMatchPasswordException 비밀번호가 일치하지 않을 경우 발생
     */
    void update(Long postId, String password, PostUpdaterCommand postUpdaterCommand, File file) throws IOException;
}
