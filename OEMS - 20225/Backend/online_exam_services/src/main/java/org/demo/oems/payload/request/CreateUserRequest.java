package org.demo.oems.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserRequest {

    private String name;

    private String email;

    private String role;

    private String password;

    private String userId;

    private LocalDate dob;

    private String gender;

    private long roleId;

    private String phoneNumber;

    private String address;

    private Boolean status = true;

}
