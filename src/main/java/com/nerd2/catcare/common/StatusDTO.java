package com.nerd2.catcare.common;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class StatusDTO {
    //요청 성공
    private final String success = "0000";
    //실패
    private final String fail = "0001";
    //사용중인 아이디
    private final String duplicateID = "0002";
    //아이디 또는 비밀번호 확인필요
    private final String loginErr = "0003";



}
