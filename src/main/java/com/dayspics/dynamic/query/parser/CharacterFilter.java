package com.dayspics.dynamic.query.parser;

import java.util.Map;

import com.dayspics.dynamic.query.Characters;
import com.dayspics.dynamic.query.Token;
import com.dayspics.dynamic.query.exception.ParsingException;
import com.dayspics.dynamic.query.handler.ContextHandler;
import com.dayspics.dynamic.query.handler.OptionalFieldHandler;
import com.dayspics.dynamic.query.handler.RequiredFieldHandler;

/**
 * 
 * @author zhangcaijie
 *
 */
public class CharacterFilter {
    
    private ContextHandler contextHandler;
    private Token token;
    private char pre;
    //private char type;
    private int whereLength = 0;
    private int fromLength = 0;
    private boolean isSelect = false;
    private boolean isUpdate = false;
    private boolean isInsert = false;
    private int positon = 0;
    
    public CharacterFilter(Token token) {
        this.token = token;
        contextHandler = new ContextHandler(token);
    }

    public void process(char c) throws ParsingException {
        if (handleFrom(c)) {
            return;
        }
        if (handleWhere(c)) {
            return;
        }
        //update和select语句where条件之后的$处理
        if (c == Characters.DOLLAR) {
            if (pre != Characters.SPACE) {
                contextHandler.putChar(Characters.SPACE);
            }
            contextHandler.putChar(Characters.QUESTION);
            contextHandler.setCurrentHandler(contextHandler.getOptionalFieldHandler());
            pre = c;
            return;
        }
        //update和select语句where条件之后的?处理
        if (!isInsert && c == Characters.QUESTION) {
            if (pre != Characters.SPACE) {
                contextHandler.putChar(Characters.SPACE);
            }
            contextHandler.putChar(c);
            contextHandler.setCurrentHandler(contextHandler.getRequiredFieldHandler());
            pre = c;
            return;
        }
        //insert语句?号处理
        if (isInsert && c == Characters.QUESTION) {
            if (pre == Characters.COMMA) {
                contextHandler.putChar(Characters.SPACE);
            }
            contextHandler.putChar(c);
            contextHandler.setCurrentHandler(contextHandler.getInsertFieldHandler());
            pre = c;
            return;
        }
        //update语句@符号处理
        if (isUpdate && c == Characters.AT) {
            if (pre != Characters.SPACE) {
                contextHandler.putChar(Characters.SPACE);
            }
            contextHandler.putChar(Characters.QUESTION);
            contextHandler.setCurrentHandler(contextHandler.getUpdateFieldHandler());
            pre = c;
            return;
        }
        contextHandler.putChar(c);
        pre = c;
    }

    public boolean handleFrom(char c) throws ParsingException {
        if(pre == Characters.SPACE && positon == 0 && isSelect && Characters.equalsIgnoreCase(c, 'f') && fromLength == 0) {
            fromLength++;
            contextHandler.putChar(c);
            pre = c;
            return true;
        }
        switch (fromLength) {
            case 1 :
                if (Characters.equalsIgnoreCase(pre, 'f') && positon == 0 && isSelect && Characters.equalsIgnoreCase(c, 'r')) {
                    fromLength++;
                    contextHandler.putChar(c);
                    pre = c;
                    return true;
                } else {
                    fromLength = 0;
                }
            case 2 :
                if (Characters.equalsIgnoreCase(pre, 'r') && positon == 0 && isSelect && Characters.equalsIgnoreCase(c, 'o')) {
                    fromLength++;
                    contextHandler.putChar(c);
                    pre = c;
                    return true;
                } else {
                    fromLength = 0;
                }
            case 3 :
                if (Characters.equalsIgnoreCase(pre, 'o') && positon == 0 && isSelect && Characters.equalsIgnoreCase(c, 'm')) {
                    fromLength++;
                    contextHandler.putChar(c);
                    pre = c;
                    return true;
                } else {
                    fromLength = 0;
                }
            case 4 :
                if (Characters.equalsIgnoreCase(pre, 'm') && positon == 0 && isSelect && c == Characters.SPACE) {
                    fromLength = 0;
                    contextHandler.putCache();
                    positon = token.length();
                    contextHandler.putChar(c);
                    pre = c;
                    return true;
                } else {
                    fromLength = 0;
                }
        }
        return false;
    }
    
