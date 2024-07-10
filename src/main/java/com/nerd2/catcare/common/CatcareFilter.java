package com.nerd2.catcare.common;


import com.nerd2.catcare.user.dao.UserDAO;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
@Slf4j
@CrossOrigin
@Component
public class CatcareFilter extends GenericFilterBean {

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private UserDAO userDAO;

    @Value("${jwt.secret.refresh}")
    private String refreshKey;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(httpResponse);

        setCorsHeaders(responseWrapper);

        String path = httpRequest.getRequestURI();

        if (isPreflightRequest(httpRequest)) {
            responseWrapper.setStatus(HttpServletResponse.SC_NO_CONTENT);
            responseWrapper.copyBodyToResponse(); // 추가된 부분
            return;
        }

        if (isPermittedPath(path) || isAuthorizedRequest(httpRequest)) {
            chain.doFilter(request, responseWrapper);
        } else {
            responseWrapper.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
        responseWrapper.copyBodyToResponse();
    }

    private void setCorsHeaders(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "*");
        response.setHeader("Access-Control-Allow-Headers", "*");
    }

    private boolean isPreflightRequest(HttpServletRequest request) {
        return "OPTIONS".equals(request.getMethod());
    }

    private boolean isPermittedPath(String path) {

        log.info("============ 요청 url = {}",path);

        List<String> permittedPaths = Arrays.asList(
                "/user/createUser",
                "/user/login"
        );
        return permittedPaths.stream().anyMatch(path::contains);
    }


    private boolean isAuthorizedRequest(HttpServletRequest request) {


            String path = request.getRequestURI();

        String token = request.getHeader("accessToken");
        String userId = request.getHeader("userid");
        String userLevel = request.getHeader("UserLevel");

// or 조건 말고 && 조건 변경 필요
            if (StringUtils.isEmpty(token) ) {
            logger.info("Header 값 누락");

            return false;
        }

        int tokenValidityStatus = jwtProvider.validateToken(jwtProvider.getSecretKey(), token);
            log.info("토큰 검증 ==== {}",tokenValidityStatus);
        if (tokenValidityStatus == 1 && jwtProvider.getSubject(token, jwtProvider.getSecretKey()).equals(userId)) {
            logger.info("Access token is valid (유효한 토큰)");
            String tokenUserLevel = Optional.ofNullable(jwtProvider.getClaims(token, jwtProvider.getSecretKey()).get("UserLevel")).orElse("").toString();

            return userLevel.equals(tokenUserLevel) && (tokenUserLevel.equals("1") || tokenUserLevel.equals("2") || tokenUserLevel.equals("3"));
        }
        return false;
    }
}


