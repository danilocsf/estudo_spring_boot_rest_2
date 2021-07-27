package br.com.alura.forum.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@EnableWebSecurity
@Configuration
public class SecurityConfigurations extends WebSecurityConfigurerAdapter{
	
	@Autowired
	private AutenticacaoService autenticacaoService; 
	
	/*Configuraçoes de autenticacao*/
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		
		/*diz para o Spring qual a classe, a service que tem a lógica de autenticação
		 * e também qual o Encoder responsavel pela criptografia da senha*/
		auth.userDetailsService(autenticacaoService).passwordEncoder(new BCryptPasswordEncoder());
	}
	
	/*Configuraçoes de autorização*/
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		
		http.authorizeRequests()
		//Liberando acesso ao endpoint /topicos utilizando GET (lista)
		.antMatchers(HttpMethod.GET, "/topicos").permitAll()
		//Liberando acesso ao endpoint /topicos/{id} utilizando GET (detalha)
		.antMatchers(HttpMethod.GET, "/topicos/*").permitAll()
		//Qualquer outra requisição tem q estar autenticado
		.anyRequest().authenticated()
		//and().formLogin(). Existe esse método que é para falar para o Spring 
		//gerar um formulário de autenticação. 
		//O Spring já tem um formulário de autenticação e um controller que recebe 
		//as requisições desse formulário
		.and().formLogin();
	}
	
	
	/*Configuraçoes de recursos estáticos (css, javascript, imagens)*/
	@Override
	public void configure(WebSecurity web) throws Exception {
		
	}
	
	/*Utilizando apenas para exibir a senha 123456 codificada, para informar no arquivo
	 * sql com os dados iniciais da aplicação*/
	public static void main(String[] args) {
		System.out.println(new BCryptPasswordEncoder().encode("123456"));
	}
}
