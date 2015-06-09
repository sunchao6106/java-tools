package com.sunchao.complex.base64;

import java.io.IOException;
import java.io.OutputStream;

/**<p>A base64 output stream</p>
 * <p>
 *   it encodes base64 everything passed on the stream, and puts the encoded data
 *   into the underlying stream.
 * </p>
 * @author Administrator
 *
 */

public class Base64OutputStream extends OutputStream {
    /**
     * The underlying stream.
     */
	private OutputStream outputStream = null;
	
	/**
	 * The value buffer.
	 */
	private int buffer = 0;
	
	/**
	 * How many bytes are currently in the value buffer.
	 */
	private int bytecounter = 0;
	
	/**
	 * the counter for current line length.
	 */
	private int linecounter = 0;
	
	/**
	 * the requested line length
	 */
	private int linelength;
	
	/**
	 * <p>It builds a base64 encoding output stream writing the encoded data in
	 * the given underlying stream.
	 * </p>
	 * 
	 * <p>The encoded data is wrapped into a new line(with CRLF sequence) every 76 bytes
	 * send to the underlying stream.
	 * </p>
	 * 
	 * @param outputStream the underlying stream
	 */
    public Base64OutputStream(OutputStream outputStream) {
		this(outputStream, 76);
	}
    
    /**  
     * <p>It builds a base64 encoding output stream writing the encoding data in 
     * the given underlying stream.
     * </p>
     * 
     * <p>The encoded data is wrapped into a new line(with CRLF sequence) every <em>
     * wrapAt</em> bytes send to the underlying stream. If the <em>wrapAt</em> supplied
     * value is less than 1,the encoded data is not to be wrapped.
     * 
     * 
     * @param outputStream
     *             the underlying stream
     * @param wrapAt
     *             the max line length for encoded data, if the supplied value is less than
     *               1,no wrap is applied.
     */
    public Base64OutputStream(OutputStream outputStream, int wrapAt) {
		this.outputStream = outputStream;
		this.linelength = wrapAt;
	}
    
    
	@Override
	public void write(int b) throws IOException {
		int value = (b & 0xFF) << (16 - (bytecounter * 8));
		buffer = buffer | value;
		bytecounter++;
		if(bytecounter == 3)
			commit();
	}
	
	public void close() throws IOException{
		commit();
		outputStream.close();
	}
	
	/**
	 * <p>It commit 4 bytes to the underlying stream</p>
	 * @throws IOException
	 */
	protected void commit() throws IOException {
		if (bytecounter > 0) {
			if (linelength > 0 && linecounter == linelength) {
				outputStream.write("\r\n".getBytes());
				linecounter = 0;
			}
			char b1 = Shared.chars.charAt((buffer << 8) >>> 26); //have 6 bits
			char b2 = Shared.chars.charAt((buffer << 14) >>> 26);
			char b3 = (bytecounter < 2) ? Shared.pad : Shared.chars.charAt((buffer << 20) >>> 26);
			char b4 = (bytecounter < 3) ? Shared.pad : Shared.chars.charAt((buffer << 26) >>> 26);
			outputStream.write(b1);
			outputStream.write(b2);
			outputStream.write(b3);
			outputStream.write(b4);
			linecounter += 4;
			bytecounter = 0 ;
			buffer = 0;
		}
	}
}

