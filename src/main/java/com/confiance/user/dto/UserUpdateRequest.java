package com.confiance.user.dto;

import com.confiance.common.enums.Salutation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequest {
    private Salutation salutation;
    private String firstName;
    private String middleName;
    private String lastName;
    private String contactNumber;
    private String country;
    private String state;
    private String city;
    private String address;
    private String postalCode;
}