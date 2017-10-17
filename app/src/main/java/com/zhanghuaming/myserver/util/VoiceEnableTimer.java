package com.zhanghuaming.myserver.util;

/**
 * Created by zhang on 2017/10/14.
 */

public class VoiceEnableTimer extends Thread{

    private Boolean enable = false;
    private int num = 10;
    private int num2 = 10;

    private OkBack okBack;

    public void setOkBack(OkBack okBack) {
        this.okBack = okBack;
    }

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public int getNum() {
        return this.num;
    }

    public void setNum(int num) {
        this.num = num;
        this.num2 = num;
    }

    @Override
    public void run() {
        while(enable){
            if(num>0){
                num--;
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }else{
                num=num2;
                okBack.back("finish");
                break;
            }
        }
    }

    public void Restart(){
        enable = true;
        num=num2;
        this.run();
    }

}
