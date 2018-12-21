package cn.sunguolei.note.security;

import cn.sunguolei.note.entity.TokenInfo;
import cn.sunguolei.note.entity.User;
import cn.sunguolei.note.utils.UserUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import static cn.sunguolei.note.security.SecurityConstants.EXPIRATION_TIME;
import static cn.sunguolei.note.security.SecurityConstants.JWT_KEY;

// 登录的时候使用
public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private AuthenticationManager authenticationManager;

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    // 验证带上来的 用户名 和 密码
    @Override
    public Authentication attemptAuthentication(HttpServletRequest req,
                                                HttpServletResponse res) throws AuthenticationException {
        try {
            // 从请求 request 中解出 user 对象
            User user = new ObjectMapper().readValue(req.getInputStream(), User.class);

            // 进行验证
            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            user.getUsername(),
                            user.getPassword(),
                            new ArrayList<>())
            );
        } catch (BadCredentialsException e) {
            e.printStackTrace();
            try {
                res.setCharacterEncoding("UTF-8");
                res.setContentType("application/json;charset=UTF-8");
                res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                res.getWriter().print("error");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    // 用户验证成功后
    @Override
    protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse response,
                                            FilterChain chain, Authentication auth) throws JsonProcessingException {

        // 强转成 spring user 对象，获取用户名
        String username = ((org.springframework.security.core.userdetails.User) auth.getPrincipal()).getUsername();

        // 调用 jwt 的类库构建 token 返回给用户浏览器存储
        String token = Jwts.builder()
                .setSubject(username)
                // 过期时间 10 天
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(JWT_KEY)
                .compact();

        // 写 token 到 response 的 cookie 里
        UserUtil.setTokenToCookie(response, token);

        // 设置返回结果
        TokenInfo tokenInfo = new TokenInfo();
        tokenInfo.setIsLogin(true);
        tokenInfo.setResultCode(200);
        tokenInfo.setUsername(username);

        String resultString = new ObjectMapper().writeValueAsString(tokenInfo);

        // 将 JWT 写入 body
        try {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().print(resultString);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
