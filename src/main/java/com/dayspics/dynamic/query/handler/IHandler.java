package com.dayspics.dynamic.query.handler;

import com.dayspics.dynamic.query.exception.ParsingException;

/**
 * @author zhangcaijie
 *
 */
public interface IHandler {
    
    public void putCache() throws ParsingException;
    
    public void putChar(char c) throws ParsingException;
    
    public void exit() throws ParsingException;
    
    public void end() throws ParsingException;
}
