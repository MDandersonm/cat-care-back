package com.nerd2.catcare.user;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping(value = "/user")
public class UserController {

@Autowired
UserService userService;


    @PostMapping("/createUser")
    public JSONObject createUser (@RequestBody UserDTO userDTO
                                  ){

        JSONObject result = new JSONObject();

         result = userService.createUser(userDTO);

        return result;
    }


    // 로그인
    @PostMapping(value = "/login")
    public JSONObject userLogin(HttpServletRequest req,
                                @RequestBody UserDTO userDTO){

        log.info("전체 userDTO: {}", userDTO);
        log.info("아이디체크 ===",userDTO.getUserId());

        JSONObject result = userService.userLogin(userDTO);

        if(result.get("status").equals("0000")){

            String accessToken = userService.getToken(userDTO);
            result.put("accessToken", accessToken);

        }

        return result;
    }

}
