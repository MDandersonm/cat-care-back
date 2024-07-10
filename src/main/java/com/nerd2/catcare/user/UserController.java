package com.nerd2.catcare.user;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping(value = "/user")
public class UserController {

@Autowired
UserService userService;


    @PostMapping("/createUser")
    public JSONObject createUser (@RequestParam String userID,
                                  @RequestParam String password,
                                  @RequestParam String userName
                                  ){
        System.out.println("test");
        JSONObject param = new JSONObject();
        JSONObject result = new JSONObject();

        param.put("userID",userID);
        param.put("password",password);
        param.put("userName",userName);

        result = userService.createUser(param);

        return result;

    }
}
