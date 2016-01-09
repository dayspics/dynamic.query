package com.dayspics.dynamic.query.handler;

import com.dayspics.dynamic.query.Characters;
import com.dayspics.dynamic.query.Token;
import com.dayspics.dynamic.query.exception.ParsingException;

/**
 * 
 * @author zhangcaijie
 *
 */
public class TextHandler extends AbstractHandler {

    private Token token;
    private char pre;
    private ContextHandler contextHandler;
    private boolean isStart = false;
    
    public TextHandler(Token token, ContextHandler contextHandler) {
        super(token, contextHandler);
        this.token = token;
        this.contextHandler = contextHandler;
    }

    @Override
    public void putChar(char c) throws ParsingException {
        if(c == Characters.SPACE && !isStart) {
            super.putChar(c);
            token.putList();
            pre = c;
            return;
        }
        boolean isOperators = c == Characters.LT || c == Characters.GT || c == Characters.EQUAL || c == Characters.EXCLAMATION;
        //'<>' '!=' '<' '>' '<=' '>=' '='七个操作符前如果没有空格则添加空格，即：
        //'<' '>' '!' '='四个符号前如果不是字符或数字则添加空格
        if (isOperators && Characters.isValidCharOfName(pre)) {
            super.putChar(Characters.SPACE);
            token.putList();
        }
        //删除可选参数中已经删除的连接符 'and'或'or'
        if (contextHandler.isDeleConnector()) {
            if (c != Characters.SPACE) {
                isStart = true;
            } else {
                isStart = false;
                contextHandler.setDeleConnector(false);
            }
            pre = c;
            return;
        }
        super.putChar(c);
        pre = c;
    }

    @Override
    public void end() throws ParsingException {}

}
