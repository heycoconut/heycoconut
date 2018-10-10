package org.noixdecoco.app.service;

public interface UserService {

    boolean isAdmin(String userId);

    boolean isBot(String userId);
}
