package com.frostedmc.core.api.account.punish;

import com.frostedmc.core.Core;
import com.frostedmc.core.api.account.Timestamp;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Redraskal_2 on 1/14/2017.
 */
public class PunishmentManager {

    private String PUNISH_QUERY = "SELECT * FROM `punishments` WHERE uuid=?";
    private String PUNISH_INSERT = "INSERT INTO `punishments` (uuid, punishmentType, punishmentSeverity, issued, end, punisher, reason) VALUES (?, ?, ?, ?, ?, ?, ?)";
    private String PUNISH_CREATE = "CREATE TABLE IF NOT EXISTS `punishments` (uuid VARCHAR(255) NOT NULL, punishmentType INT NOT NULL, punishmentSeverity INT NOT NULL, issued VARCHAR(255) NOT NULL, end VARCHAR(255) NOT NULL, punisher VARCHAR(255) NOT NULL, reason VARCHAR(255) NOT NULL)";

    public PunishmentManager() { this.create(); }

    public PunishmentRecord[] parsePunishmentRecords(UUID uuid) {
        try {
            ResultSet resultSet = this.queryPunishmentRecords(uuid);
            List<PunishmentRecord> temp = new ArrayList<PunishmentRecord>();
            while(resultSet.next()) {
                temp.add(new PunishmentRecord(
                        UUID.fromString(resultSet.getString("uuid")),
                        PunishmentType.byID(resultSet.getInt("punishmentType")),
                        PunishmentSeverity.byID(resultSet.getInt("punishmentSeverity")),
                        new Timestamp(Timestamp.parse(resultSet.getString("issued"))),
                        new Timestamp(Timestamp.parse(resultSet.getString("end"))),
                        UUID.fromString(resultSet.getString("punisher")),
                        resultSet.getString("reason")
                ));
            }
            return temp.toArray(new PunishmentRecord[temp.size()]);
        } catch (Exception e) {
            return new PunishmentRecord[]{};
        }
    }

    public PunishmentRecord[] parsePunishmentRecords(UUID uuid, PunishmentType punishmentType) {
        PunishmentRecord[] punishmentRecords = this.parsePunishmentRecords(uuid);
        List<PunishmentRecord> temp = new ArrayList<PunishmentRecord>();
        for(PunishmentRecord punishmentRecord : punishmentRecords) {
            if(punishmentRecord.getPunishmentType() == punishmentType) {
                temp.add(punishmentRecord);
            }
        }
        return temp.toArray(new PunishmentRecord[temp.size()]);
    }

    public PunishmentRecord[] parsePunishmentRecords(UUID uuid, PunishmentSeverity punishmentSeverity) {
        PunishmentRecord[] punishmentRecords = this.parsePunishmentRecords(uuid);
        List<PunishmentRecord> temp = new ArrayList<PunishmentRecord>();
        for(PunishmentRecord punishmentRecord : punishmentRecords) {
            if(punishmentRecord.getPunishmentSeverity() == punishmentSeverity) {
                temp.add(punishmentRecord);
            }
        }
        return temp.toArray(new PunishmentRecord[temp.size()]);
    }

    public PunishmentRecord[] parsePunishmentRecords(UUID uuid, PunishmentType punishmentType,
                                                     PunishmentSeverity punishmentSeverity) {
        PunishmentRecord[] punishmentRecords = this.parsePunishmentRecords(uuid);
        List<PunishmentRecord> temp = new ArrayList<PunishmentRecord>();
        for(PunishmentRecord punishmentRecord : punishmentRecords) {
            if(punishmentRecord.getPunishmentType() == punishmentType
                    && punishmentRecord.getPunishmentSeverity() == punishmentSeverity) {
                temp.add(punishmentRecord);
            }
        }
        return temp.toArray(new PunishmentRecord[temp.size()]);
    }

