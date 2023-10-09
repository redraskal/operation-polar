package com.frostedmc.core.api.account.report;

import com.frostedmc.core.Core;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

/**
 * Created by Redraskal_2 on 3/2/2017.
 */
public class ReportManager {

    private String REPORT_QUERY = "SELECT * FROM `player_reports` WHERE uuid=?";
    private String REPORT_INSERT = "INSERT INTO `player_reports` (uuid, reporter, reasons) VALUES (?, ?, ?)";
    private String REPORT_CREATE = "CREATE TABLE IF NOT EXISTS `player_reports` (uuid VARCHAR(255) NOT NULL, reporter VARCHAR(255) NOT NULL, reasons VARCHAR(6000) NOT NULL)";

    public ReportManager() { this.create(); }

    public ResultSet queryAllReports() {
        PreparedStatement statement = this.prepare("SELECT * FROM `player_reports`");

        try {
            ResultSet result = statement.executeQuery();
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public ResultSet queryReport(UUID uuid) {
        PreparedStatement statement = this.prepare(this.REPORT_QUERY);

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

    public ResultSet queryReportBackwards(UUID reporter) {
        PreparedStatement statement = this.prepare("SELECT * FROM `player_reports` WHERE reporter=?");

        try {
            statement.setString(1, reporter.toString());
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

    public boolean contains(UUID uuid) {
        try {
            ResultSet result = queryReport(uuid);
            if(result.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean register(ReportDetails reportDetails) {
        if(contains(reportDetails.getPlayer())) return false;
        PreparedStatement statement = this.prepare(this.REPORT_INSERT);

        try {
            statement.setString(1, reportDetails.getPlayer().toString());
            statement.setString(2, reportDetails.getReporter().toString());
            statement.setString(3, convert(reportDetails));
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        try {
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public ReportDetails[] parseDetails() {
        try {
            ResultSet result = queryAllReports();
            List<ReportDetails> temp = new ArrayList<ReportDetails>();

            while(result.next()) {
                temp.add(new ReportDetails(UUID.fromString(result.getString("uuid")),
                        UUID.fromString(result.getString("reporter")),
                        backwardsConvert(result.getString("reasons"))));
            }

            return temp.toArray(new ReportDetails[temp.size()]);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ReportDetails[]{};
    }

    public ReportDetails parseDetails(UUID uuid, UUID reporter) {
        ReportDetails[] reports = this.parseDetails(uuid);
        if(reports == null) return null;
        for(ReportDetails reportDetails : reports) {
            if(reportDetails.getReporter().toString().equalsIgnoreCase(reporter.toString())) return reportDetails;
        }
        return null;
    }

    public ReportDetails[] parseDetails(UUID uuid) {
        try {
            ResultSet result = queryReport(uuid);
            List<ReportDetails> temp = new ArrayList<ReportDetails>();

            while(result.next()) {
                temp.add(new ReportDetails(UUID.fromString(result.getString("uuid")),
                        UUID.fromString(result.getString("reporter")),
                        backwardsConvert(result.getString("reasons"))));
            }

            return temp.toArray(new ReportDetails[temp.size()]);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public ReportDetails[] parseDetailsBackwards(UUID reporter) {
        try {
            ResultSet result = queryReportBackwards(reporter);
            List<ReportDetails> temp = new ArrayList<ReportDetails>();

            while(result.next()) {
                temp.add(new ReportDetails(UUID.fromString(result.getString("uuid")),
                        UUID.fromString(result.getString("reporter")),
                        backwardsConvert(result.getString("reasons"))));
            }

            return temp.toArray(new ReportDetails[temp.size()]);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void deleteReport(UUID uuid, UUID reporter) {
        PreparedStatement statement = this.prepare("DELETE FROM `player_reports` WHERE uuid=? AND reporter=?");

        try {
            statement.setString(1, uuid.toString());
            statement.setString(2, reporter.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Map<ReportCategory, String> backwardsConvert(String reportDetails) {
        Map<ReportCategory, String> temp = new HashMap<ReportCategory, String>();
        for(String section : reportDetails.split(":")) {
            String[] array = section.split("#");
            temp.put(ReportCategory.byId(Integer.parseInt(array[0])), array[1]);
        }
        return temp;
    }

    public String convert(ReportDetails reportDetails) {
        String temp = "";
        for(Map.Entry<ReportCategory, String> entry : reportDetails.getReasons().entrySet()) {
            if(!temp.isEmpty()) temp+=":";
            temp+=entry.getKey().getId()+"#"+entry.getValue();
        }
        return temp;
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
            statement.executeUpdate(this.REPORT_CREATE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}