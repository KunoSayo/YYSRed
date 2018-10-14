package com.github.euonmyoji.yysred.data;

/**
 * @author yinyangshi
 */
public enum Type {
    /**
     * 普通红包
     */
    NORMAL,
    /**
     * 拼手气红包
     */
    LUCK,
    /**
     * 口令红包
     */
    PASSWORD,
    /**
     * 问答红包
     */
    QUESTION;

    public String getDisplayName() {
        //todo: get display name from lang.
        return null;
    }
}