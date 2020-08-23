package br.ce.wcaquino.matchers;

import java.util.Calendar;

public class MatchersProprios {

	public static DiaSemanaMatcher caiEm(Integer diaSemana) {
		return new DiaSemanaMatcher(diaSemana);
	}
	
	public static DiaSemanaMatcher caiNumaSegunda() {
		return new DiaSemanaMatcher(Calendar.MONDAY);
	}
	
	public static DiaLocacaoMatcher ehHojeComDiferencaDias(Integer diferencaDeDias) {
		return new DiaLocacaoMatcher(diferencaDeDias);
	}
	
	public static DiaLocacaoMatcher ehHoje() {
		return new DiaLocacaoMatcher(0);
	}
}
