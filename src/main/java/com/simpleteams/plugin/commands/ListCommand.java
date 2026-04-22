package com.simpleteams.plugin.commands;

import com.simpleteams.plugin.SimpleTeams;
import com.simpleteams.plugin.team.Team;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.Comparator;
import java.util.Optional;
import java.util.Set;

public class ListCommand extends TeamSubCommand {

    public ListCommand(SimpleTeams plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "list";
    }

    @Override
    public String getDescription() {
        return "List all teams";
    }

    @Override
    public String getUsage() {
        return "list";
    }

    @Override
    public String getPermission() {
        return "simpleteams.player.list";
    }

    @Override
    public boolean isPlayerOnly() {
        return false;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Set<Team> all = plugin.getTeamManager().getAllTeams();
        plugin.getMessageManager().sendRaw(sender, "listHeader", "count", String.valueOf(all.size()));
        if (all.isEmpty()) {
            plugin.getMessageManager().sendRaw(sender, "listEmpty");
            return;
        }
        all.stream()
                .sorted(Comparator.comparing(Team::getName))
                .forEach(t -> {
                    String leader = Optional.ofNullable(Bukkit.getOfflinePlayer(t.getLeader()).getName()).orElse("Unknown");
                    plugin.getMessageManager().sendRaw(sender, "listEntry",
                            "team", t.getName(),
                            "leader", leader,
                            "members", String.valueOf(t.getMemberCount()));
                });
        plugin.getMessageManager().sendRaw(sender, "listFooter");
    }
}