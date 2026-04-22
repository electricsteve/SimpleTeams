package com.simpleteams.plugin.commands;

import com.simpleteams.plugin.SimpleTeams;
import com.simpleteams.plugin.team.Team;
import com.simpleteams.plugin.team.TeamRank;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BanCommand extends TeamSubCommand {

    public BanCommand(SimpleTeams plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "ban";
    }

    @Override
    public String getDescription() {
        return "Ban a player from your team";
    }

    @Override
    public String getUsage() {
        return "ban <player>";
    }

    @Override
    public String getPermission() {
        return "simpleteams.player.ban";
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 2) {
            plugin.getMessageManager().send(sender, "usageBan");
            return;
        }
        Player banner = (Player) sender;
        Team team = plugin.getTeamManager().getPlayerTeam(banner.getUniqueId());
        if (team == null) {
            plugin.getMessageManager().send(sender, "notInTeam");
            return;
        }
        if (!team.getMemberRank(banner.getUniqueId()).isAtLeast(TeamRank.MODERATOR)) {
            plugin.getMessageManager().send(sender, "notEnoughPermissionInTeam");
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            plugin.getMessageManager().send(sender, "playerNotFound", "player", args[1]);
            return;
        }
        if (target.getUniqueId().equals(banner.getUniqueId())) {
            plugin.getMessageManager().send(sender, "cannotTargetSelf");
            return;
        }
        if (team.isBanned(target.getUniqueId())) {
            plugin.getMessageManager().send(sender, "alreadyBanned", "player", target.getName());
            return;
        }

        if (team.hasMember(target.getUniqueId())) {
            TeamRank bannerRank = team.getMemberRank(banner.getUniqueId());
            TeamRank targetRank = team.getMemberRank(target.getUniqueId());
            if (bannerRank.getPriority() <= targetRank.getPriority()) {
                plugin.getMessageManager().send(sender, "cannotBanHigherRank");
                return;
            }
        }

        plugin.getTeamManager().banMember(target.getUniqueId(), team);
        plugin.getMessageManager().send(sender, "playerBanned", "player", target.getName());
        plugin.getMessageManager().send(target, "playerBannedTarget", "team", team.getName());
    }
}