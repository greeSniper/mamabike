package com.tangzhe.mamabike.bike.entity;

public class Bike {
    private Long id;

    private Long number;

    private Byte type;

    private Byte enableFlag;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNumber() {
        return number;
    }

    public void setNumber(Long number) {
        this.number = number;
    }

    public Byte getType() {
        return type;
    }

    public void setType(Byte type) {
        this.type = type;
    }

    public Byte getEnableFlag() {
        return enableFlag;
    }

    public void setEnableFlag(Byte enableFlag) {
        this.enableFlag = enableFlag;
    }
}