package com.frostedmc.backbone.api.misc;

/**
 * Created by Redraskal_2 on 11/6/2016.
 */
public class TokenManager {

    public static final String TOKEN = "597Pm7pFa7xF0FMi2x8UgJ9C52KHWTTi";
    public static final String ACCOUNT_LINK_URI = "https://fa566452.ngrok.io/api/link/%username%/" + TOKEN;

    private static TokenManager instance = null;

    public static TokenManager getInstance() {
        if(instance == null) {
            instance = new TokenManager();
        }
        return instance;
    }

    private TokenManager() {}

    public boolean linkAccount(String username) {
        return true;
    }
}