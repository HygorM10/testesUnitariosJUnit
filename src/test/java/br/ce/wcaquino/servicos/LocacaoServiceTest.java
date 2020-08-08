package br.ce.wcaquino.servicos;

import static br.ce.wcaquino.utils.DataUtils.adicionarDias;
import static br.ce.wcaquino.utils.DataUtils.isMesmaData;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Date;

import org.junit.Assert;
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

	@Rule
	public ErrorCollector error = new ErrorCollector();
	
	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Test
	public void testeLocacao() throws Exception {
		// Cenario
		LocacaoService locacaoService = new LocacaoService();

		Usuario usuario = new Usuario();
		usuario.setNome("Hygor Martins");

		Filme filme = new Filme();
		filme.setEstoque(2);
		filme.setNome("Aprendendo testes unitarios em Java");
		filme.setPrecoLocacao(22.90);

		// Ação
		Locacao locacao = locacaoService.alugarFilme(usuario, filme);

		// Verificações
		error.checkThat(locacao.getValor(), is(equalTo(22.90)));
		error.checkThat(isMesmaData(locacao.getDataLocacao(), new Date()), is(true));
		error.checkThat(isMesmaData(locacao.getDataRetorno(), adicionarDias(new Date(), 1)), is(true));
	}

	@Test(expected = FilmeSemEstoqueException.class)
	public void testLocacao_filmeSemEstoque() throws Exception {
		// Cenario
		LocacaoService locacaoService = new LocacaoService();

		Usuario usuario = new Usuario();
		usuario.setNome("Hygor Martins");

		Filme filme = new Filme();
		filme.setEstoque(0);
		filme.setNome("Aprendendo testes unitarios em Java");
		filme.setPrecoLocacao(22.90);

		// Ação
		locacaoService.alugarFilme(usuario, filme);
	}
	
	@Test
	public void testLocacao_usuarioVazio() throws FilmeSemEstoqueException {
		// Cenario
		LocacaoService locacaoService = new LocacaoService();
		Filme filme = new Filme("Filme 2", 1, 4.0);
		
		// Acao
		try {
			locacaoService.alugarFilme(null, filme);
			Assert.fail();
		} catch (LocadoraException e) {
			assertThat(e.getMessage(), is("Usuario Vazio"));
		}
	}
	
	@Test
	public void testLocacao_FilmeVazio() throws FilmeSemEstoqueException, LocadoraException {
		// cenario
		
		LocacaoService locacaoService = new LocacaoService();
		Usuario usuario =  new Usuario("Hygor Martins");
		
		// Acao
		
		exception.expect(LocadoraException.class);
		exception.expectMessage("Filme Vazio");
		
		locacaoService.alugarFilme(usuario, null);

	}
}
