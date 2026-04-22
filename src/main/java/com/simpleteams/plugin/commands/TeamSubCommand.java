package com.simpleteams.plugin.commands;

import com.simpleteams.plugin.SimpleTeams;
import com.simpleteams.plugin.team.Team;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.UUID;

public abstract class TeamSubCommand implements SubCommand {
    protected final SimpleTeams plugin;

    protected TeamSubCommand(SimpleTeams plugin) {
        this.plugin = plugin;
    }

    protected void broadcast(Team team, UUID exclude, String messageKey, String... placeholders) {
        team.getMembers().keySet().stream()
                .filter(uuid -> !uuid.equals(exclude))
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull)
                .forEach(p -> plugin.getMessageManager().send(p, messageKey, placeholders));
    }
}