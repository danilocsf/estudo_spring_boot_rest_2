package br.com.alura.forum.config.validacao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/*
 * Esta classe é um interceptador o qual
 * sempre que ocorrer uma exception no Controller,
 * o Spring irá chamar automaticamente esta classe 
 * onde terá o tratamento de erro apropriado.
 */
@RestControllerAdvice
public class ErroDeValidacaoHandler {
	
	@Autowired 
	private MessageSource messageSource;
	
	/*
	 * Sem o ResponseStatus, o Spring consideraria que foi feito um tratamento para o erro,
	 * e retornaria o status 200.
	 * Aqui será tratada as mensagens de erro e a serem retornadas ao client.
	 * Sendo assimé necessário devolver o 400.
	 */
	@ResponseStatus(code = HttpStatus.BAD_REQUEST)
	/*Aqui é informado ao Spring que sempre que esta determinada Exception ocorrer em um Controller, 
	 * este método será chamado. (Esta é a esception lançada quando dá algum erro pego pelo Bean Validation)*/
	@ExceptionHandler(MethodArgumentNotValidException.class)
	/*O método precisa obter a exceção que ocorreu, para realizar o tratamento*/
	public List<ErroDeFormularioDto> handle(MethodArgumentNotValidException exception) {
		List<ErroDeFormularioDto> erroList = new ArrayList<ErroDeFormularioDto>(); 
		/*Todos os erros dos campos que deram erros*/
		List<FieldError> fieldErrors = exception.getFieldErrors();
		fieldErrors.forEach(e -> {
			/*LocaleContextHolder.getLocale() obtém o idioma atual para obter a msg no idioma correto
			 *Para teste, no postman pode-se informar um Header Accept-Language com a lingua desejada.
			 *A mensagem será retornada de acordo com esse Header*/
			
			String mensagem = messageSource.getMessage(e, LocaleContextHolder.getLocale());
			ErroDeFormularioDto dto = new ErroDeFormularioDto(e.getField(), mensagem);
			erroList.add(dto);
		});
		return erroList;
	}
}
