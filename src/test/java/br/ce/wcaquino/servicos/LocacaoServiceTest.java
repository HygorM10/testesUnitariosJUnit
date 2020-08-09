package br.ce.wcaquino.servicos;

import static br.ce.wcaquino.utils.DataUtils.adicionarDias;
import static br.ce.wcaquino.utils.DataUtils.isMesmaData;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;

import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;

public class LocacaoServiceTest {

	private LocacaoService locacaoService;

	private Usuario usuario;

	private List<Filme> filmes;

	@Rule
	public ErrorCollector error = new ErrorCollector();

	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Before
	public void setup() {
		locacaoService = new LocacaoService();
		usuario = new Usuario();
		filmes = new ArrayList<Filme>();
	}

	@Test
	public void testeLocacao() throws Exception {
		// Cenario
		usuario.setNome("Hygor Martins");

		Filme filme = new Filme();
		filme.setEstoque(2);
		filme.setNome("Aprendendo testes unitarios em Java");
		filme.setPrecoLocacao(22.90);

		filmes.add(filme);

		// Ação
		Locacao locacao = locacaoService.alugarFilme(usuario, filmes);

		// Verificações
		error.checkThat(locacao.getValor(), is(equalTo(22.90)));
		error.checkThat(isMesmaData(locacao.getDataLocacao(), new Date()), is(true));
		error.checkThat(isMesmaData(locacao.getDataRetorno(), adicionarDias(new Date(), 1)), is(true));
	}

	@Test(expected = FilmeSemEstoqueException.class)
	public void testLocacao_filmeSemEstoque() throws Exception {
		// Cenario
		usuario.setNome("Hygor Martins");

		Filme filme = new Filme();
		filme.setEstoque(0);
		filme.setNome("Aprendendo testes unitarios em Java");
		filme.setPrecoLocacao(22.90);

		filmes.add(filme);

		// Ação
		locacaoService.alugarFilme(usuario, filmes);
	}

	@Test
	public void testLocacao_usuarioVazio() throws FilmeSemEstoqueException {
		// Cenario
		Filme filme = new Filme("Filme 2", 1, 4.0);

		filmes.add(filme);

		// Acao
		try {
			locacaoService.alugarFilme(null, filmes);
			Assert.fail();
		} catch (LocadoraException e) {
			assertThat(e.getMessage(), is("Usuario Vazio"));
		}
	}

	@Test
	public void testLocacao_FilmeVazio() throws FilmeSemEstoqueException, LocadoraException {
		// cenario
		Usuario usuario = new Usuario("Hygor Martins");

		// Acao
		exception.expect(LocadoraException.class);
		exception.expectMessage("Filme Vazio");

		locacaoService.alugarFilme(usuario, null);
	}

	@Test
	public void testLocacao_MaisDeUmFilme() throws Exception {
		// cenario
		Usuario usuario = new Usuario("Hygor Martins");
		Filme filme = new Filme("Filme 4", 1, 5.0);
		Filme filme2 = new Filme("Filme 5", 3, 10.0);

		filmes.add(filme);
		filmes.add(filme2);

		// Ação
		Locacao locacao = locacaoService.alugarFilme(usuario, filmes);

		// Verificações
		error.checkThat(locacao.getValor(), is(equalTo(15.00)));
		error.checkThat(isMesmaData(locacao.getDataLocacao(), new Date()), is(true));
		error.checkThat(isMesmaData(locacao.getDataRetorno(), adicionarDias(new Date(), 1)), is(true));
	}
}
