package com.nerd2.catcare.user;


import com.nerd2.catcare.user.dao.UserDAO;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserService {



    @Autowired
    UserDAO userDAO;

    public JSONObject createUser(JSONObject parma) {

        JSONObject result = new JSONObject();


        try {

            if(1 <= userDAO.valid_ID(parma) ){
                result.put("status","0002");

            }else {
                userDAO.createUser(parma);
                result.put("status", "0000");

            }


        }catch (Exception e){

            result.put("status", e);
            System.out.println(e);
        }

        return result;
    }


}
