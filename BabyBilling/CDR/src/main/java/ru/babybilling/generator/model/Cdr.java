package ru.babybilling.generator.model;

import lombok.Data;

@Data
public class Cdr {
    private Long callType;
    private String firstNum;
    private String secondNum;
    private Long startTime;
    private String endTime;
}
