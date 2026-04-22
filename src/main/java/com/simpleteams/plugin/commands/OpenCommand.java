package com.simpleteams.plugin.commands;

import com.simpleteams.plugin.SimpleTeams;
import com.simpleteams.plugin.team.Team;
import com.simpleteams.plugin.team.TeamRank;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class OpenCommand extends TeamSubCommand {

    public OpenCommand(SimpleTeams plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "open";
    }

    @Override
    public String getDescription() {
        return "Open team to public joining";
    }

    @Override
    public String getUsage() {
        return "open";
    }

    @Override
    public String getPermission() {
        return "simpleteams.player.open";
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        Team team = plugin.getTeamManager().getPlayerTeam(player.getUniqueId());
        if (team == null) {
            plugin.getMessageManager().send(sender, "notInTeam");
            return;
        }
        if (!team.getMemberRank(player.getUniqueId()).isAtLeast(TeamRank.MODERATOR)) {
            plugin.getMessageManager().send(sender, "notEnoughPermissionInTeam");
            return;
        }
        team.setOpen(true);
        plugin.getTeamManager().saveTeam(team);
        plugin.getMessageManager().send(sender, "teamOpened");
    }
}