package com.sunchao.complex.base64;

import java.io.IOException;
import java.io.InputStream;

/**
 * <p>A base64 encoding input stream</p>
 *  A <em> Base64InputStream</em> reads from the underlying stream which is
 *  supposed to be a base64 encoded stream. <em>Base64InputStream</em> decodes
 *  the data reads from the underlying stream and decodes the data return to the 
 * caller.
 * </p>
 * @author Administrator
 *
 */
public class Base64InputStream extends InputStream{
   
	/**
    * the underlying stream.
    */
	private InputStream inputStream;
	
	/**
	 * The buffer.
	 */
	private int[] buffer;
	
	/**
	 * The counter for value in the buffer.
	 */
	private int buffercounter;
	
	/**
	 * End-of-stream flag.
	 */
	private boolean eof = false;
	
	/**
	 * <p>It builds a base64 decode input stream</p>
	 * 
	 * @param intputStream
	 *                   the underlying stream
	 */
	public Base64InputStream(InputStream intputStream){
		this.inputStream = intputStream;
	}
	
	
	@Override
	public int read() throws IOException {
		if (buffer == null || buffercounter == buffer.length) {
			if (eof) {
				return -1;
			}
		    acquire();
		    if (buffer.length == 0){
		    	buffer = null;
		        return -1;
		    }   
		    buffercounter = 0;
	    }
	    return buffer[buffercounter++]; 
	}
	
    /**
     * Reads data from the underlying stream and decodes the data,
     * puts the decoded data into the buffer.  
     * @throws IOException 
     */
	private void acquire() throws IOException {
		char[] four = new char[4];
		int i = 0;
		do{
			int b = inputStream.read();
			if (b == -1)
				if (i != 0)
					throw new IOException("Bad base64 stream!");
				else {
					buffer = new int[0];
					eof = true;
					return;
				}
			char c = (char)b;
			if (Shared.chars.indexOf(c) != -1 || c == Shared.pad)
				four[i++] = c;
			else if (c != '\r' && c != '\n')
					throw new IOException("Bad base64 stream!");
		}while(i < 4);
		
		boolean padded = false;
		
		for (i = 0; i < 4; i++) {
			if (four[i] != Shared.pad){
				if (padded){
					throw new IOException("Bad base64 stream!");
				}
			}
			else{
				if (!padded){
					padded = true;
				}
			}
		}
		int l;
		
		if (four[3] == Shared.pad) {
			if(inputStream.read() != -1)
				throw new IOException("Bad base64 stream!");
			eof = true;
			if(four[2] == Shared.pad){
				l = 1;
			}else{ 
				l = 2;
			}
		}else{
			l = 3;	
		}
		int aux = 0;
		for (i = 0; i < 4; i++) 
			if (four[i] != Shared.pad)
				aux = aux | (Shared.chars.indexOf(four[i]) << (6 * (3 - i)));
		buffer = new int[l];
		for (i = 0; i < l; i++){
			buffer[i] = (aux >>> (8 * (2 - i))) & 0xFF;
		}
	}
		
	
	public void close() throws IOException{
		inputStream.close();
	}
}
