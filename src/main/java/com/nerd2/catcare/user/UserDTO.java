package com.nerd2.catcare.user;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class UserDTO {

    private String userId;
    private String userName;
    private String email;
    private String password;
    private String phone;
    private String address;
    private String birthday;
    private int gender;
    private int userLevel;

}
