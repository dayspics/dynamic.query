package com.dayspics.dynamic.query.exception;

/**
 * 
 * @author zhangcaijie
 * 
 */
public class UnsupportedCharacterException extends ParsingException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UnsupportedCharacterException(char c) {
		super("Unsupported character :" + c);
	}

	public UnsupportedCharacterException(Object where, char c) {
		super(where + ", Unsupported character :" + c);
	}

}
