package com.tangzhe.mamabike.wallet.dao;

import com.tangzhe.mamabike.wallet.entity.Wallet;

public interface WalletMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Wallet record);

    int insertSelective(Wallet record);

    Wallet selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Wallet record);

    int updateByPrimaryKey(Wallet record);

    Wallet selectByUserId(Long userId);
}