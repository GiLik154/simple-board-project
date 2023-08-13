package com.simple.post.domain.post.domain.command;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class PostSearchCommand {
    private final String company;

    private final String registrant;

    private final LocalDate startDate;

    private final LocalDate endDate;

    public PostSearchCommand(String company, String registrant, LocalDate startDate, LocalDate endDate) {
        this.company = company;
        this.registrant = registrant;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
