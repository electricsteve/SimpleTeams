package com.simpleteams.plugin.commands;

import com.simpleteams.plugin.SimpleTeams;
import com.simpleteams.plugin.team.Team;
import org.bukkit.command.CommandSender;

import java.util.List;

public class TopCommand extends TeamSubCommand {

    public TopCommand(SimpleTeams plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "top";
    }

    @Override
    public String getDescription() {
        return "Top teams by member count";
    }

    @Override
    public String getUsage() {
        return "top";
    }

    @Override
    public String getPermission() {
        return "simpleteams.player.top";
    }

    @Override
    public boolean isPlayerOnly() {
        return false;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        int limit = plugin.getConfigManager().getTopEntries();
        List<Team> sorted = plugin.getTeamManager().getAllTeams().stream()
                .sorted((a, b) -> Integer.compare(b.getMemberCount(), a.getMemberCount()))
                .limit(limit)
                .toList();

        plugin.getMessageManager().sendRaw(sender, "topHeader");
        for (int i = 0; i < sorted.size(); i++) {
            Team t = sorted.get(i);
            plugin.getMessageManager().sendRaw(sender, "topEntry",
                    "rank", String.valueOf(i + 1),
                    "team", t.getName(),
                    "members", String.valueOf(t.getMemberCount()));
        }
        plugin.getMessageManager().sendRaw(sender, "topFooter");
    }
}