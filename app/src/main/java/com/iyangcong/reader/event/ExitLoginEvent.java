package com.iyangcong.reader.event;

public class ExitLoginEvent {
   private String msg;

    public ExitLoginEvent(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }
}
