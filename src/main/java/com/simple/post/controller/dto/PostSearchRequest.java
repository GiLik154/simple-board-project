package com.simple.post.controller.dto;

import com.simple.post.domain.post.domain.command.PostSearchCommand;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Objects;

@Getter
public class PostSearchRequest {
    private final String company;

    private final String registrant;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private final LocalDate startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private final LocalDate endDate;

    public PostSearchRequest(String company, String registrant, LocalDate startDate, LocalDate endDate) {
        this.company = company;
        this.registrant = registrant;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public PostSearchCommand toCommand() {
        return new PostSearchCommand(this.company, this.registrant,
                Objects.requireNonNullElseGet(this.startDate, () -> LocalDate.of(1999, 1, 1)),
                Objects.requireNonNullElseGet(this.endDate, () -> LocalDate.of(2099, 12, 31)));
    }
}