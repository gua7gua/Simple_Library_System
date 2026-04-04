package edu.cupk.simple_library_system.common;

import java.util.List;

public class PageResponse<T> {
    private int code;
    private String message;
    private long count;
    private List<T> data;

    public PageResponse() {
    }

    public PageResponse(int code, String message, long count, List<T> data) {
        this.code = code;
        this.message = message;
        this.count = count;
        this.data = data;
    }

    public static <T> PageResponse<T> success(long count, List<T> data) {
        return new PageResponse<>(0, "success", count, data);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }
}
