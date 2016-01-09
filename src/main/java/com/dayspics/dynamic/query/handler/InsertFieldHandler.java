package com.dayspics.dynamic.query.handler;

import com.dayspics.dynamic.query.Characters;
import com.dayspics.dynamic.query.Token;
import com.dayspics.dynamic.query.exception.ParsingException;

/**
 * 
 * @author zhangcaijie
 *
 */
public class InsertFieldHandler extends AbstractHandler {

    private StringBuilder builder = new StringBuilder();
    private Token token;
    public InsertFieldHandler(Token token, ContextHandler contextHandler) {
        super(token, contextHandler);
        this.token = token;
    }

    @Override
    public void putChar(char c) throws ParsingException {
        if (c != Characters.SPACE && c != Characters.COMMA && c != Characters.CLOSE_PARENTHESIS) {
            builder.append(c);
        } else {
            if (builder.length() == 0) {
                throw new ParsingException("the '?' is single of the insert statement, there should be a alias after '?'");
            }
            String fieldTemp = Characters.convertColumnName(builder.toString());
            token.addConditionParameter(fieldTemp);
            builder.setLength(0);
            super.putChar(c);
            super.exit();
        }
    }

    @Override
    public void end() throws ParsingException {}
}
