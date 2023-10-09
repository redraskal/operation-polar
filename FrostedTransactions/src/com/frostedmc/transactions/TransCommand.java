package com.frostedmc.transactions;

import com.frostedmc.core.Core;
import com.frostedmc.core.api.account.UpdateDetails;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Redraskal_2 on 1/15/2017.
 */
public class TransCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(commandSender instanceof Player)
            return false;
        List<String> temp = new ArrayList<String>();
        boolean email = false;
        for(int i=0; i< strings.length; i++) {
            if(strings[i].contains("@")) {
                email = true;
            }
        }
        for(int i=0; i<9; i++) {
            temp.add(strings[i]);
        }
        String last = "";
        for(int i=9; i<strings.length; i++) {
            if(last.isEmpty()) {
                last = strings[i];
            } else {
                last+=" " + strings[i];
            }
        }
        temp.add(last);
        if(!email) {
            temp.add(5, "test@example.com");
            temp.remove(6);
        }
        JsonObject jsonObject = this.format(temp.toArray(new String[temp.size()]));
        String json = new Gson().toJson(jsonObject);
        Core.getInstance().getPurchaseManager().createTransaction(
                UUID.fromString(jsonObject.get("uuid").getAsString()),
                jsonObject.get("transaction").getAsString(), jsonObject.get("packageName").getAsString());
        if(!Core.getInstance().getAccountManager()
                .isRegistered(UUID.fromString(jsonObject.get("uuid").getAsString()))) {
            Core.getInstance().getAccountManager()
                    .register(UUID.fromString(jsonObject.get("uuid").getAsString()),
                            jsonObject.get("name").getAsString(), jsonObject.get("ip").getAsString());
        }
        Core.getInstance().getAccountManager().update(UUID.fromString(jsonObject.get("uuid").getAsString()),
                new UpdateDetails(UpdateDetails.UpdateType.RANK,
                        this.parseRank(jsonObject.get("packageName").getAsString())));
        Main.getInstance().publish("transactions-purchase", json);
        return false;
    }

    private int parseRank(String packageName) {
        if(packageName.equalsIgnoreCase("VIP"))
            return 1;
        if(packageName.equalsIgnoreCase("ELITE"))
            return 2;
        if(packageName.equalsIgnoreCase("LEGEND"))
            return 3;
        return 1;
    }

    private JsonObject format(String[] strings) {
        JsonObject jsonObject = new JsonObject();
        for(int i=0; i<strings.length; i++) {
            String name = "";
            switch(i) {
                case 0: {
                    name = "name";
                    break;
                } case 1: {
                    name = "uuid";
                    break;
                } case 2: {
                    name = "transaction";
                    break;
                } case 3: {
                    name = "price";
                    break;
                } case 4: {
                    name = "currency";
                    break;
                } case 5: {
                    name = "email";
                    break;
                } case 6: {
                    name = "ip";
                    break;
                } case 7: {
                    name = "packageId";
                    break;
                } case 8: {
                    name = "packagePrice";
                    break;
                } case 9: {
                    name = "packageName";
                    break;
                }
            }
            jsonObject.addProperty(name, strings[i]);
        }
        return jsonObject;
    }
}