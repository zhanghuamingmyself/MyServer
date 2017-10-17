package com.zhanghuaming.myplugin;

/**
 * Created by zhang on 2017/8/14.
 */

public class MessBeen {
    private String pkg;
    private String open;
    private String mess;

    public String getPkg() {
        return pkg;
    }

    public void setPkg(String pkg) {
        this.pkg = pkg;
    }

    public String getOpen() {
        return open;
    }

    public void setOpen(String open) {
        this.open = open;
    }

    public String getMess() {
        return mess;
    }

    public void setMess(String mess) {
        this.mess = mess;
    }

    @Override
    public String toString() {
        return "MessBeen{" +
                "pkg='" + pkg + '\'' +
                ", open='" + open + '\'' +
                ", mess='" + mess + '\'' +
                '}';
    }
}
