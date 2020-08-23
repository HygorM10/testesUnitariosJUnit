package br.ce.wcaquino.servicos;

import static br.ce.wcaquino.builders.FilmeBuilder.umFilme;
import static br.ce.wcaquino.builders.LocacaoBuilder.umLocacao;
import static br.ce.wcaquino.builders.UsuarioBuilder.umUsuario;
import static br.ce.wcaquino.matchers.MatchersProprios.caiNumaSegunda;
import static br.ce.wcaquino.matchers.MatchersProprios.ehHoje;
import static br.ce.wcaquino.matchers.MatchersProprios.ehHojeComDiferencaDias;
import static br.ce.wcaquino.utils.DataUtils.adicionarDias;
import static br.ce.wcaquino.utils.DataUtils.isMesmaData;
import static br.ce.wcaquino.utils.DataUtils.obterDataComDiferencaDias;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
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
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;
import br.ce.wcaquino.servicos.DAOS.LocacaoDAO;
import br.ce.wcaquino.utils.DataUtils;

public class LocacaoServiceTest {

	@InjectMocks
	private LocacaoService locacaoService;
	
	@Mock
	private LocacaoDAO dao;
	@Mock
	private SPCService spc;
	@Mock
	private EmailService email;

	@Rule
	public ErrorCollector error = new ErrorCollector();

	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void deveAlugarFilmeSucesso() throws Exception {
		Assume.assumeFalse(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));
		
		// Cenario
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().agora());

		// Ação
		Locacao locacao = locacaoService.alugarFilme(usuario, filmes);

		// Verificações
		error.checkThat(locacao.getValor(), is(equalTo(10.00)));
//		error.checkThat(isMesmaData(locacao.getDataLocacao(), new Date()), is(true));
		error.checkThat(locacao.getDataLocacao(), ehHoje());
//		error.checkThat(isMesmaData(locacao.getDataRetorno(), adicionarDias(new Date(), 1)), is(true));
		error.checkThat(locacao.getDataRetorno(), ehHojeComDiferencaDias(1));
	}

	@Test(expected = FilmeSemEstoqueException.class)
	public void naoDeveAlugarFilmeSemEstoque() throws Exception {
		// Cenario
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().semEstoque().agora());

		// Ação
		locacaoService.alugarFilme(usuario, filmes);
	}

	@Test
	public void naoDeveAlugarFilmeSemUsuario() throws FilmeSemEstoqueException {
		// Cenario
		List<Filme> filmes = Arrays.asList(umFilme().agora());

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
		Usuario usuario = umUsuario().agora();

		// Acao
		exception.expect(LocadoraException.class);
		exception.expectMessage("Filme Vazio");

		locacaoService.alugarFilme(usuario, null);
	}

	@Test
	public void deveAlugarMaisDeUmFilme() throws Exception {
		Assume.assumeFalse(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));
		
		// cenario
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().agora(), umFilme().agora());

		// Ação
		Locacao locacao = locacaoService.alugarFilme(usuario, filmes);

		// Verificações
		error.checkThat(locacao.getValor(), is(equalTo(20.00)));
		error.checkThat(isMesmaData(locacao.getDataLocacao(), new Date()), is(true));
		error.checkThat(isMesmaData(locacao.getDataRetorno(), adicionarDias(new Date(), 1)), is(true));
	}
	
	@Test
	public void naoDeveCederDescontoEmApenasUmFilme() throws FilmeSemEstoqueException, LocadoraException {
		//cenario
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().agora());
		
		// acao
		Locacao locacao = locacaoService.alugarFilme(usuario, filmes);
		
		//Verificacoes
		assertThat(locacao.getValor(), is(10.00));
	}
	
	@Test
	public void naoDeveDevolverFilmeNoDomingo() throws FilmeSemEstoqueException, LocadoraException {
		Assume.assumeTrue(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));
		
		//cenario
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().agora());
		
		//acao
		Locacao locacao = locacaoService.alugarFilme(usuario, filmes);
		
		//verificacao
		assertThat(locacao.getDataRetorno(), caiNumaSegunda());
	}
	
	@Test
	public void naoDeveAlugarFilmeParaNegativadoSPC() throws Exception {
		//cenario
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().agora());
		
		when(spc.possuiNegativacao(Mockito.any(Usuario.class))).thenReturn(true);
		
		exception.expect(LocadoraException.class);
		exception.expectMessage("Usuario Negativado");
		
		//acao
		locacaoService.alugarFilme(usuario, filmes);
		
		//verificacao
		verify(spc).possuiNegativacao(usuario);
		
	}
	
	@Test
	public void deveTratarErroNoSPC() throws Exception {
		//cenario
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().agora());
		
		when(spc.possuiNegativacao(usuario)).thenThrow(new Exception("Falha catrastrofica"));
		
		//verificacao
		exception.expect(LocadoraException.class);
		exception.expectMessage("Problemas com SPC, tente novamente");
		
		//acao
		locacaoService.alugarFilme(usuario, filmes);
	}
	
	@Test
	public void deveEnviarEmailParaLocacoesAtrasadas() {
		//cenario
		Usuario usuario = umUsuario().agora();
		Usuario usuario2 = umUsuario().comNome("Usuario em dia").agora();
		Usuario usuario3 = umUsuario().comNome("Outro atrasado").agora();
		List<Locacao> locacoes = Arrays.asList(
				umLocacao()
					.comUsuario(usuario)
					.atrasado()
					.comDataRetorno(obterDataComDiferencaDias(-2))
					.agora(),
				umLocacao()
					.comUsuario(usuario2)
					.agora(),
				umLocacao()
					.comUsuario(usuario3)
					.atrasado()
					.comDataRetorno(obterDataComDiferencaDias(-2))
					.agora()
				);
		
		when(dao.obterLocacoesPendetes()).thenReturn(locacoes);
		
		//acao
		locacaoService.notificarAtrasos();
		
		//verificacao
		verify(email).notificarAtraso(usuario);
		verify(email).notificarAtraso(usuario3);
		verify(email, times(2)).notificarAtraso(Mockito.any(Usuario.class));
		verify(email, never()).notificarAtraso(usuario2);
		verifyNoMoreInteractions(email);
	}
	
	@Test
	public void deveProrrogarUmaLocacao() {
		//cenario
		Locacao locacao = umLocacao().agora();
		
		//acao
		locacaoService.prorrogarLocacao(locacao, 3);
		
		//verificacao
		ArgumentCaptor<Locacao> argumentCaptor = ArgumentCaptor.forClass(Locacao.class);
		Mockito.verify(dao).salvar(argumentCaptor.capture());
		Locacao locacaoRetornada = argumentCaptor.getValue();
		
		error.checkThat(locacaoRetornada.getValor(), is(30.00));
		error.checkThat(locacaoRetornada.getDataLocacao(), ehHoje());
		error.checkThat(locacaoRetornada.getDataRetorno(), ehHojeComDiferencaDias(3));
	}
}
