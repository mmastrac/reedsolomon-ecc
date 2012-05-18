package com.grack.reedsolomon;

import static org.junit.Assert.*;

import org.junit.Test;

public class GaloisFieldTest {
	/**
	 * Test multiplication over the field.
	 */
	@Test
	public void testMultiply() {
		GaloisField2 gf = new GaloisField2(10);
		assertEquals(512, gf.multiply(256, 2));
		assertEquals(9, gf.multiply(512, 2));
		assertEquals(18, gf.multiply(512, 4));
		assertEquals(36, gf.multiply(512, 8));
		assertEquals(488, gf.multiply(0xff, 0xff));
	}

	/**
	 * Test exponentiation.
	 */
	@Test
	public void testExp() {
		GaloisField2 gf = new GaloisField2(10);
		assertEquals(1, gf.exp(0));
		assertEquals(2, gf.exp(1));
		assertEquals(4, gf.exp(2));
		assertEquals(16, gf.exp(4));
		assertEquals(512, gf.exp(9));
		assertEquals(9, gf.exp(10));
		assertEquals(1, gf.exp(1023));
		assertEquals(2, gf.exp(1024));
	}
}
