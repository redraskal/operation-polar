package com.frostedmc.core.api.jukebox;

import com.frostedmc.core.Core;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by Redraskal_2 on 9/28/2016.
 */
public class JukeboxManager {

    private static JukeboxManager instance;
    public static String DEFAULT_KEY = "11e202ff-0fb4-4236-a53b-6dce7bd9a0fb";
    private String API_KEY;

    public static JukeboxManager getInstance() {
        if(instance == null)
            instance = new JukeboxManager(DEFAULT_KEY);
        return instance;
    }

    public static boolean initialize(String api_key) {
        if(instance == null) {
            instance = new JukeboxManager(api_key);
            return true;
        } else {
            return false;
        }
    }

    private String REQUEST_IS_CONNECTED = "http://music.frostedmc.com/backend/isconnected/?api_key=%api_key%&request={%request%}";
    private String REQUEST_UPDATE = "http://music.frostedmc.com/backend/update/?api_key=%api_key%&request={%request%}";

    public JukeboxManager() {
        this.API_KEY = "" + DEFAULT_KEY;
    }

    public JukeboxManager(String api_key) {
        this.API_KEY = api_key;
    }

    public String getAPIKey() {
        return this.API_KEY;
    }

    public void setAPIKey(String api_key) {
        this.API_KEY = api_key;
    }

    public int isConnected(String username) {
        JsonObject request = new JsonObject();
        request.addProperty("username", username);

        try {
            String response = this.makeRequest(this.REQUEST_IS_CONNECTED.replace("%api_key%", this.API_KEY)
                    .replace("%request%", request.toString()));
            JsonObject json = new JsonParser().parse(response).getAsJsonObject();

            return json.get("code").getAsInt();
        } catch (Exception e) {
            Core.getInstance().getLogger().severe("[Jukebox] Error while contacting Jukebox API.");
            return 500;
        }
    }

    public int update(String username, SoundType soundType, String url) {
        JsonObject request = new JsonObject();
        request.addProperty("username", username);
        request.addProperty("type", soundType.getValue());
        request.addProperty("url", url);

        try {
            String response = this.makeRequest(this.REQUEST_UPDATE.replace("%api_key%", this.API_KEY)
                    .replace("%request%", request.toString()));
            JsonObject json = new JsonParser().parse(response).getAsJsonObject();

            return json.get("code").getAsInt();
        } catch (Exception e) {
            Core.getInstance().getLogger().severe("[Jukebox] Error while contacting Jukebox API.");
            return 500;
        }
    }

    public int stopMusic(String username) {
        JsonObject request = new JsonObject();
        request.addProperty("username", username);
        request.addProperty("type", SoundType.MUSIC.getValue());
        request.addProperty("url", "https://downloads.frostedmc.com/music/blank.mp3");

        try {
            String response = this.makeRequest(this.REQUEST_UPDATE.replace("%api_key%", this.API_KEY)
                    .replace("%request%", request.toString()));
            JsonObject json = new JsonParser().parse(response).getAsJsonObject();

            return json.get("code").getAsInt();
        } catch (Exception e) {
            Core.getInstance().getLogger().severe("[Jukebox] Error while contacting Jukebox API.");
            return 500;
        }
    }

    private String makeRequest(String url) throws Exception {
        String json = url.split("&request=")[1];
        json = json.replace("{", "");
        json = json.replace("}", "");

        json = URLEncoder.encode(json, "UTF-8");

        json = "{" + json + "}";
        url = url.split("&request=")[0] + "&request=" + json;

        //Core.getInstance().getLogger().info("[Jukebox] Sending request to " + url + "...");
        URL server = new URL(url);
        HttpURLConnection con = (HttpURLConnection) server.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", "FROSTED_CORE");

        int responseCode = con.getResponseCode();
        //Core.getInstance().getLogger().info("[Jukebox] Response code: " + responseCode);

        BufferedReader input = new BufferedReader(
                new InputStreamReader(con.getInputStream())
        );
        String buffer;
        StringBuffer response = new StringBuffer();

        while((buffer = input.readLine()) != null) {
            response.append(buffer);
        }

        input.close();

        String buffered = response.toString();
        // Core.getInstance().getLogger().info("[Jukebox] Response: " + buffered);
        return buffered;
    }
}