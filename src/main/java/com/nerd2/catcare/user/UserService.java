package com.nerd2.catcare.user;


import com.nerd2.catcare.common.JwtProvider;
import com.nerd2.catcare.common.StatusDTO;
import com.nerd2.catcare.user.dao.UserDAO;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserService {

    @Autowired
    UserDAO userDAO;

    @Autowired
    JwtProvider jwtProvider;

    @Value("${jwt.expTime}")
    private long min;
    @Value("${jwt.expTime.refresh}")
    private long refreshMin;

    StatusDTO statusDTO = new StatusDTO();
    public JSONObject createUser(UserDTO userDTO) {

        JSONObject result = new JSONObject();


        try {

            if(1 <= userDAO.valid_ID(userDTO) ){
                result.put("status", statusDTO.getDuplicateID().toString());

            }else {
                userDAO.createUser(userDTO);
                result.put("status", statusDTO.getSuccess().toString());

            }


        }catch (Exception e){

            result.put("status", statusDTO.getFail().toString());
            log.info("Exception = ",e);
        }

        return result;
    }




    public String getToken(UserDTO userDTO) {

        String accessToken = null;

        try {
            String UserID = userDTO.getUserId();
//            String UserName = userDTO.getUserName();
//            int UserLevel = userDTO.getUserLevel();
//            String Token = jwtProvider.createToken(UserID, UserName, UserLevel, (min * 1000 * 60), "access");
//            String refreshToken = jwtProvider.createToken(UserID, UserName, UserLevel, (refreshMin * 1000 * 60), "reToken");
            String Token = jwtProvider.createToken(UserID, (min * 1000 * 60), "access");
            String refreshToken = jwtProvider.createToken(UserID,(refreshMin * 1000 * 60), "reToken");
            insertRetoken(UserID, refreshToken);
            accessToken = Token;


        } catch (Exception e) {

            log.info("Exception = {}",e);

        }

        return accessToken;

    }

    private void insertRetoken(String userID, String refreshToken) {

        try {

            userDAO.insertRetoken(userID,refreshToken);

        }catch (Exception e ){

            log.info("Exception = {}" , e);

        }
    }

    public JSONObject userLogin(UserDTO userDTO) {

        JSONObject result = new JSONObject();

        try {

            if(userDAO.userIDChk(userDTO)==0){

                result.put("status",statusDTO.getLoginErr());
                log.info("아이디를 확인해주세요.");
                return result;

            }else if (userDAO.passwordChk(userDTO)==0){

                result.put("status",statusDTO.getLoginErr());
                log.info("비밀번호를 확인해주세요.");
                return result;
            }
            result.put("status",statusDTO.getSuccess());
            log.info("로그인 성공");

        }catch (Exception e){

            result.put("status",statusDTO.getFail());
            log.info("Exception = {}" , e);

        }


        return result;
    }

}