    public boolean handleWhere(char c) throws ParsingException {
        if (pre == Characters.SPACE && !isInsert && Characters.equalsIgnoreCase(c, 'w') && whereLength == 0) {
            whereLength++;
            contextHandler.putChar(c);
            pre = c;
            return true;
        }
        switch (whereLength) {
            case 1 :
                if (Characters.equalsIgnoreCase(pre, 'w') && !isInsert && Characters.equalsIgnoreCase(c, 'h')) {
                    whereLength++;
                    contextHandler.putChar(c);
                    pre = c;
                    return true;
                } else {
                    whereLength = 0;
                }
            case 2 :
                if (Characters.equalsIgnoreCase(pre, 'h') && !isInsert && Characters.equalsIgnoreCase(c, 'e')) {
                    whereLength++;
                    contextHandler.putChar(c);
                    pre = c;
                    return true;
                } else {
                    whereLength = 0;
                }
            case 3 :
                if (Characters.equalsIgnoreCase(pre, 'e') && !isInsert && Characters.equalsIgnoreCase(c, 'r')) {
                    whereLength++;
                    contextHandler.putChar(c);
                    pre = c;
                    return true;
                } else {
                    whereLength = 0;
                }
            case 4 :
                if (Characters.equalsIgnoreCase(pre, 'r') && !isInsert && Characters.equalsIgnoreCase(c, 'e')) {
                    whereLength++;
                    contextHandler.putChar(c);
                    pre = c;
                    return true;
                } else {
                    whereLength = 0;
                }
            case 5 :
                if (Characters.equalsIgnoreCase(pre, 'e') && !isInsert && c == Characters.SPACE) {
                    whereLength = 0;
                    contextHandler.putString(" 1 = 1 ");
                    contextHandler.putCache();
                    //有可能需要删除此'and'连接符，所以单独放入cache
                    contextHandler.putString("and ");
                    pre = c;
                    return true;
                } else {
                    whereLength = 0;
                }
        }
        return false;
    }
    
    
    public void setMap(Map<String, ?> map) {
        token.setMap(map);
    }

    public void setType(char c) {
        //type = c;
        isSelect = Characters.equalsIgnoreCase(c, 's');
        isUpdate = Characters.equalsIgnoreCase(c, 'u');
        isInsert = Characters.equalsIgnoreCase(c, 'i');
    }

    public void close() throws ParsingException {
        //如果sql语句以可选参数或必选参数结尾，则在结束时尚未检查字段值，需在此手动调用处理
        contextHandler.end();
        contextHandler.putCache();
        if(positon > 0) {
            token.setCountSql(positon);
        }
        paging();
    }

    private void paging() {
        Map<String, ?> map = token.getMap();
        Object limitObj = map.get("limit");
        Object startObj = map.get("start");
        int limit, start;
        if (limitObj instanceof Integer) {
            limit = (Integer) limitObj;
        } else if (limitObj instanceof String) {
            try {
                limit = Integer.parseInt((String) limitObj);
            } catch (NumberFormatException e) {
                return;
            }
        } else {
            return;
        }
        if (startObj instanceof Integer) {
            start = (Integer) startObj;
        } else if (startObj instanceof String) {
            try {
                start = Integer.parseInt((String) startObj);
            } catch (NumberFormatException e) {
                return;
            }
        } else {
            return;
        }
        if(limit > 0 && start >= 0) {
            token.paging(start, limit);
        }
    }
}
