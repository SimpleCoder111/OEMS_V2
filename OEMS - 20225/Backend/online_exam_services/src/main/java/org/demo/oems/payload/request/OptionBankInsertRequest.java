package org.demo.oems.payload.request;

import lombok.*;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class OptionBankInsertRequest {

    private String optionText;

    private Boolean isCorrect;
}
