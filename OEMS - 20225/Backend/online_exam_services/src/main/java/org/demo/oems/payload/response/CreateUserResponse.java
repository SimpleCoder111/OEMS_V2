package org.demo.oems.payload.response;

import lombok.Data;

@Data
public class CreateUserResponse {

    private String id;

    private String email;

    private String role;

    private String status;

    private String createAt;

}
