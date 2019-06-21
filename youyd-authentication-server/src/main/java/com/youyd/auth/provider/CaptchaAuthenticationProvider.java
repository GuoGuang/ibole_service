/**
 * 
 */
package com.youyd.auth.provider;

import com.youyd.auth.exception.ValidateCodeException;
import com.youyd.auth.token.CaptchaAuthenticationToken;
import com.youyd.cache.redis.RedisService;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;

/**
 * 图片验证码登录验证
 * @author : LGG
 * @create : 2019-06-18 14:34
 **/
@Data
public class CaptchaAuthenticationProvider implements AuthenticationProvider {

	@Autowired
	private RedisService redisService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private UserDetailsService userDetailsService;


	public Authentication authenticate(Authentication authentication) throws AuthenticationException {

		CaptchaAuthenticationToken authenticationToken = (CaptchaAuthenticationToken) authentication;
		//String details =  authenticationToken.getDetails().toString();
		String username = authenticationToken.getPrincipal().toString();
		String captcha = authenticationToken.getCaptcha();
		if (captcha == null || StringUtils.isBlank(captcha)){
			throw new ValidateCodeException("验证码不能为空！");
		}
		String captchaUuid = authenticationToken.getCaptchaUuid();
		Object redisCaptcha = redisService.get(captchaUuid);
		if (redisCaptcha == null){
			throw new ValidateCodeException("验证码已失效！");
		}
		if (!captcha.equalsIgnoreCase(redisCaptcha.toString())) {
			throw new ValidateCodeException("验证码错误！");
		}
		UserDetails user = userDetailsService.loadUserByUsername(username);
		if (user == null){
			throw new ValidateCodeException("用户名或密码错误！");
		}
		String password = user.getPassword();
		String credentials = authentication.getCredentials().toString();
		if (!passwordEncoder.matches(credentials,password)){
			throw new ValidateCodeException("用户名或密码错误！");
		}

		//查询该code拥有的权限
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		if (captcha!=null){

			authorities.add(new SimpleGrantedAuthority("ROLE_role1"));
		}
		// 认证通过，生成已认证的Authentication，加入请求权限
		CaptchaAuthenticationToken authenticationResult = new CaptchaAuthenticationToken(user, user.getAuthorities());
		authenticationResult.setDetails(authenticationToken.getDetails());
		return new CaptchaAuthenticationToken(user,authorities);
	}


	/**
	 * supports方法用于检查入参的类型，AuthenticationProvider只会认证符合条件的类型
	 * 检查入参Authentication是否是UsernamePasswordAuthenticationToken或它的子类
	 *
	 * 系统默认的Authentication入参都是UsernamePasswordAuthenticationToken类型，所以这里supports必须为true。
	 * 需自定义认证过滤器，到时候就可以自定义不同的入参类型了，以适用于不同的AuthenticationProvider。

	 * @param authorization 符合条件的类型
	 * @return boolean
	 */
	public boolean supports(Class<?> authentication) {
		//负责处理MyAuthentication类型登录认证，参考上一篇
		return (CaptchaAuthenticationToken.class.isAssignableFrom(authentication));
	}

}