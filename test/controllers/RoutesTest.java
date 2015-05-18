package controllers;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static play.mvc.Http.Status.OK;
import static play.mvc.Http.Status.SEE_OTHER;
import static play.test.Helpers.GET;
import static play.test.Helpers.POST;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.fakeRequest;
import static play.test.Helpers.route;
import static play.test.Helpers.status;

import java.util.ArrayList;
import java.util.List;

import models.Autor;
import models.AutorRepository;
import models.Livro;
import models.LivroRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import play.GlobalSettings;
import play.mvc.Result;
import play.test.WithApplication;

/**
 * Classe de testes de integração focada em testar a configuração das rotas e
 * sua interação com o controller. Esse teste faz um mock dos serviços do
 * repositório, para que o bd real não seja necessário
 */
@RunWith(MockitoJUnitRunner.class)
public class RoutesTest extends WithApplication {

	private static final Long SOME_ID = 1L;

	private static final String WRITER = "George R. R. Martin";

	private Application app;

	@Mock
	private AutorRepository repoAutor;

	@Mock
	private LivroRepository repoLivro;

	private Livro livro;

	private Autor autor;

	private List<Livro> livros;

	/**
	 * Configura as aplicação mock
	 * 
	 * @throws Exception
	 *             lança exceção se a aplicação mock não puder ser criada
	 */
	@Before
	public void setUp() throws Exception {
		app = new Application(repoAutor, repoLivro);

		final GlobalSettings global = new GlobalSettings() {
			@SuppressWarnings("unchecked")
			@Override
			public <A> A getControllerInstance(Class<A> aClass) {
				return (A) app;
			}
		};
		setUpObjects();
		start(fakeApplication(global));
	}

	/**
	 * Configura os objetos a serem usados nos testes
	 */
	@SuppressWarnings("serial")
	private void setUpObjects() {
		livro = new Livro();
		livro.setId(SOME_ID);
		autor = new Autor();
		autor.setNome(WRITER);
		livros = new ArrayList<Livro>() {
			{
				add(livro);
			}
		};
	}

	/**
	 * Testa a url '/books' com o método http GET, que deve retornar todos os
	 * livros do sistema
	 */
	@Test
	public void testaBooks() {
		when(repoLivro.findAll()).thenReturn(livros);
		final Result result = route(fakeRequest(GET, "/books"));

		assertEquals(OK, status(result));
	}

	/**
	 * Testa o http POST na url '/books', que cria um novo livro no sistema
	 */
	@Test
	public void testaNewBook() {
		when(repoLivro.save(livro)).thenReturn(livro);
		when(repoLivro.findAll()).thenReturn(livros);
		final Result result = route(fakeRequest(POST, "/books"));

		assertEquals(SEE_OTHER, status(result));
	}

	/**
	 * Testa a url '/books/addAutor' com o método http GET, que adiciona um novo
	 * autor ao livro selecionado
	 */
	@Test
	public void testaAddAutor() {
		when(repoLivro.save(livro)).thenReturn(livro);
		when(repoAutor.save(autor)).thenReturn(autor);
		when(repoLivro.findOne(SOME_ID)).thenReturn(livro);
		when(repoLivro.findAll()).thenReturn(livros);
		final Result result = route(fakeRequest(GET, "/books/addAutor?livro="
				+ SOME_ID.intValue() + "&autor=" + WRITER));

		assertEquals(SEE_OTHER, status(result));
	}

	/**
	 * Teste a remoção de um livro do sistema, um POST na url
	 * '/books/:idLivro/delete'
	 */
	@Test
	public void testaDeleteBook() {
		final Result result = route(fakeRequest(POST,
				"/books/" + SOME_ID.intValue() + "/delete"));

		assertEquals(SEE_OTHER, status(result));
	}
}
