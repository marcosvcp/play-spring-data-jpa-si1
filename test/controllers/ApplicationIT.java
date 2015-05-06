package controllers;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.GET;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.fakeRequest;
import static play.test.Helpers.route;
import static play.test.Helpers.status;
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
 * An integration test focused on testing our routes configuration and
 * interactions with our controller. However we can mock repository interactions
 * here so we don't need a real db.
 */
@RunWith(MockitoJUnitRunner.class)
public class ApplicationIT extends WithApplication {

	private static final Long SOME_ID = 1L;

	private Application app;

	@Mock
	private AutorRepository repo;

	@Mock
	private LivroRepository repoLivro;

	@Before
	public void setUp() throws Exception {
		app = new Application(repo, repoLivro);

		final GlobalSettings global = new GlobalSettings() {
			@Override
			public <A> A getControllerInstance(Class<A> aClass) {
				return (A) app;
			}
		};

		start(fakeApplication(global));
	}

	@Test
	public void indexSavesDataAndReturnsId() {
		final Livro livro = new Livro();
		livro.setId(SOME_ID);
		when(repoLivro.save(any(Livro.class))).thenReturn(livro);
		when(repoLivro.findOne(SOME_ID)).thenReturn(livro);

		final Result result = route(fakeRequest(GET, "/books"));

		assertEquals(OK, status(result));
	}

}
