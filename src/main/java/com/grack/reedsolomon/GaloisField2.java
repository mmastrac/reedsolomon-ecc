package com.grack.reedsolomon;

/**
 * Galios field GF(p^n) over p = 2.
 * 
 * Derived from TI DaVinci Flash and Boot Utilities under GPL, so this code is
 * also under the GPL.
 */
public class GaloisField2 {
	/**
	 * Lee & Messerschmitt, p. 453
	 */
	private static final int[] PRIMITIVE_POLYNOMIALS = new int[] {
			//
			0x00000007, // 2-nd: poly = x^2 + x + 1
			0x0000000B, // 3-rd: poly = x^3 + x + 1
			0x00000013, // 4-th: poly = x^4 + x + 1
			0x00000025, // 5-th: poly = x^5 + x^2 + 1
			0x00000043, // 6-th: poly = x^6 + x + 1
			0x00000089, // 7-th: poly = x^7 + x^3 + 1
			0x0000011D, // 8-th: poly = x^8 + x^4 + x^3 + x^2 + 1
			0x00000211, // 9-th: poly = x^9 + x^4 + 1
			0x00000409, // 10-th: poly = x^10 + x^3 + 1
			0x00000805, // 11-th: poly = x^11 + x^2 + 1
			0x00001053, // 12-th: poly = x^12 + x^6 + x^4 + x + 1
			0x0000201B, // 13-th: poly = x^13 + x^4 + x^3 + x + 1
			0x00004443, // 14-th: poly = x^14 + x^10 + x^6 + x + 1
			0x00008003, // 15-th: poly = x^15 + x + 1
			0x0001100B // 16-th: poly = x^16 + x^12 + x^3 + x + 1
	};

	/**
	 * Currently selected irreducible poly for given r.
	 */
	private int poly;

	/**
	 * Length of exp/log tables
	 */
	private int tableLength;

	private int[] exp;
	private int[] log;

	/**
	 * Create a new Galois field, GF(2^r).
	 */
	public GaloisField2(int r) {
		poly = PRIMITIVE_POLYNOMIALS[r - 2];

		tableLength = (1 << r);

		generateLogExp(r);
	}

	/**
	 * Generate the log/exp tables for fast lookup.
	 */
	private void generateLogExp(int r) {
		exp = new int[tableLength];
		log = new int[tableLength];

		exp[0] = 1;
		log[0] = 1;

		int highBit = (1 << (r - 1));

		// Generate the exponential table via shift and xor
		for (int i = 1; i < tableLength; i++) {
			boolean highBitSet = (exp[i - 1] & highBit) != 0;
			exp[i] = exp[i - 1] << 1; // Multiply by the primitive element, 2
			if (highBitSet)
				exp[i] ^= poly;
			log[exp[i]] = i;
		}
	}

	/**
	 * Exponential.
	 */
	public int exp(int x) {
		return this.exp[x % (tableLength - 1)];
	}

	/**
	 * Multiplication over the field (done via lookup).
	 */
	public int multiply(int x, int y) {
		if ((x | y) == 0)
			return 0;

		return exp(log[x] + log[y]);
	}
}