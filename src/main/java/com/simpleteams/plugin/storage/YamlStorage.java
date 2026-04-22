package com.simpleteams.plugin.storage;

import com.simpleteams.plugin.SimpleTeams;
import com.simpleteams.plugin.team.Team;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class YamlStorage implements Storage {

    private final SimpleTeams plugin;
    private final File teamsFile;
    private FileConfiguration teamsConfig;

    public YamlStorage(SimpleTeams plugin) {
        this.plugin = plugin;
        this.teamsFile = new File(plugin.getDataFolder(), "teams.yml");
        load();
    }

    private void load() {
        if (!teamsFile.exists()) {
            try {
                teamsFile.getParentFile().mkdirs();
                teamsFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create teams.yml!");
                e.printStackTrace();
            }
        }
        teamsConfig = YamlConfiguration.loadConfiguration(teamsFile);
    }

    @Override
    public void saveTeam(Team team) {
        String path = "teams." + team.getId();
        teamsConfig.set(path, null);
        team.save(teamsConfig.createSection(path));
        saveFile();
    }

    @Override
    public void deleteTeam(UUID teamId) {
        teamsConfig.set("teams." + teamId, null);
        saveFile();
    }

    @Override
    public Map<UUID, Team> loadAllTeams() {
        Map<UUID, Team> result = new HashMap<>();
        ConfigurationSection section = teamsConfig.getConfigurationSection("teams");
        if (section == null) {
            return result;
        }

        for (String key : section.getKeys(false)) {
            try {
                UUID id = UUID.fromString(key);
                Team team = new Team(id, section.getConfigurationSection(key));
                result.put(id, team);
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to load team: " + key + " — " + e.getMessage());
            }
        }
        return result;
    }

    @Override
    public void saveAllTeams(Map<UUID, Team> teams) {
        teamsConfig.set("teams", null);
        for (Team team : teams.values()) {
            String path = "teams." + team.getId();
            team.save(teamsConfig.createSection(path));
        }
        saveFile();
    }

    private void saveFile() {
        try {
            teamsConfig.save(teamsFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save teams.yml!");
            e.printStackTrace();
        }
    }
}