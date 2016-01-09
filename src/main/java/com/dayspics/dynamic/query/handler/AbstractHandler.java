package com.dayspics.dynamic.query.handler;

import com.dayspics.dynamic.query.Token;
import com.dayspics.dynamic.query.exception.ParsingException;

/**
 * 
 * @author zhangcaijie
 *
 */
public abstract class AbstractHandler implements IHandler {

    private Token token;
    private ContextHandler contextHandler;
    
    public AbstractHandler(Token token, ContextHandler contextHandler) {
        this.token = token;
        this.contextHandler = contextHandler;
    }
    
    public Token getToken() {
        return this.token;
    }
    
    @Override
    public void putCache() throws ParsingException {
        token.putCache();
    }

    @Override
    public void putChar(char c) throws ParsingException {
        token.putChar(c);
    }

    @Override
    public void exit() throws ParsingException {
        putCache();
        contextHandler.setCurrentHandler(contextHandler.getTextHandler());
    }

}
