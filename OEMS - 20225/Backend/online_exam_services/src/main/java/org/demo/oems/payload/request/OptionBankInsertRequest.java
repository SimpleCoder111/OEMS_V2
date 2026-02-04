package org.demo.oems.payload.request;

import lombok.*;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class OptionBankInsertRequest {

    private Long optionId;

    private String optionText;

    private Boolean isCorrect;
}
