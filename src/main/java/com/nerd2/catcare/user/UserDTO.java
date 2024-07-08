package com.nerd2.catcare.user;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class UserDTO {

    String userID;
    String userName;
    String password;

    public UserDTO(){

    }
}
