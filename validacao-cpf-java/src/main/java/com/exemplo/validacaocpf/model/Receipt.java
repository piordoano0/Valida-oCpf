package com.exemplo.validacaocpf.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Receipt {
    private String controlCode;
    private String receiptCode;
}
