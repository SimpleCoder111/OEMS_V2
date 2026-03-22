package org.demo.oems.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OptionListResponse {

    private Long optionId;

    private String optionText;

    private Boolean isCorrect;

}
