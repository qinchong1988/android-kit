package com.bmbstack.kit.app.account;

import com.bmbstack.kit.api.JWTHelper;
import com.bmbstack.kit.app.dao.DBManager;
import com.bmbstack.kit.app.dao.User;
import com.bmbstack.kit.app.storage.CommonTraySp;

public class AccountMgr {

    private AccountMgr() {
    }

    private static class SingletonInstance {
        private static final AccountMgr INSTANCE = new AccountMgr();
    }

    public static AccountMgr getInstance() {
        return SingletonInstance.INSTANCE;
    }

    public boolean isLogin() {
        return DBManager.INST.getUser() != null;
    }

    public User getUser() {
        return DBManager.INST.getUser();
    }

    public void saveUser(User user) {
        DBManager.INST.saveUser(user);
    }

    public void logout() {
        CommonTraySp.removeThirdLoginInfo();
        DBManager.INST.clearUser();
    }

    public String getJWT() {
        User user = DBManager.INST.getUser();
        if (user == null) {
            return null;
        }
        return user.getToken();
    }

    public boolean isJWTExpired() {
        String jwt = getJWT();
        return jwt == null || JWTHelper.isJWTExpired(jwt);
    }
}