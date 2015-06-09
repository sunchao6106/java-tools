package com.sunchao.complex.base64;

import java.io.IOException;

public class Test {

	public static void main(String args[]) throws IOException{
		String s ="sunchao6106";
		
		String s1 =Base64.encoding(s);
	String s2 =Base64.decode(s1);
		System.out.println(s1);
		System.out.println(s2);
	}
}
