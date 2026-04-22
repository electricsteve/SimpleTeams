package com.simpleteams.plugin.commands;

import com.simpleteams.plugin.SimpleTeams;
import com.simpleteams.plugin.team.Team;
import com.simpleteams.plugin.team.TeamRank;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CloseCommand extends TeamSubCommand {

    public CloseCommand(SimpleTeams plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "close";
    }

    @Override
    public String getDescription() {
        return "Close team from public joining";
    }

    @Override
    public String getUsage() {
        return "close";
    }

    @Override
    public String getPermission() {
        return "simpleteams.player.close";
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
        team.setOpen(false);
        plugin.getTeamManager().saveTeam(team);
        plugin.getMessageManager().send(sender, "teamClosed");
    }
}