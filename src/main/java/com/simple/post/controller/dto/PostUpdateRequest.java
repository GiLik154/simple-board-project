package com.simple.post.controller.dto;

import com.simple.post.domain.post.service.command.PostUpdaterCommand;
import lombok.Getter;

@Getter
public class PostUpdateRequest {
    private final String title;

    private final String content;

    public PostUpdateRequest(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public PostUpdaterCommand toCommand() {
        return new PostUpdaterCommand(this.title, this.content);
    }
}
