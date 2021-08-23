package br.com.alura.forum.config.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity
@Configuration
@Profile("dev")
public class DevSecurityConfigurations extends WebSecurityConfigurerAdapter{
			
	/*Configuraçoes de autorização*/
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		
		http.authorizeRequests()
		//Liberando acesso a todos endpoints
		.antMatchers("/**").permitAll()		
		/*
		 * Csrf é uma abreviação para cross-site request forgery, 
		 * que é um tipo de ataque hacker que acontece em aplicações web. 
		 * Como vamos fazer autenticação via token, automaticamente nossa API 
		 * está livre desse tipo de ataque. Nós vamos desabilitar isso para o 
		 * Spring security não fazer a validação do token do csrf.
		 * */
		.and().csrf().disable();
	}
}
