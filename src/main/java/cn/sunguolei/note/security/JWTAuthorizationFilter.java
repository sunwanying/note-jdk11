package cn.sunguolei.note.security;

import cn.sunguolei.note.entity.User;
import cn.sunguolei.note.service.UserService;
import cn.sunguolei.note.utils.UserUtil;
import io.jsonwebtoken.Jwts;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

import static cn.sunguolei.note.security.SecurityConstants.JWT_KEY;

// 授权过滤
public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

    private UserService userService;

    public JWTAuthorizationFilter(AuthenticationManager authManager, UserService userService) {
        super(authManager);
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws IOException, ServletException {

        // 读取 cookie 里的 token value
        Optional<String> token = UserUtil.getTokenFromCookie(request);

        // 如果 token 不存在，不进行授权
        if (!token.isPresent()) {
            chain.doFilter(request, response);
            return;
        }

        // 通过验证 token 合法性获取授权
        UsernamePasswordAuthenticationToken authentication = getAuthentication(token.get());

        // 将获取到的用户对象设置为 SecurityContextHolder 中的用户对象，有了合法的 spring 用户对象，后面的过滤器就能取到 spring 用户
        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(request, response);
    }

    // 通过验证用户合法，获取授权
    private UsernamePasswordAuthenticationToken getAuthentication(String token) {

        if (token != null) {
            // parse the token.
            String username = Jwts.parser()
                    .setSigningKey(JWT_KEY)
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();

            // 解出 token，获取 token 中的 name，返回一个创建的合法 spring 用户，第三个参数 new ArrayList 是用来设置权限的
            // 查出用户对象，设置到 authentication 对象中构建 Principal
            if (username != null) {
                User myUser = userService.findUserByUsername(username);
                return new UsernamePasswordAuthenticationToken(myUser, null, new ArrayList<>());
            }
            return null;
        }
        return null;
    }
}
