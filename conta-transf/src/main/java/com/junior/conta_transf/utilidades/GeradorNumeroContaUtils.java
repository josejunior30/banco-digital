package com.junior.conta_transf.utilidades;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;

public final class GeradorNumeroContaUtils {

	private GeradorNumeroContaUtils() {
	}

	public static String gerarNumeroConta(String agencia4, Predicate<String> existeNumero) {
		String agencia = somenteDigitos(agencia4);
		if (agencia.length() != 4) {
			agencia = String.format("%04d", parseIntSeguro(agencia, 1));
		}

		for (int attempt = 0; attempt < 20; attempt++) {
			int raw = ThreadLocalRandom.current().nextInt(0, 100_000_000);
			String conta8 = String.format("%08d", raw);

			String digitos = agencia + conta8;
			int dv = calcularDigitoVerificadorLuhn(digitos);

			String formatado = agencia + "-" + conta8 + "-" + dv;

			if (existeNumero == null || !existeNumero.test(formatado)) {
				return formatado;
			}
		}

		throw new IllegalStateException("Falha ao gerar número de conta único");
	}

	public static String somenteDigitos(String s) {
		if (s == null)
			return "";
		return s.replaceAll("\\D+", "");
	}

	public static int parseIntSeguro(String s, int fallback) {
		try {
			return Integer.parseInt(s);
		} catch (Exception ignored) {
			return fallback;
		}
	}

	public static int calcularDigitoVerificadorLuhn(String digitos) {
		int soma = 0;
		boolean dobrar = true;

		for (int i = digitos.length() - 1; i >= 0; i--) {
			int d = digitos.charAt(i) - '0';
			int add = d;

			if (dobrar) {
				add = d * 2;
				if (add > 9)
					add -= 9;
			}

			soma += add;
			dobrar = !dobrar;
		}

		return (10 - (soma % 10)) % 10;
	}
}