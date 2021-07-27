package br.com.alura.forum.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.alura.forum.config.security.TokenService;
import br.com.alura.forum.controller.dto.TokenDto;
import br.com.alura.forum.controller.form.LoginForm;

@RestController
@RequestMapping("/auth")
public class AutenticacaoController {
	
	/* Para fazer uma autenticação de maneira programática, manualmente, no Spring 
	   Security, vamos precisar de uma classe chamada authentication manager.
	   Essa classe é do Spring, mas ele não consegue fazer a injeção de dependências 
	   dela automaticamente, a não ser que nós configuremos isso na nossa classe SecurityConfiguration
	   sobrescrevendo o método para criar o authenticationManager e anotando com @Bean.	   	   
	*/
	@Autowired
	private AuthenticationManager authManager;
	@Autowired
	private TokenService tokenService;
	
	@PostMapping
	public ResponseEntity<?> autenticar(@RequestBody @Valid LoginForm form){
		UsernamePasswordAuthenticationToken dadosLogin = form.converter();
		try {
			Authentication authentication = authManager.authenticate(dadosLogin);
			String token = tokenService.gerarToken(authentication);
			
			return ResponseEntity.ok(new TokenDto(token, "Bearer"));
		} catch(AuthenticationException e) {
			return ResponseEntity.badRequest().build();
		}
		
	}
}