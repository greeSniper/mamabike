package com.tangzhe.mamabike.security;

import com.tangzhe.mamabike.cache.CommonCacheUtil;
import com.tangzhe.mamabike.common.constants.Parameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

/**
 * SpringSecurity配置
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private Parameters parameters;
    @Autowired
    private CommonCacheUtil commonCacheUtil;

    /**
     * preAuthenticatedProcessingFilter  authenticationManager----restAuthenticationProvider(一个或者N个 提供权限信息) entrypoint 统一异常处理
     */
    private RestPreAuthenticatedProcessingFilter getPreAuthenticatedProcessingFilter() throws Exception {
        RestPreAuthenticatedProcessingFilter filter = new RestPreAuthenticatedProcessingFilter(parameters.getNoneSecurityPath(), commonCacheUtil);
        filter.setAuthenticationManager(this.authenticationManagerBean());
        return filter;
    }

    /**
     * preAuthenticatedProcessingFilter  authenticationManager----restAuthenticationProvider(一个或者N个 提供权限信息) entrypoint 统一异常处理
     */
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(new RestAuthenticationProvider());
    }

    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                .antMatchers(parameters.getNoneSecurityPath().toArray(new String[parameters.getNoneSecurityPath().size()])).permitAll()
                .anyRequest().authenticated()
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) //安卓端，无session状态
                .and().httpBasic().authenticationEntryPoint(new RestAuthenticationEntryPoint())
                .and().addFilter(getPreAuthenticatedProcessingFilter());
    }

    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(HttpMethod.OPTIONS, "/**");//忽略 OPTIONS 方法的请求
    }

}
















