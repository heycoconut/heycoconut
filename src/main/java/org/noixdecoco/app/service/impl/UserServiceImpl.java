package org.noixdecoco.app.service.impl;

import org.noixdecoco.app.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Value("${admin.users}")
    private String[] adminList;

    @Override
    public boolean isAdmin(String userId) {
        for (String admin : adminList) {
            if (userId.equals(admin)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isBot(String userId) {
        return false;
    }
}
