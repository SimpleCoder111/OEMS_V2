package org.demo.oems.payload.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileResponse {
    private String userId;

    private String name;

    private String email;

    private String phoneNumber;

    private String address;

    private LocalDate dateOfBirth;

    private String gender;

    private String roleName;

    private String profileImageUrl;  // ← new
}
