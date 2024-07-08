package com.nerd2.catcare.user.dao;


import com.nerd2.catcare.user.UserDTO;
import org.apache.ibatis.annotations.Mapper;
import org.json.simple.JSONObject;

@Mapper
public interface UserDAO {
    int valid_ID(JSONObject jsonObject);

    void createUser(JSONObject jsonObject);
}
