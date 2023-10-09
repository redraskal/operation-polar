package com.frostedmc.core;

import com.frostedmc.core.api.BugReportManager;
import com.frostedmc.core.api.account.AccountManager;
import com.frostedmc.core.api.account.PurchaseManager;
import com.frostedmc.core.api.account.TitleManager;
import com.frostedmc.core.api.account.achievement.AchievementManager;
import com.frostedmc.core.api.account.punish.GlacierPunishmentManager;
import com.frostedmc.core.api.account.punish.PunishmentManager;
import com.frostedmc.core.api.account.report.ReportManager;
import com.frostedmc.core.api.account.statistics.StatisticsManager;
import com.frostedmc.core.api.misc.GameProfileFetcher;
import com.frostedmc.core.api.misc.UUIDFetcher;
import com.frostedmc.core.module.Module;
import com.frostedmc.core.sql.SQLConnection;
import com.frostedmc.core.sql.SQLDetails;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by Redraskal_2 on 8/24/2016.
 */
public class Core {

    private static Core instance = null;

    public static Core getInstance() {
        return instance;
    }

    public static boolean initialize(SQLDetails sqlDetails, Logger logger, Module[] enabledModules) {
        if(instance == null) {
            instance = new Core(sqlDetails, logger, enabledModules);
            return true;
        } else {
            return false;
        }
    }

    private SQLConnection connection;
    private Logger logger;

    private AccountManager accountManager;
    private PurchaseManager purchaseManager;
    private BugReportManager bugReportManager;
    private PunishmentManager punishmentManager;
    private GlacierPunishmentManager glacierPunishmentManager;
    private TitleManager titleManager;
    private StatisticsManager statisticsManager;
    private AchievementManager achievementManager;
    private UUIDFetcher uuidFetcher;
    private GameProfileFetcher gameProfileFetcher;
    private ReportManager playerReportManager;

    private List<Module> modules;

    public Core(SQLDetails details, Logger logger, Module[] enabledModules) {
        instance = this;
        this.connection = new SQLConnection(details);
        this.logger = logger;

        logger.info("[Core] Opening SQL Connection...");

        if(connection.openConnection() && connection.getConnection() != null) {
            logger.info("[Core] Connection established with the SQL server.");

            logger.info("[Manager] Enabling Account Manager...");
            this.accountManager = new AccountManager();

            logger.info("[Manager] Enabling Purchase Manager...");
            this.purchaseManager = new PurchaseManager();

            logger.info("[Manager] Enabling Bug Report Manager...");
            this.bugReportManager = new BugReportManager();

            logger.info("[Manager] Enabling Punishment Manager...");
            this.punishmentManager = new PunishmentManager();

            logger.info("[Manager] Enabling Glacier Punishment Manager...");
            this.glacierPunishmentManager = new GlacierPunishmentManager();

            logger.info("[Manager] Enabling Title Manager...");
            this.titleManager = new TitleManager();

            logger.info("[Manager] Enabling Statistic Manager...");
            this.statisticsManager = new StatisticsManager();

            logger.info("[Manager] Enabling Achievement Manager...");
            this.achievementManager = new AchievementManager();

            logger.info("[Manager] Enabling Player Report Manager...");
            this.playerReportManager = new ReportManager();

            logger.info("[Manager] Enabling UUIDFetcher...");
            this.uuidFetcher = new UUIDFetcher();

            logger.info("[Manager] Enabling GameProfileFetcher...");
            this.gameProfileFetcher = new GameProfileFetcher();

            long start = System.currentTimeMillis();
            logger.info("[Core] Enabling modules...");

            this.modules = new ArrayList<>();

            for(Module module : enabledModules) {
                this.enableModule(module);
            }

            long end = System.currentTimeMillis();
            long time = (end - start);
            logger.info("[Core] Enabled modules in " + time + " ms.");
        } else {
            logger.severe("[Core] Could not connect to the SQL server.");
        }
    }

    public void disable() {
        this.connection.safeClose = true;
        this.connection.closeConnection();
        for(Module module : modules) {
            long start = System.currentTimeMillis();
            logger.info("[Module] Disabling " + module.name() + "...");
            module.onDisable();
            long end = System.currentTimeMillis();
            long time = (end - start);
            logger.info("[Module] Disabled " + module.name() + " in " + time + " ms.");
        }
        modules.clear();
    }

    public SQLConnection getSQLConnection() {
        return this.connection;
    }

    public AccountManager getAccountManager() { return this.accountManager; }

    public BugReportManager getBugReportManager() {
        return this.bugReportManager;
    }

    public GlacierPunishmentManager getGlacierPunishmentManager() { return this.glacierPunishmentManager; }

    public PunishmentManager getPunishmentManager() { return this.punishmentManager; }

    public PurchaseManager getPurchaseManager() { return this.purchaseManager; }

    public TitleManager getTitleManager() { return this.titleManager; }

    public StatisticsManager getStatisticsManager() { return this.statisticsManager; }

    public AchievementManager getAchievementManager() { return this.achievementManager; }

    public UUIDFetcher getUUIDFetcher() { return this.uuidFetcher; }

    public GameProfileFetcher getGameProfileFetcher() { return this.gameProfileFetcher; }

    public ReportManager getPlayerReportManager() { return this.playerReportManager; }

    public Logger getLogger() {
        return this.logger;
    }

    public List<Module> getModules() {
        return this.modules;
    }

    public Module getModule(Class<?> clazz) {
        for(Module module : this.modules) {
            if(module.getClass() == clazz) {
                return module;
            }
        }
        return null;
    }

    public boolean enableModule(Module module) {
        if(!modules.contains(module)) {
            modules.add(module);

            long start = System.currentTimeMillis();
            logger.info("[Module] Enabling " + module.name() + "...");
            module.onEnable();

            long end = System.currentTimeMillis();
            long time = (end - start);
            logger.info("[Module] Enabled " + module.name() + " in " + time + " ms.");

            return true;
        } else {
            return false;
        }
    }

    public boolean disableModule(Module module) {
        if(modules.contains(module)) {
            modules.remove(module);

            long start = System.currentTimeMillis();
            logger.info("[Module] Disabling " + module.name() + "...");
            module.onDisable();

            long end = System.currentTimeMillis();
            long time = (end - start);
            logger.info("[Module] Disabled " + module.name() + " in " + time + " ms.");

            return true;
        } else {
            return false;
        }
    }
}