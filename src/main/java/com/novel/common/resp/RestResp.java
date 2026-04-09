package com.novel.common.resp;

import lombok.Data;

@Data
public class RestResp<T> {
    private int code;
    private String msg;
    private T data;

    public static <T> RestResp<T> ok(T data) {
        RestResp<T> resp = new RestResp<>();
        resp.setCode(200);
        resp.setMsg("success");
        resp.setData(data);
        return resp;
    }

    public static <T> RestResp<T> ok() {
        return ok(null);
    }

    public static <T> RestResp<T> error(String msg) {
        RestResp<T> resp = new RestResp<>();
        resp.setCode(500);
        resp.setMsg(msg);
        return resp;
    }

    public static <T> RestResp<T> error(int code, String msg) {
        RestResp<T> resp = new RestResp<>();
        resp.setCode(code);
        resp.setMsg(msg);
        return resp;
    }
}