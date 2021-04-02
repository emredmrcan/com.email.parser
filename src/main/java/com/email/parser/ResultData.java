package com.email.parser;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResultData {
    String subject;
    String from;
    Client client;
}
