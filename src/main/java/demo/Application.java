package demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.authentication.configurers.InMemoryClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.OAuth2AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;

@ComponentScan
@EnableAutoConfiguration
@Order(Ordered.LOWEST_PRECEDENCE - 100)
public class Application extends OAuth2AuthorizationServerConfigurerAdapter {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		// @formatter:off
		 	auth.apply(new InMemoryClientDetailsServiceConfigurer())
		        .withClient("my-trusted-client")
		            .authorizedGrantTypes("password", "authorization_code", "refresh_token", "implicit")
		            .authorities("ROLE_CLIENT", "ROLE_TRUSTED_CLIENT")
		            .scopes("read", "write", "trust")
		            .accessTokenValiditySeconds(60)
 		    .and()
		        .withClient("my-client-with-secret")
		            .authorizedGrantTypes("client_credentials")
		            .authorities("ROLE_CLIENT")
		            .scopes("read")
		            .secret("secret");
		// @formatter:on
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// @formatter:off
        http
            .authorizeRequests()
                .antMatchers("/oauth/token").fullyAuthenticated()
                .and()
            .requestMatchers()
                .antMatchers("/oauth/token")
                .and()
            .apply(new OAuth2AuthorizationServerConfigurer());
    	// @formatter:on
	}
	
}
