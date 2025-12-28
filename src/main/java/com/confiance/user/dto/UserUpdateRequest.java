package com.confiance.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequest {
    private String firstName;
    private String lastName;
    private String contactNumber;
    private String country;
    private String state;
    private String city;
    private String address;
    private String postalCode;
}