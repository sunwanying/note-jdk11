package cn.sunguolei.note.config;

import cn.sunguolei.note.security.JWTAuthenticationFilter;
import cn.sunguolei.note.security.JWTAuthorizationFilter;
import cn.sunguolei.note.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private UserDetailsService userDetailsService;
    private UserService userService;

    public WebSecurityConfig(UserDetailsService userDetailsService, UserService userService) {
        this.userDetailsService = userDetailsService;
        this.userService = userService;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // cross-origin resource sharing 关闭跨域资源共享 和 Cross-site request forgery 跨站请求伪造 的检测 Why? - 20181112
        http.cors().and().csrf().disable().authorizeRequests()
                // 除了 login 其它接口都需要验证
                .antMatchers("/", "/toLogin", "/login",
                        "/user/add", "/user/create", "/user/activeUser",
                        // 允许查看公开的笔记，公开的工具
                        "/note/view/**", "/tool/jsonToParam",
                        // 静态资源过滤
                        "/static/css/**", "/static/js/**", "/static/images/**", "/favicon.ico").permitAll()
                .anyRequest().authenticated()
                .and()
                // 非 login 请求，先走 JWT token 过滤器，校验 token 的有效性
                .addFilter(new JWTAuthenticationFilter(authenticationManager()))
                .addFilter(new JWTAuthorizationFilter(authenticationManager(), userService))
                // this disables session creation on Spring Security
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .formLogin().loginPage("/toLogin")
                .and()
                .rememberMe()
                // 两周免登录
                .tokenValiditySeconds(1209600);
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        // passwordEncoder 是用来加密使用，采用的 BCrypt 加密方法
        auth.userDetailsService(userDetailsService).passwordEncoder(new BCryptPasswordEncoder(11));
    }

    // 允许所有的跨域
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", new CorsConfiguration().applyPermitDefaultValues());
        return source;
    }
}
