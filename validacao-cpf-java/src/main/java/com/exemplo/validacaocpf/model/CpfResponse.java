package com.exemplo.validacaocpf.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CpfResponse {
    private int success;
    private Data data;

    @lombok.Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Data {
        private String name;
        private String cpfNumber;
        private String birthDate;
        private String registrationDate;
        private String situation;
        private String status;
        private Receipt receipt;
    }
}
