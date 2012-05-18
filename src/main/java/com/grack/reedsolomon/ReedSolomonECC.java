package com.grack.reedsolomon;

import java.util.Arrays;

/**
 * Reed-solomon ECC generator.
 * 
 * Derived from TI DaVinci Flash and Boot Utilities under GPL, so this code is
 * also under the GPL.
 */
public class ReedSolomonECC {
	/**
	 * Total number of transmitted symbols (message + parity)
	 */
	private int N;

	/**
	 * Total number of message symbols
	 */
	private int k;

	/**
	 * Total number of correctable symbol errors
	 */
	private int s;

	/**
	 * The generator polynomial.
	 */
	private int[] generatorPoly;

	/**
	 * The Galois field, GF(2^symbolBitWidth) under which all operations take
	 * place
	 */
	private GaloisField2 galoisField;

	public ReedSolomonECC(int messageSymbolCount, int maxCorrectibleErrorCount, int symbolBitWidth) {
		k = messageSymbolCount;
		s = maxCorrectibleErrorCount;
		N = k + 2 * s;

		// Create Binary Galois Field (that is GF(2^symbolBitWidth) )
		galoisField = new GaloisField2(symbolBitWidth);

		// Create the generator polynomial, g(x)
		generatorPoly = createGeneratorPolynomial(galoisField, maxCorrectibleErrorCount);
	}

	public int[] generateParity(int[] messageData) {
		if (messageData.length != k)
			throw new IllegalArgumentException("Wrong size.");

		// Parity is defined parityPoly(x) = x^2s * messagePoly(x) (mod
		// generatorPoly(x))

		// Create x^2s * messagePoly(x) by shifting data up by 2s positions
		int[] data = new int[N];
		System.arraycopy(messageData, 0, data, 2 * s, k);

		// Now do long division using generatorPoly, remainder is parity data
		// Use synthetic division since generatorPoly is monic
		for (int i = N - 1; i >= (2 * s); i--) {
			if (data[i] != 0) {
				for (int j = 1; j <= (2 * s); j++) {
					data[i - j] = data[i - j] ^ galoisField.multiply(data[i], generatorPoly[2 * s - j]);
				}
				// Set to zero
				data[i] = 0;
			}
		}

		// Return parity symbols
		return Arrays.copyOf(data, 2 * s);
	}

	private static int[] createGeneratorPolynomial(GaloisField2 gf, int maxCorrectibleErrorCount) {
		// Generator polynomial, g(x), is of order 2s, so has 2s+1 coefficients
		int[] g = new int[2 * maxCorrectibleErrorCount + 1];

		// Make g(x) = 1
		g[0] = 1;
		for (int i = 1; i <= (2 * maxCorrectibleErrorCount); i++) {
			// Always make coefficient of x^i term equal to 1
			g[i] = 1;

			// Below multiply (g(x) = g[0] + g[1]*x + ... + g[i]*(x^i)) by (x -
			// alpha^i)
			for (int j = (i - 1); j > 0; j--) {
				if (g[j] != 0)
					g[j] = g[j - 1] ^ gf.multiply(gf.exp(i), g[j]);
				else
					g[j] = g[j - 1];
			}

			// Coefficient of x^0 term is alpha^(1+2+...+i)
			g[0] = gf.exp(((i * (i + 1)) / 2));
		}

		return g;
	}

}
