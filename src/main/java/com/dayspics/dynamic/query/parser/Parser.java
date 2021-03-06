package com.dayspics.dynamic.query.parser;

import java.util.Map;

import com.dayspics.dynamic.query.Characters;
import com.dayspics.dynamic.query.Token;
import com.dayspics.dynamic.query.exception.ParsingException;

/**
 * 
 * @author zhangcaijie
 *
 */
public class Parser {

    private char[] sqlChar;
    private Map<String, ?> map;
    private Token token;
    private CharacterFilter filter;
    
    public Parser(String sql, Map<String, ?> map) {
        //去掉sql结尾的空格，否则如果结尾为需要删除的可选参数，会出现删除错误问题
        sqlChar = sql.trim().toCharArray();
        this.map = map;
        token = new Token();
        filter = new CharacterFilter(token);
    }

    public void parse() throws ParsingException {
        filter.setMap(map);
        for(char c : sqlChar) {
            if (c != Characters.SPACE) {
                filter.setType(c);
                break;
            }
        }
        for (char c : sqlChar) {
            filter.process(c);
        }
        filter.close();
    }
    
    public Token getToken() {
        return this.token;
    }
    
}
