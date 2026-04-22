package com.simpleteams.plugin;

import com.simpleteams.plugin.commands.TeamCommand;
import com.simpleteams.plugin.faststats.FastStatsManager;
import com.simpleteams.plugin.listeners.ChatListener;
import com.simpleteams.plugin.listeners.PlayerListener;
import com.simpleteams.plugin.manager.ConfigManager;
import com.simpleteams.plugin.manager.MessageManager;
import com.simpleteams.plugin.manager.TeamManager;
import com.simpleteams.plugin.storage.YamlStorage;
import com.simpleteams.plugin.util.UpdateChecker;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SingleLineChart;
import org.bukkit.plugin.java.JavaPlugin;

public final class SimpleTeams extends JavaPlugin {

    private static final int BSTATS_ID = 30889;

    private static SimpleTeams instance;
    private TeamManager teamManager;
    private ConfigManager configManager;
    private MessageManager messageManager;
    private FastStatsManager fastStatsManager;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();

        configManager = new ConfigManager(this);
        messageManager = new MessageManager(this);
        teamManager = new TeamManager(this, new YamlStorage(this));

        TeamCommand teamCommand = new TeamCommand(this);
        getCommand("team").setExecutor(teamCommand);
        getCommand("team").setTabCompleter(teamCommand);

        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getServer().getPluginManager().registerEvents(new ChatListener(this), this);

        if (configManager.isBStatsEnabled()) {
            setupBStats();
        }

        fastStatsManager = new FastStatsManager();
        fastStatsManager.init(this);


        if (configManager.isUpdateCheckerEnabled()) {
            new UpdateChecker(this).checkAsync();
        }

        getLogger().info("SimpleTeams v" + getDescription().getVersion() + " has been enabled!");
    }

    @Override
    public void onDisable() {
        if (teamManager != null) {
            teamManager.saveAllTeams();
        }
        getLogger().info("SimpleTeams has been disabled!");
    }

    private void setupBStats() {
        Metrics metrics = new Metrics(this, BSTATS_ID);

        metrics.addCustomChart(new SingleLineChart("total_teams", () -> teamManager.getAllTeams().size()));

    }

    public void reload() {
        reloadConfig();
        configManager.reload();
        messageManager.reload();
    }

    public static SimpleTeams getInstance() {
        return instance;
    }

    public TeamManager getTeamManager() {
        return teamManager;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }

    public FastStatsManager getFastStatsManager() {
        return fastStatsManager;
    }
}