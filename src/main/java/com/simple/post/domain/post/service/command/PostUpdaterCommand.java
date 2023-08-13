package com.simple.post.domain.post.service.command;

import lombok.Getter;

@Getter
public class PostUpdaterCommand {
    private final String title;

    private final String content;

    public PostUpdaterCommand(String title, String content) {
        this.title = title;
        this.content = content;
    }
}