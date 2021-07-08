package br.com.alura.forum.controller;

import java.net.URI;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.alura.forum.controller.dto.DetalhesTopicoDto;
import br.com.alura.forum.controller.dto.TopicoDto;
import br.com.alura.forum.controller.form.TopicoForm;
import br.com.alura.forum.modelo.Topico;
import br.com.alura.forum.repository.CursoRepository;
import br.com.alura.forum.repository.TopicoRepository;

/* Informa que esse Controller espera sempre uma requisição, 
 * não sendo necessário informar a anotação @responseBody nos métodos,
 * que diz que não está sendo navegado em uma página, mas sim apenas retornando conteudo.*/
@RestController
/*Significa que esse Controller responde a requisições iniciadas com /topicos*/
@RequestMapping("/topicos")
public class TopicosController {
	
	@Autowired
	private TopicoRepository topicoRepository;
	
	@Autowired
	CursoRepository cursoRepository;
	
	/*
	 * Quando for feito um GET para o endereço /topicos, cairá aqui.
	 * Se for passado no endereço do GET 
	 * o nomeCurso(/topicos?^nomeCurso=Spring), então o parametro será preenchido.
	 * Ao contrário o parametro virá null.
	 */
	@GetMapping
	public List<TopicoDto> lista(String nomeCurso) {
		if(nomeCurso == null) {
			List<Topico> topicos = topicoRepository.findAll();		
			return TopicoDto.converter(topicos);	
		}else {
			List<Topico> topicos = topicoRepository.findByCursoNome(nomeCurso);		
			return TopicoDto.converter(topicos);
		}		
	}
	
	/*
	 * @PathVariable informa ao Spring que o parâmetro será informado pela URL
	 * (não usando o ?). 
	 * O parâmetro será reconhecido pelo nome (id -n nome do parâmetro e id configurado no 
	 * @GetMapping)
	 * Caso o nome do parametro fosse diferente, poderia ser informado da seguinte maneira:
	 * public List<TopicoDto> detalhar(@PathVariable("id")Long codigo)
	 */
	@GetMapping("/{id}")
	public DetalhesTopicoDto detalhar(@PathVariable Long id) {
		Topico topico = topicoRepository.getById(id);
		return new DetalhesTopicoDto(topico);
	}
	
	/*O @RequestBody informa ao Spring que os parâmetros estão no corpo da Requisição
	 * e que devem ser informados em TopicoForm.
	 * 
	 * UriComponentsBuilder será injetado pelo Spring e conterá a URI completa da requisição*/
	@PostMapping
	/*O ResponseEntity é utilizado para retornar o corpo da resposta (contido no objeto TopicoDto) e pra informar
	 * o código do retorno - no caso 201*/
	public ResponseEntity<TopicoDto> cadastrar(@RequestBody @Valid TopicoForm form, UriComponentsBuilder uriBuilder) {
		Topico topico = form.converter(cursoRepository);
		topicoRepository.save(topico);
		/* O created devolve o código 201. Porém é necessário devolver tb a URI do registro criado
		 * Sendo assim, é utilizado o uriBuilder chamando os métodos path, o buildAndExpando pra substituir o {id)
		 * do path pelo id criado e toUri para transformar em URI*/
		URI uri = uriBuilder.path("/topicos/{id}").buildAndExpand(topico.getId()).toUri();   
		/*Devolve o código 201, a URL do registro criado e os dados do registro criado*/
		return ResponseEntity.created(uri).body(new TopicoDto(topico));
	}
}
