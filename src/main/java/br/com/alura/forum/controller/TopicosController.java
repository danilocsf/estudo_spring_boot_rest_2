package br.com.alura.forum.controller;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.alura.forum.controller.dto.DetalhesTopicoDto;
import br.com.alura.forum.controller.dto.TopicoDto;
import br.com.alura.forum.controller.form.AtualizacaoTopicoForm;
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
	 * 
	 * Pageable - para ser possível utilizá-lo como parâmetro, deve-se incluir a anotação 
	 * @EnableSpringDataWebSupport na classe main.
	 */
	@GetMapping
	@Cacheable(value = "listaDeTopicos")
	public Page<TopicoDto> lista(@RequestParam(required = false) String nomeCurso, @PageableDefault(sort = "id", direction = Direction.DESC) Pageable paginacao) {
	
		if (nomeCurso == null) {
			Page<Topico> topicos = topicoRepository.findAll(paginacao);
			return TopicoDto.converter(topicos);
		} else {
			Page<Topico> topicos = topicoRepository.findByCursoNome(nomeCurso, paginacao);
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
	public ResponseEntity<DetalhesTopicoDto> detalhar(@PathVariable Long id) {
		Optional<Topico> topico = topicoRepository.findById(id);
		if(topico.isPresent()) {
			return ResponseEntity.ok(new DetalhesTopicoDto(topico.get()));
		}		
		/*Retorna um erro 404*/
		return ResponseEntity.notFound().build();		
	}
	
	/*O @RequestBody informa ao Spring que os parâmetros estão no corpo da Requisição
	 * e que devem ser informados em TopicoForm.
	 * 
	 * UriComponentsBuilder será injetado pelo Spring e conterá a URI completa da requisição*/
	@PostMapping	
	 /*
	  * @CacheEvict - Para dizer que quero que o Spring limpe determinado cache
	  * allEntries - para limpar tudo deste cache
	  */
	@CacheEvict(value = "listaDeTopicos", allEntries = true)	
	@Transactional
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
	
	/*
	 * Utiliza uma nova classe para atualizacao pois pode ter dados que não devem ser alterados no objeto form de criação*/
	@PutMapping("/{id}")
	@CacheEvict(value = "listaDeTopicos", allEntries = true)
	@Transactional
	public ResponseEntity<TopicoDto> atualizar(@PathVariable Long id, @RequestBody @Valid AtualizacaoTopicoForm form, UriComponentsBuilder uriBuilder) {
		Optional<Topico> optTopico = topicoRepository.findById(id);
		if(optTopico.isPresent()) {
			Topico topico = form.atualizar(id, topicoRepository);
			return ResponseEntity.ok(new TopicoDto(topico));
		}		
		/*Retorna um erro 404*/
		return ResponseEntity.notFound().build();
	}
	
	
	@DeleteMapping("/{id}")
	@CacheEvict(value = "listaDeTopicos", allEntries = true)
	@Transactional
	public ResponseEntity<?> remover(@PathVariable Long id) {
		Optional<Topico> optTopico = topicoRepository.findById(id);
		if(optTopico.isPresent()) {
			topicoRepository.deleteById(id);
			return ResponseEntity.ok().build();
		}	
		/*Retorna um erro 404*/
		return ResponseEntity.notFound().build();
	}
		
}
