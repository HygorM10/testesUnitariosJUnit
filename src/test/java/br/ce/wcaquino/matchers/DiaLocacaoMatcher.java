package br.ce.wcaquino.matchers;

import static br.ce.wcaquino.utils.DataUtils.adicionarDias;
import static br.ce.wcaquino.utils.DataUtils.obterDataComDiferencaDias;

import java.util.Date;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import br.ce.wcaquino.utils.DataUtils;

public class DiaLocacaoMatcher extends TypeSafeMatcher<Date> {
	
	private Integer diferencaDeDias;
	
	public DiaLocacaoMatcher(Integer diferencaDeDias) {
		this.diferencaDeDias = diferencaDeDias;
	}
	
	public void describeTo(Description description) {
		Date data = adicionarDias(new Date(), diferencaDeDias);
		description.appendText(data.toString());
	}

	@Override
	protected boolean matchesSafely(Date data) {
		return DataUtils.isMesmaData(data, obterDataComDiferencaDias(diferencaDeDias));
	}

}
