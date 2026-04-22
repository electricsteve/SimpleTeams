package com.simpleteams.plugin.commands;

import com.simpleteams.plugin.SimpleTeams;
import com.simpleteams.plugin.team.Team;
import com.simpleteams.plugin.team.TeamRank;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class InviteCommand extends TeamSubCommand {

    public InviteCommand(SimpleTeams plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "invite";
    }

    @Override
    public String getDescription() {
        return "Invite a player to your team";
    }

    @Override
    public String getUsage() {
        return "invite <player>";
    }

    @Override
    public String getPermission() {
        return "simpleteams.player.invite";
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!plugin.getConfigManager().isInvitesEnabled()) {
            plugin.getMessageManager().send(sender, "inviteDisabled");
            return;
        }
        if (args.length < 2) {
            plugin.getMessageManager().send(sender, "usageInvite");
            return;
        }
        Player inviter = (Player) sender;
        Team team = plugin.getTeamManager().getPlayerTeam(inviter.getUniqueId());
        if (team == null) {
            plugin.getMessageManager().send(sender, "notInTeam");
            return;
        }
        if (!team.getMemberRank(inviter.getUniqueId()).isAtLeast(TeamRank.MODERATOR)) {
            plugin.getMessageManager().send(sender, "notEnoughPermissionInTeam");
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            plugin.getMessageManager().send(sender, "playerNotFound", "player", args[1]);
            return;
        }
        if (plugin.getTeamManager().hasTeam(target.getUniqueId())) {
            plugin.getMessageManager().send(sender, "invitePlayerHasTeam", "player", target.getName());
            return;
        }
        if (plugin.getTeamManager().hasOutgoingInvite(team.getId(), target.getUniqueId())) {
            plugin.getMessageManager().send(sender, "inviteAlreadySent", "player", target.getName());
            return;
        }
        if (team.getMemberCount() >= plugin.getConfigManager().getMaxMembers()) {
            plugin.getMessageManager().send(sender, "teamFull",
                    "count", String.valueOf(team.getMemberCount()),
                    "max", String.valueOf(plugin.getConfigManager().getMaxMembers()));
            return;
        }

        plugin.getTeamManager().sendInvite(target.getUniqueId(), team.getId(), inviter.getUniqueId());
        plugin.getMessageManager().send(sender, "inviteSent",
                "player", target.getName(),
                "seconds", String.valueOf(plugin.getConfigManager().getInviteExpireSeconds()));
        plugin.getMessageManager().send(target, "inviteReceived",
                "player", inviter.getName(), "team", team.getName());
        plugin.getMessageManager().send(target, "inviteReceivedHint");
    }
}