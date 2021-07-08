package br.com.alura.forum.controller.form;

import javax.validation.constraints.NotEmpty;

import br.com.alura.forum.modelo.Curso;
import br.com.alura.forum.modelo.Topico;
import br.com.alura.forum.repository.CursoRepository;

/*
 * Representam os dados que chegam do client para a API.
 */
public class TopicoForm {
	@NotEmpty	
	private String titulo;
	@NotEmpty
	private String mensagem;
	@NotEmpty
	private String nomeCurso;
	
	public Topico converter(CursoRepository cursoRepository) {
		Curso curso = cursoRepository.findByNome(nomeCurso);
		return new Topico(titulo, mensagem, curso); 
	}
	
	public String getTitulo() {
		return titulo;
	}
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}
	public String getMensagem() {
		return mensagem;
	}
	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}
	public String getNomeCurso() {
		return nomeCurso;
	}
	public void setNomeCurso(String nomrCurso) {
		this.nomeCurso = nomrCurso;
	}
	
	
	
}
