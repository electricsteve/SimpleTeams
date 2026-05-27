package com.simpleteams.plugin.commands;

import com.simpleteams.plugin.SimpleTeams;
import com.simpleteams.plugin.manager.TeamManager.JoinResult;
import com.simpleteams.plugin.team.Team;
import com.simpleteams.plugin.team.TeamInvite;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AcceptCommand extends TeamSubCommand {

    public AcceptCommand(SimpleTeams plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "accept";
    }

    @Override
    public String getDescription() {
        return "Accept a pending team invite";
    }

    @Override
    public String getUsage() {
        return "accept";
    }

    @Override
    public String getPermission() {
        return "simpleteams.player.accept";
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        TeamInvite invite = plugin.getTeamManager().getPendingInvite(player.getUniqueId());
        if (invite == null) {
            plugin.getMessageManager().send(sender, "inviteNoPending");
            return;
        }

        Team team = plugin.getTeamManager().getTeam(invite.getTeamId());
        if (team == null) {
            plugin.getMessageManager().send(sender, "teamNotFound", "team", "?");
            return;
        }

        plugin.getTeamManager().clearInvite(player.getUniqueId());
        JoinResult result = plugin.getTeamManager().joinTeam(player, team, true);
        if (result == JoinResult.SUCCESS) {
            plugin.getMessageManager().send(sender, "inviteAccepted", "team", team.getName());
            broadcast(team, player.getUniqueId(), "inviteAcceptedBroadcast", "player", player.getName());
        } else {
            plugin.getMessageManager().send(sender, "teamFull",
                    "count", String.valueOf(team.getMemberCount()),
                    "max", String.valueOf(plugin.getConfigManager().getMaxMembers()));
        }
    }
}