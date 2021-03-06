package com.dayspics.dynamic.query.handler;

import java.util.List;

import com.dayspics.dynamic.query.Characters;
import com.dayspics.dynamic.query.Token;
import com.dayspics.dynamic.query.exception.ParsingException;

/**
 * 
 * @author zhangcaijie
 *
 */
public class OptionalFieldHandler extends AbstractHandler {

    private StringBuilder builder = new StringBuilder();
    private Token token;
    private ContextHandler contextHandler;
    
    public OptionalFieldHandler(Token token, ContextHandler contextHandler) {
        super(token, contextHandler);
        this.token = token;
        this.contextHandler = contextHandler;
    }

    @Override
    public void putChar(char c) throws ParsingException {
        if (c != Characters.SPACE && c != Characters.CLOSE_PARENTHESIS) {
            builder.append(c);
        } else {
            getField(false);
            super.putChar(c);
            super.exit();
        }
    }

    @Override
    public void end() throws ParsingException {
        getField(true);
        super.exit();
    }
    
    private void getField(boolean isEnd)  throws ParsingException {
        List<StringBuilder> list = token.getCacheList();
        int size = list.size();
        if (builder.length() == 0) {
            if(size < 2) {
                throw new ParsingException("parse error");
            }
            builder.append(list.get(size - 2));
        }
        String fieldTemp = Characters.convertColumnName(builder.toString());
        if (token.getMap().containsKey(fieldTemp)) {
            int end = 3;
            //如果要删除的可选参数在结尾，则需要删除可选参数前面的连接符，否则删除后面的连接符
            if(isEnd) {
                end = 4;
            }
            for(int i = 1; i < end; i++) {
                list.remove(size - i);
            }
            token.clear();
            contextHandler.setDeleConnector(true);
        } else {
            token.addConditionParameter(fieldTemp);
        }
        builder.setLength(0);
    }
}
