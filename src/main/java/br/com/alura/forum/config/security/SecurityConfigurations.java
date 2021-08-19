package br.com.alura.forum.config.security;

import javax.servlet.Filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import br.com.alura.forum.repository.UsuarioRepository;

@EnableWebSecurity
@Configuration
public class SecurityConfigurations extends WebSecurityConfigurerAdapter{
	
	@Autowired
	private AutenticacaoService autenticacaoService; 
	
	@Autowired
	private TokenService tokenService; 
	
	@Autowired
	private UsuarioRepository repository;
	
	@Override
	@Bean
	protected AuthenticationManager authenticationManager() throws Exception {		
		return super.authenticationManager();
	}
	
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
		//Liberando acesso ao endpoint /auth utilizando POST (autenticação)
		.antMatchers(HttpMethod.POST, "/auth").permitAll()
		//Qualquer outra requisição tem q estar autenticado
		.anyRequest().authenticated()
		//and().formLogin(). Existe esse método que é para falar para o Spring 
		//gerar um formulário de autenticação. 
		//O Spring já tem um formulário de autenticação e um controller que recebe 
		//as requisições desse formulário
		//.and().formLogin()
		
		//A partir de agora será utilizado token para autenticação e não mais
		//a autenticaçaõ por sessão , por isso retirada o and().formlogin()
		
		
		/*
		 * Csrf é uma abreviação para cross-site request forgery, 
		 * que é um tipo de ataque hacker que acontece em aplicações web. 
		 * Como vamos fazer autenticação via token, automaticamente nossa API 
		 * está livre desse tipo de ataque. Nós vamos desabilitar isso para o 
		 * Spring security não fazer a validação do token do csrf.
		 * */
		.and().csrf().disable()
		/*Configuração para que não tenha sessão na autenticação*/
		.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
		.and().addFilterBefore(new AutenticacaoViaTokenFilter(tokenService, repository), UsernamePasswordAuthenticationFilter.class);
		
	}
	
	
	/*Configuraçoes de recursos estáticos (css, javascript, imagens)*/
	@Override	
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/**.html", "/v2/api-docs", "/webjars/**", "/configuration/**", "/swagger-resources/**");
	}	
	/*Utilizando apenas para exibir a senha 123456 codificada, para informar no arquivo
	 * sql com os dados iniciais da aplicação*/
	public static void main(String[] args) {
		System.out.println(new BCryptPasswordEncoder().encode("123456"));
	}
}
