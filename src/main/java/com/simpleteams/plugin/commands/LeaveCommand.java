package com.simpleteams.plugin.commands;

import com.simpleteams.plugin.SimpleTeams;
import com.simpleteams.plugin.team.Team;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LeaveCommand extends TeamSubCommand {

    public LeaveCommand(SimpleTeams plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "leave";
    }

    @Override
    public String getDescription() {
        return "Leave your current team";
    }

    @Override
    public String getUsage() {
        return "leave";
    }

    @Override
    public String getPermission() {
        return "simpleteams.player.leave";
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

        String teamName = team.getName();
        boolean wasLeader = team.isLeader(player.getUniqueId());

        if (wasLeader) {
            broadcast(team, null, "playerDisbandedBroadcast", "player", player.getName());
            plugin.getTeamManager().leaveTeam(player);
            plugin.getMessageManager().send(sender, "leaderLeftDisbanded", "team", teamName);
        } else {
            broadcast(team, player.getUniqueId(), "playerLeftBroadcast", "player", player.getName());
            plugin.getTeamManager().leaveTeam(player);
            plugin.getMessageManager().send(sender, "teamLeft", "team", teamName);
        }
    }
}