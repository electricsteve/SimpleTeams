package com.simpleteams.plugin.commands;

import com.simpleteams.plugin.SimpleTeams;
import com.simpleteams.plugin.team.Team;
import com.simpleteams.plugin.team.TeamRank;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UnbanCommand extends TeamSubCommand {

    public UnbanCommand(SimpleTeams plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "unban";
    }

    @Override
    public String getDescription() {
        return "Unban a player from your team";
    }

    @Override
    public String getUsage() {
        return "unban <player>";
    }

    @Override
    public String getPermission() {
        return "simpleteams.player.unban";
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 2) {
            plugin.getMessageManager().send(sender, "usageUnban");
            return;
        }
        Player unbanner = (Player) sender;
        Team team = plugin.getTeamManager().getPlayerTeam(unbanner.getUniqueId());
        if (team == null) {
            plugin.getMessageManager().send(sender, "notInTeam");
            return;
        }
        if (!team.getMemberRank(unbanner.getUniqueId()).isAtLeast(TeamRank.MODERATOR)) {
            plugin.getMessageManager().send(sender, "notEnoughPermissionInTeam");
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            plugin.getMessageManager().send(sender, "playerNotFound", "player", args[1]);
            return;
        }
        if (!team.isBanned(target.getUniqueId())) {
            plugin.getMessageManager().send(sender, "notBanned", "player", target.getName());
            return;
        }

        plugin.getTeamManager().unbanMember(target.getUniqueId(), team);
        plugin.getMessageManager().send(sender, "playerUnbanned", "player", target.getName());
    }
}