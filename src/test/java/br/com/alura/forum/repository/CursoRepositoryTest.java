package br.com.alura.forum.repository;

import br.com.alura.forum.modelo.Curso;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
/*Isso indica ao Spring que o Spring não deve substituir o banco de dados
  configurado na aplicação por um banco de dados em memória (no caso desse projeto o banco configurado da aplicação
   também é em memória)*/
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
/*Ao rodar essa classe irá entender que o profile ativo do momento é o de teste
e ler o application-test.properties*/
@ActiveProfiles("test")
public class CursoRepositoryTest {

    @Autowired
    private CursoRepository repository;

    @Autowired
    private TestEntityManager em;

    @Test
    public void deveriaCarregarUmCursoAoBuscarPeloNome(){
        String nome = "HTML 5";
        Curso html5 = new Curso();
        html5.setNome(nome);
        html5.setCategoria("Programacao");
        em.persist(html5);

        Curso curso = repository.findByNome(nome);
        Assertions.assertNotNull(curso);
        Assertions.assertEquals(nome, curso.getNome());
    }
}