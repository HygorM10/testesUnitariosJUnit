package br.ce.wcaquino.servicos;

import static br.ce.wcaquino.utils.DataUtils.adicionarDias;
import static br.ce.wcaquino.utils.DataUtils.isMesmaData;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Assume;
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
import br.ce.wcaquino.utils.DataUtils;

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
	public void deveAlugarFilmeSucesso() throws Exception {
		Assume.assumeFalse(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));
		
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
	public void naoDeveAlugarFilmeSemEstoque() throws Exception {
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
	public void naoDeveAlugarFilmeSemUsuario() throws FilmeSemEstoqueException {
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
	public void naoDeveAlugarFilmeSemFilme() throws FilmeSemEstoqueException, LocadoraException {
		// cenario
		Usuario usuario = new Usuario("Hygor Martins");

		// Acao
		exception.expect(LocadoraException.class);
		exception.expectMessage("Filme Vazio");

		locacaoService.alugarFilme(usuario, null);
	}

	@Test
	public void deveAlugarMaisDeUmFilme() throws Exception {
		Assume.assumeFalse(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));
		
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
	
	@Test
	public void devePagar75pctNoTerceiroFilme() throws FilmeSemEstoqueException, LocadoraException {
		//cenario
		Usuario usuario = new Usuario("Hygor");
		Filme filme = new Filme("Filme 1", 1, 10.00);
		Filme filme2 = new Filme("Filme 2", 1, 10.00);
		Filme filme3 = new Filme("Filme 3", 1, 20.00);
		
		filmes.add(filme);
		filmes.add(filme2);
		filmes.add(filme3);
		
		// acao
		Locacao locacao = locacaoService.alugarFilme(usuario, filmes);
		
		//Verificacoes
		assertThat(locacao.getValor(), is(35.00));
	}
	
	@Test
	public void devePagar50pctNoQuartoFilme() throws FilmeSemEstoqueException, LocadoraException {
		//cenario
		Usuario usuario = new Usuario("Hygor");
		Filme filme = new Filme("Filme 1", 1, 10.00);
		Filme filme2 = new Filme("Filme 2", 1, 10.00);
		Filme filme3 = new Filme("Filme 3", 1, 20.00);
		Filme filme4 = new Filme("Filme 4", 1, 25.00);
		
		filmes.add(filme);
		filmes.add(filme2);
		filmes.add(filme3);
		filmes.add(filme4);
		
		// acao
		Locacao locacao = locacaoService.alugarFilme(usuario, filmes);
		
		//Verificacoes
		assertThat(locacao.getValor(), is(47.50));
	}
	
	@Test
	public void devePagar25pctNoQuintoFilme() throws FilmeSemEstoqueException, LocadoraException {
		//cenario
		Usuario usuario = new Usuario("Hygor");
		Filme filme = new Filme("Filme 1", 1, 10.00);
		Filme filme2 = new Filme("Filme 2", 1, 10.00);
		Filme filme3 = new Filme("Filme 3", 1, 20.00);
		Filme filme4 = new Filme("Filme 4", 1, 25.00);
		Filme filme5 = new Filme("Filme 5", 1, 30.00);
		
		filmes.add(filme);
		filmes.add(filme2);
		filmes.add(filme3);
		filmes.add(filme4);
		filmes.add(filme5);
		
		// acao
		Locacao locacao = locacaoService.alugarFilme(usuario, filmes);
		
		//Verificacoes
		assertThat(locacao.getValor(), is(55.00));
	}
	
	@Test
	public void deveGanhar100pctDescontoSextoFilme() throws FilmeSemEstoqueException, LocadoraException {
		//cenario
		Usuario usuario = new Usuario("Hygor");
		Filme filme = new Filme("Filme 1", 1, 10.00);
		Filme filme2 = new Filme("Filme 2", 1, 10.00);
		Filme filme3 = new Filme("Filme 3", 1, 20.00);
		Filme filme4 = new Filme("Filme 4", 1, 25.00);
		Filme filme5 = new Filme("Filme 5", 1, 30.00);
		Filme filme6 = new Filme("Filme 6", 1, 35.00);
		
		filmes.add(filme);
		filmes.add(filme2);
		filmes.add(filme3);
		filmes.add(filme4);
		filmes.add(filme5);
		filmes.add(filme6);
		
		// acao
		Locacao locacao = locacaoService.alugarFilme(usuario, filmes);
		
		//Verificacoes
		assertThat(locacao.getValor(), is(55.00));
	}
	
	@Test
	public void naoDeveCederDescontoEmApenasUmFilme() throws FilmeSemEstoqueException, LocadoraException {
		//cenario
		Usuario usuario = new Usuario("Hygor");
		Filme filme = new Filme("Filme 1", 1, 10.00);
		
		filmes.add(filme);
		
		// acao
		Locacao locacao = locacaoService.alugarFilme(usuario, filmes);
		
		//Verificacoes
		assertThat(locacao.getValor(), is(filme.getPrecoLocacao()));
	}
	
	@Test
	public void naoDeveDevolverFilmeNoDomingo() throws FilmeSemEstoqueException, LocadoraException {
		Assume.assumeTrue(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));
		
		//cenario
		Usuario usuario = new Usuario("Hygor");
		Filme filme = new Filme("Filme 1", 1, 10.00);
		
		filmes.add(filme);
		
		//acao
		Locacao locacao = locacaoService.alugarFilme(usuario, filmes);
		
		//verificacao
		boolean ehSegunda = DataUtils.verificarDiaSemana(locacao.getDataRetorno(), Calendar.MONDAY);
		Assert.assertTrue(ehSegunda);
		
		
	}
}
