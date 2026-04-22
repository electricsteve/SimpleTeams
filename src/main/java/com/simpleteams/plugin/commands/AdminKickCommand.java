package com.simpleteams.plugin.commands;

import com.simpleteams.plugin.SimpleTeams;
import com.simpleteams.plugin.team.Team;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AdminKickCommand extends TeamSubCommand {

    public AdminKickCommand(SimpleTeams plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "adminkick";
    }

    @Override
    public String getDescription() {
        return "Admin: kick player from any team";
    }

    @Override
    public String getUsage() {
        return "adminkick <player>";
    }

    @Override
    public String getPermission() {
        return "simpleteams.admin.kick";
    }

    @Override
    public boolean isPlayerOnly() {
        return false;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 2) {
            plugin.getMessageManager().send(sender, "usageAdminkick");
            return;
        }
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            plugin.getMessageManager().send(sender, "playerNotFound", "player", args[1]);
            return;
        }

        Team team = plugin.getTeamManager().getPlayerTeam(target.getUniqueId());
        if (team == null) {
            plugin.getMessageManager().send(sender, "notInTeam");
            return;
        }

        plugin.getTeamManager().kickMember(target.getUniqueId(), team, null);
        plugin.getMessageManager().send(sender, "playerKickedBroadcast", "player", target.getName());
        plugin.getMessageManager().send(target, "playerKickedTarget", "team", team.getName());
    }
}