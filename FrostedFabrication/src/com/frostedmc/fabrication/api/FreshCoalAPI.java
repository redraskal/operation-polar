package com.frostedmc.fabrication.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

/**
 * Created by Redraskal_2 on 11/1/2016.
 */
public class FreshCoalAPI {

    private static FreshCoalAPI freshCoalAPI;
    private Map<String, SkullResult[]> skullCache = new HashMap<String, SkullResult[]>();

    public static FreshCoalAPI getInstance() {
        if(freshCoalAPI == null) {
            freshCoalAPI = new FreshCoalAPI();
        }
        return freshCoalAPI;
    }

    public SkullResult[] query(String find) throws Exception {
        find = find.toLowerCase();
        if(skullCache.containsKey(find)) { return skullCache.get(find); }

        SkullResult[] userCollection = this.queryCollection(find, false);
        SkullResult[] mainCollection = this.queryCollection(find, true);
        List<SkullResult> totalCollection = new ArrayList<SkullResult>();

        for(SkullResult skullResult : userCollection) {
            totalCollection.add(skullResult);
        }

        for(SkullResult skullResult : mainCollection) {
            totalCollection.add(skullResult);
        }

        SkullResult[] array = totalCollection.toArray(new SkullResult[totalCollection.size()]);
        skullCache.put(find, array);
        return array;
    }

    public SkullResult[] queryCollection(String find, boolean main) throws Exception {
        String add = ""; if(main) { add = "main"; }
        URL url = new URL("http://heads.freshcoal.com/" + add + "api.php?query=" + find);
        String statement = this.statement(url);

        int location = 0;
        boolean found = false;
        for(int i=0; i<statement.length(); i++) {
            if(statement.charAt(i) == '[' && !found) {
                location = i;
                found = true;
            }
        }

        statement = statement.substring(location, statement.length());
        statement = statement.replace("</body></html>", "");
        //Core.getInstance().getLogger().info("[Debug] " + statement);

        if(!statement.contains("{")) { return new SkullResult[]{}; }

        JsonArray jsonArray = new JsonParser().parse(statement).getAsJsonArray();
        List<SkullResult> resultsList = new ArrayList<SkullResult>();

        for(int i=0; i<jsonArray.size(); i++) {
            JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();
            resultsList.add(new SkullResult(jsonObject.get("name").getAsString(),
                    UUID.fromString(jsonObject.get("skullowner").getAsString()),
                    jsonObject.get("value").getAsString()));
        }

        return resultsList.toArray(new SkullResult[resultsList.size()]);
    }

    private String statement(URL url) throws Exception {
        URLConnection connection = url.openConnection();
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String temp;
        String output = "";

        while((temp = reader.readLine()) != null) {
            output = output + temp;
        }

        reader.close();
        return output;
    }
}