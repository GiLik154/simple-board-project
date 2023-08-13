package com.simple.post.domain.post.service.command;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class PostCreatorCommand {
    private final String title;

    private final String content;

    private final String company;

    private final String registrant;

    private final String password;

    private final LocalDate registrationDate;


    public PostCreatorCommand(String title, String content, String company, String registrant, String password, LocalDate registrationDate) {
        this.title = title;
        this.content = content;
        this.company = company;
        this.registrant = registrant;
        this.password = password;
        this.registrationDate = registrationDate;
    }
}