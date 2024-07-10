package com.nerd2.catcare.user.dao;


import com.nerd2.catcare.user.UserDTO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserDAO {
    int valid_ID(UserDTO userDTO);

    void createUser(UserDTO userDTO);


    int userIDChk(UserDTO userDTO);

    int passwordChk(UserDTO userDTO);

    void insertRetoken(String userID, String refreshToken);
}
