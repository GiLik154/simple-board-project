package com.simple.post.domain.post.service;

import com.simple.post.exception.NotFoundPostException;
import com.simple.post.exception.NotMatchPasswordException;

import java.io.IOException;

public interface PostDeleter {
    /**
     * postId와 Password가 일치하면 Post 삭제.
     * postId가 일치하지 않으면 {@link NotFoundPostException} 발생
     * Password가 일치하지 않으면 {@link NotMatchPasswordException} 발생
     *
     * @param postId   삭제할 Post의 고유키
     * @param password 삭제할 Postd의 비밀번호
     * @throws NotFoundPostException     삭제할 Post가 DB에 없으면 발생
     * @throws NotMatchPasswordException 삭제할 Post의 Password가 일치하지 않으면 발생
     */
    void delete(Long postId, String password) throws IOException;
}
