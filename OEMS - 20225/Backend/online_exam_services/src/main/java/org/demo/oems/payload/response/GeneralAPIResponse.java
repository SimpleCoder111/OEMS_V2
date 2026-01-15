package org.demo.oems.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GeneralAPIResponse {

    private String status;

    private String message;

    private Map<String, Object> data = new HashMap<>();

}
