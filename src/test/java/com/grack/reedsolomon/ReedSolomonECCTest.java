package com.grack.reedsolomon;

import static org.junit.Assert.*;

import org.junit.Test;

public class ReedSolomonECCTest {
	/**
	 * Test against a known NAND ECC output.
	 */
	@Test
	public void test00() {
		ReedSolomonECC ecc = new ReedSolomonECC(512, 4, 10);
		int[] data = new int[512];
		for (int i = 0; i < data.length; i++)
			data[i] = 0x00;
		int[] parity = ecc.generateParity(data);
		int[] bytes = toBytes(parity);

		// All zeros
		assertArrayEquals(new int[10], bytes);
	}

	/**
	 * Test against a known NAND ECC output.
	 */
	@Test
	public void test01() {
		ReedSolomonECC ecc = new ReedSolomonECC(512, 4, 10);
		int[] data = new int[512];
		for (int i = 0; i < data.length; i++)
			data[i] = 0x01;
		int[] parity = ecc.generateParity(data);
		int[] bytes = toBytes(parity);

		// All zeros
		assertArrayEquals(new int[] { 0xdd, 0x33, 0x7b, 0x1b, 0xf5, 0xda, 0xe2, 0x00, 0x99, 0x67 }, bytes);
	}

	/**
	 * Test against a known NAND ECC output.
	 */
	@Test
	public void testFF() {
		ReedSolomonECC ecc = new ReedSolomonECC(512, 4, 10);
		int[] data = new int[512];
		for (int i = 0; i < data.length; i++)
			data[i] = 0xff;
		int[] parity = ecc.generateParity(data);
		int[] bytes = toBytes(parity);

		assertArrayEquals(new int[] { 0xcf, 0xd8, 0x9d, 0x54, 0xa7, 0x76, 0x25, 0x87, 0x74, 0x52 }, bytes);
	}

	/**
	 * Naive conversion from 8 10-bit values to 10 8-bit values using strings.
	 * It's more important to be right here than fast.
	 */
	private int[] toBytes(int[] parity) {
		int[] bytes = new int[10];
		StringBuilder binary = new StringBuilder();
		for (int i = 0; i < parity.length; i++) {
			String x = "0000000000" + Integer.toBinaryString(parity[i]);
			x = x.substring(x.length() - 10);
			binary.append(x);
		}

		for (int i = 0; i < 10; i++) {
			int x = Integer.parseInt(binary.substring(i * 8, i * 8 + 8), 2);
			bytes[i] = x;
		}

		return bytes;
	}
}
