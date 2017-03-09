package br.com.coder.arqprime.model.utils;

import static org.junit.Assert.*;

import org.junit.Test;

public class GenerateMD5Test {

	@Test
	public void testGenerateMD5() {
		String generateMD5 = GenerateMD5.generateMD5("123");
		System.out.println(generateMD5);
		assertTrue("202cb962ac59075b964b07152d234b70".equals(generateMD5));
	}

}
