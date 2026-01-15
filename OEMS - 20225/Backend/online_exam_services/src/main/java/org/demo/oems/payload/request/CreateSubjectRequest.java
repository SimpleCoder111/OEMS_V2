package org.demo.oems.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateSubjectRequest {

    private String name;

    private String code;

    private String description;

    private boolean isActive;

}
