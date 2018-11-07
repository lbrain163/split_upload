package com.forezp.Interceptor;

import com.forezp.security.AESHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by liangbing on 2018/11/7.
 * Desc: 权限拦截器
 */
@Slf4j
public class AuthInterceptor implements HandlerInterceptor {

    @Value("${auth.username}")
    String username;

    @Value("${auth.password}")
    String password;

    @Value("${auth.key}")
    String key;

    @Value("${auth.encrypt}")
    String encrypt;

    /**
     * 在请求处理之前进行调用（Controller方法调用之前）
     */
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object object) throws Exception {

        String msg = "AuthInterceptor.preHandle拦截...";
        log.info(msg + " begin");
        log.info("properties username: " + username);
        log.info("properties password: " + password);

        if(StringUtils.isEmptyOrWhitespace(username) || StringUtils.isEmptyOrWhitespace(password)) {
            log.info("未配置有效权限验证信息");
            return true;
        }

        try {
            String req_username = httpServletRequest.getHeader("username");
            String req_password = httpServletRequest.getHeader("password");

            if(StringUtils.isEmptyOrWhitespace(req_username) || StringUtils.isEmptyOrWhitespace(req_password)) {
                msg = "请求中未找到权限信息";
                log.info(msg);
                returnErrorResponse(httpServletResponse, msg);
                return false;
            }

            log.info("HttpServletRequest header username: " + req_username);
            log.info("HttpServletRequest header password: " + req_password);

            if("1".equals(encrypt)) {
                req_username = new String(AESHelper.decrypt(AESHelper.parseHexStr2Byte(req_username), key));
                req_password = new String(AESHelper.decrypt(AESHelper.parseHexStr2Byte(req_password), key));
                log.info("Decode username: " + req_username);
                log.info("Decode password: " + req_password);
            }

            if(!username.equals(req_username) || !password.equals(req_password)) {
                msg = "用户名密码不正确";
                log.info(msg);
                returnErrorResponse(httpServletResponse, msg);
                return false;
            }
        } catch (Exception e) {
            log.error("权限校验异常. " + e.getMessage());
            e.printStackTrace();
        }

        log.info(msg + " end");
        return true;
    }

    /**
     * 请求处理之后进行调用，但是在视图被渲染之前（Controller方法调用之后）
     */
    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    /**
     * 在整个请求结束之后被调用，也就是在DispatcherServlet 渲染了对应的视图之后执行（主要是用于进行资源清理工作）
     */
    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }

    public void returnErrorResponse(HttpServletResponse response, String result) throws IOException {
        OutputStream out = null;
        try {
            response.setCharacterEncoding("utf-8");
            response.setContentType("text/json");
            out = response.getOutputStream();
            out.write(result.getBytes("utf-8"));
            out.flush();
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }
}
