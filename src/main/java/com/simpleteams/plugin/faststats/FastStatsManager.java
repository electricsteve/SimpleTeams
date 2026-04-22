package com.simpleteams.plugin.faststats;

import dev.faststats.bukkit.BukkitMetrics;
import dev.faststats.core.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

public class FastStatsManager {

    private static final String FASTSTATS_TOKEN = "4a317fe4fc6f87384dafaf566515757e";
    private Metrics metrics;

    public void init(JavaPlugin plugin) {
        metrics = BukkitMetrics.factory()
                .token(FASTSTATS_TOKEN)
                .create(plugin);

        plugin.getLogger().info("FastStats enabled");
    }

    public Metrics getMetrics() {
        return metrics;
    }
}