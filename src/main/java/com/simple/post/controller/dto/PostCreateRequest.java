package com.simple.post.controller.dto;

import com.simple.post.domain.post.service.command.PostCreatorCommand;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class PostCreateRequest {
    private final String title;

    private final String content;

    private final String company;

    private final String registrant;

    private final String password;

    public PostCreateRequest(String title, String content, String company, String registrant, String password) {
        this.title = title;
        this.content = content;
        this.company = company;
        this.registrant = registrant;
        this.password = password;
    }

    public PostCreatorCommand toCommand(LocalDate registrationDate) {
        return new PostCreatorCommand(this.title, this.content, this.company, this.registrant, this.password, registrationDate);
    }
}
