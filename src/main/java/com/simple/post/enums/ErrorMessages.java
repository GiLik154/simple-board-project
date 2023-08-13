package com.simple.post.enums;

import lombok.Getter;

@Getter
public enum ErrorMessages {
    NOT_FOUND_FILE_EXCEPTION("파일을 찾을 수 없습니다."),
    NOT_MATCH_PASSWORD_EXCEPTION("비밀번호가 일치하지 않습니다."),
    NOT_FOUND_POST_EXCEPTION("게시물을 찾을 수 없습니다.");

    private final String korean;

    ErrorMessages(String korean) {
        this.korean = korean;
    }
}