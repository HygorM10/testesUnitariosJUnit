package br.ce.wcaquino.matchers;

import static br.ce.wcaquino.utils.DataUtils.isMesmaData;
import static br.ce.wcaquino.utils.DataUtils.obterDataComDiferencaDias;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
		Date data = DataUtils.obterDataComDiferencaDias(diferencaDeDias);
		DateFormat format = new SimpleDateFormat("dd/MM/YYYY");
		description.appendText(format.format(data));
	}

	@Override
	protected boolean matchesSafely(Date data) {
		return isMesmaData(data, obterDataComDiferencaDias(diferencaDeDias));
	}

}
