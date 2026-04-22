package com.simpleteams.plugin.listeners;

import com.simpleteams.plugin.SimpleTeams;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    private final SimpleTeams plugin;

    public PlayerListener(SimpleTeams plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        plugin.getTeamManager().disableTeamChat(event.getPlayer().getUniqueId());
    }
}