    public String hasOngoingBan(UUID uuid) {
        PunishmentRecord[] punishmentRecords = this.parsePunishmentRecords(uuid, PunishmentType.TEMP_BAN);
        PunishmentRecord[] punishmentRecords2 = this.parsePunishmentRecords(uuid, PunishmentType.PERM_BAN);
        if(punishmentRecords.length == 0 && punishmentRecords2.length == 0) {
            return "";
        } else {
            String tempBan = "";
            String permBan = "";
            for(PunishmentRecord punishmentRecord : punishmentRecords) {
                if(!punishmentRecord.ended())
                    tempBan = "&c&lBan Severity "
                            + punishmentRecord.getPunishmentSeverity().getID()
                            + "\n&7You are banned until &a" + punishmentRecord.getEnd().toString() + " CST"
                            + "&7.\n&7You were banned by &a"
                            + Core.getInstance().getAccountManager()
                            .parseDetails(punishmentRecord.getPunisher()).getUsername()
                            + "&7.\n&7Reason: &e" + punishmentRecord.getReason()
                            + "\n\n&fPlease appeal on our forums at: &ahttps://frostedmc.com";
            }
            for(PunishmentRecord punishmentRecord : punishmentRecords2) {
                permBan = "&c&lBan Severity "
                        + punishmentRecord.getPunishmentSeverity().getID()
                        + "\n&7You are banned &aforever&7.\n&7You were banned by &a"
                        + Core.getInstance().getAccountManager()
                            .parseDetails(punishmentRecord.getPunisher()).getUsername()
                        + "&7.\n&7Reason: &e" + punishmentRecord.getReason()
                        + "\n\n&fPlease appeal on our forums at: &ahttps://frostedmc.com";
            }
            if(!tempBan.isEmpty()) {
                return tempBan;
            } else {
                return permBan;
            }
        }
    }

    public boolean hasOngoingMute(UUID uuid) {
        PunishmentRecord[] punishmentRecords = this.parsePunishmentRecords(uuid, PunishmentType.TEMP_MUTE);
        PunishmentRecord[] punishmentRecords2 = this.parsePunishmentRecords(uuid, PunishmentType.PERM_MUTE);
        if(punishmentRecords.length == 0 && punishmentRecords2.length == 0) {
            return false;
        } else {
            boolean tempMute = false;
            boolean permMute = false;
            for(PunishmentRecord punishmentRecord : punishmentRecords) {
                if(!punishmentRecord.ended())
                    tempMute = true;
            }
            for(PunishmentRecord punishmentRecord : punishmentRecords2) {
                if(!punishmentRecord.ended())
                    permMute = true;
            }
            return (tempMute == true || permMute == true);
        }
    }

    public ResultSet queryPunishmentRecords(UUID uuid) {
        PreparedStatement statement = this.prepare(this.PUNISH_QUERY);

        try {
            statement.setString(1, uuid.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            ResultSet result = statement.executeQuery();
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean hasPunishments(UUID uuid) {
        try {
            ResultSet result = queryPunishmentRecords(uuid);

            if(result.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean register(PunishmentRecord punishmentRecord) {
        PreparedStatement statement = this.prepare(this.PUNISH_INSERT);

        try {
            statement.setString(1, punishmentRecord.getUUID().toString());
            statement.setInt(2, punishmentRecord.getPunishmentType().getID());
            statement.setInt(3, punishmentRecord.getPunishmentSeverity().getID());
            statement.setString(4, punishmentRecord.getIssued().toString());
            statement.setString(5, punishmentRecord.getEnd().toString());
            statement.setString(6, punishmentRecord.getPunisher().toString());
            statement.setString(7, punishmentRecord.getReason());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    private PreparedStatement prepare(String statement) {
        try {
            return Core.getInstance().getSQLConnection().getConnection().prepareStatement(statement);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void create() {
        try {
            Statement statement = Core.getInstance().getSQLConnection().getConnection().createStatement();
            statement.executeUpdate(this.PUNISH_CREATE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}