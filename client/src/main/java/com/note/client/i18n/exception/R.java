package com.note.client.i18n.exception;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 返回数据
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class R<T> implements Serializable {

    private String code;
    private String message;
    private T data;

    public static <T> R<T> of(String code, String message) {
        return new R<T>().setCode(code).setMessage(message);
    }

    public static <T> R<T> of(String code, String message, T data) {
        return new R<T>().setCode(code).setMessage(message).setData(data);
    }
}