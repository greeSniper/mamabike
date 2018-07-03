package com.tangzhe.mamabike.security;

import com.tangzhe.mamabike.user.entity.UserElement;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * Created by tangzhe 2017/09/11.
 * 用户凭证
 */
public class RestAuthenticationToken extends AbstractAuthenticationToken{

    public RestAuthenticationToken(Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
    }

    private UserElement user;

    public UserElement getUser() {
        return user;
    }

    public void setUser(UserElement user) {
        this.user = user;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return null;
    }

}
