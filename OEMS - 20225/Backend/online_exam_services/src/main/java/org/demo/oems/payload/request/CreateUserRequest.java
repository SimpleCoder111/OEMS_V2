package org.demo.oems.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CreateUserRequest {

    private String userId;

    private String name;

    private String password;

    private LocalDate dateOfBirth;

    private String gender;

    private Integer roleId;

    private String email;

    private String phoneNumber;

    private String address;

}
