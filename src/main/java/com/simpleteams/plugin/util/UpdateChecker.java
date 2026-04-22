package com.simpleteams.plugin.util;

import com.simpleteams.plugin.SimpleTeams;
import org.bukkit.Bukkit;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateChecker {

    private static final String SIMPLETEAMS_MODRINTH_ID = "X3pQ9SGR";
    private final SimpleTeams plugin;

    public UpdateChecker(SimpleTeams plugin) {
        this.plugin = plugin;
    }

    public void checkAsync() {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                String apiUrl = "https://api.modrinth.com/v2/project/" + SIMPLETEAMS_MODRINTH_ID + "/version?loaders=[\"paper\",\"spigot\"]";

                HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("User-Agent", "SimpleTeams/" + plugin.getDescription().getVersion());
                conn.setRequestProperty("Accept", "application/json");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);

                int status = conn.getResponseCode();
                if (status != 200) {
                    plugin.getMessageManager().send(
                            plugin.getServer().getConsoleSender(), "updateFailed");
                    return;
                }

                StringBuilder sb = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                }

                String body = sb.toString();
                if (!body.contains("\"version_number\"")) {
                    plugin.getMessageManager().send(
                            plugin.getServer().getConsoleSender(), "updateUpToDate");
                    return;
                }

                String latestVersion = extractFirstVersionNumber(body);
                String currentVersion = plugin.getDescription().getVersion();

                if (latestVersion != null && !latestVersion.equals(currentVersion)) {
                    String downloadUrl = "https://modrinth.com/plugin/" + SIMPLETEAMS_MODRINTH_ID;
                    plugin.getMessageManager().send(
                            plugin.getServer().getConsoleSender(),
                            "updateAvailable",
                            "version", latestVersion,
                            "url", downloadUrl
                    );
                    plugin.getServer().getScheduler().runTask(plugin, () ->
                            plugin.getServer().getOnlinePlayers().stream()
                                    .filter(p -> p.hasPermission("simpleteams.admin.reload"))
                                    .forEach(p -> plugin.getMessageManager().send(p,
                                            "updateAvailable",
                                            "version", latestVersion,
                                            "url", downloadUrl))
                    );
                } else {
                    plugin.getMessageManager().send(
                            plugin.getServer().getConsoleSender(), "updateUpToDate");
                }

            } catch (Exception e) {
                plugin.getLogger().warning("Update check failed: " + e.getMessage());
                plugin.getMessageManager().send(
                        plugin.getServer().getConsoleSender(), "updateFailed");
            }
        });
    }

    private String extractFirstVersionNumber(String json) {
        String key = "\"version_number\":\"";
        int start = json.indexOf(key);
        if (start == -1) {
            return null;
        }
        start += key.length();
        int end = json.indexOf('"', start);
        if (end == -1) {
            return null;
        }
        return json.substring(start, end);
    }
}