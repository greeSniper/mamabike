package com.tangzhe.mamabike.wallet.entity;

import java.math.BigDecimal;

public class Wallet {
    private Long id;

    private Long userid;

    private BigDecimal remainSum;

    private BigDecimal deposit;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserid() {
        return userid;
    }

    public void setUserid(Long userid) {
        this.userid = userid;
    }

    public BigDecimal getRemainSum() {
        return remainSum;
    }

    public void setRemainSum(BigDecimal remainSum) {
        this.remainSum = remainSum;
    }

    public BigDecimal getDeposit() {
        return deposit;
    }

    public void setDeposit(BigDecimal deposit) {
        this.deposit = deposit;
    }
}