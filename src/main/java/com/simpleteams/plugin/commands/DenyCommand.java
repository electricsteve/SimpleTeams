package com.simpleteams.plugin.commands;

import com.simpleteams.plugin.SimpleTeams;
import com.simpleteams.plugin.team.Team;
import com.simpleteams.plugin.team.TeamInvite;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DenyCommand extends TeamSubCommand {

    public DenyCommand(SimpleTeams plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "deny";
    }

    @Override
    public String getDescription() {
        return "Deny a pending team invite";
    }

    @Override
    public String getUsage() {
        return "deny";
    }

    @Override
    public String getPermission() {
        return "simpleteams.player.deny";
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
        String teamName = team != null ? team.getName() : "?";
        plugin.getTeamManager().clearInvite(player.getUniqueId());
        plugin.getMessageManager().send(sender, "inviteDenied", "team", teamName);

        if (team != null) {
            Player inviter = Bukkit.getPlayer(invite.getInviterUUID());
            if (inviter != null) {
                plugin.getMessageManager().send(inviter, "inviteDeniedBroadcast", "player", player.getName());
            }
        }
    }
}