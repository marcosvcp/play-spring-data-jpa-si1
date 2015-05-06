package controllers;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import models.Autor;
import models.AutorRepository;
import models.Livro;
import models.LivroRepository;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;

import com.google.common.collect.Lists;

/**
 * The main set of web services.
 */
@Named
@Singleton
public class Application extends Controller {
	static Form<Livro> bookForm = Form.form(Livro.class);
	private static final int FIRST_PAGE = 1;
	private static final int DEFAULT_RESULTS = 10;
	private final AutorRepository autorRepository;
	private final LivroRepository livroRepository;

	// Por injeção de dependencia inicia os repositórios
	@Inject
	public Application(final AutorRepository personRepository,
			final LivroRepository livroRepository) {
		this.autorRepository = personRepository;
		this.livroRepository = livroRepository;
	}

	public Result index() {
		return redirect(routes.Application.books(FIRST_PAGE, DEFAULT_RESULTS));
	}

	// Notação transactional sempre que o método fizer transação com o Banco de
	// Dados.
	public Result books(int page, int pageSize) {
		page = page >= FIRST_PAGE ? page : FIRST_PAGE;
		pageSize = pageSize >= FIRST_PAGE ? pageSize : DEFAULT_RESULTS;
		Long entityNumber = livroRepository.count();
		// Se a página pedida for maior que o número de entidades
		if (page > (entityNumber / pageSize)) {
			// A última página
			page = (int) (Math.ceil(entityNumber
					/ Float.parseFloat(String.valueOf(pageSize))));
		}
		session("actualPage", String.valueOf(page));

		// FIXME Colocar paginação
		return ok(views.html.index.render(
				Lists.newArrayList(livroRepository.findAll()), bookForm));
	}

	// Notação transactional sempre que o método fizer transação com o Banco de
	// Dados.
	public Result newBook() {
		// O formulário dos Livros Preenchidos
		Form<Livro> filledForm = bookForm.bindFromRequest();
		if (filledForm.hasErrors()) {
			return badRequest(views.html.index.render(firstPage(), filledForm));
		} else {
			Livro livro = filledForm.get();
			// Persiste o Livro criado
			livroRepository.save(livro);
			// Espelha no Banco de Dados
			return redirect(routes.Application.books(FIRST_PAGE,
					DEFAULT_RESULTS));
		}
	}

	public Result addAutor(Long id, String nome) {
		criaAutorDoLivro(id, nome);
		return ok(views.html.index.render(firstPage(), bookForm));
	}

	private void criaAutorDoLivro(Long id, String nome) {
		// Cria um novo Autor para um livro de {@code id}
		Autor novoAutor = new Autor();
		novoAutor.setNome(nome);
		// Procura um objeto da classe Livro com o {@code id}
		Livro livroDaListagem = livroRepository.findOne(id);
		// Save o novo autor no banco de dados
		autorRepository.save(novoAutor);
		livroDaListagem.getAutores().add(novoAutor);
		novoAutor.getLivros().add(livroDaListagem);
		// Atualiza o livro no banco de dados
		livroRepository.save(livroDaListagem);
	}

	// Notação transactional sempre que o método fizer transação com o Banco de
	// Dados.
	public Result deleteBook(Long id) {
		// Remove o Livro pelo Id
		livroRepository.delete(id);
		return redirect(routes.Application.books(FIRST_PAGE, DEFAULT_RESULTS));
	}

	/**
	 * Retorna a primeira página do banco de dados
	 */
	private List<Livro> firstPage() {
		// FIXME Paignar
		return Lists.newArrayList(livroRepository.findAll());
	}
}
