package com.simpleteams.plugin.commands;

import com.simpleteams.plugin.SimpleTeams;
import com.simpleteams.plugin.team.Team;
import com.simpleteams.plugin.team.TeamRank;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DemoteCommand extends TeamSubCommand {

    public DemoteCommand(SimpleTeams plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "demote";
    }

    @Override
    public String getDescription() {
        return "Demote a team member";
    }

    @Override
    public String getUsage() {
        return "demote <player>";
    }

    @Override
    public String getPermission() {
        return "simpleteams.player.demote";
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 2) {
            plugin.getMessageManager().send(sender, "usageDemote");
            return;
        }
        Player demoter = (Player) sender;
        Team team = plugin.getTeamManager().getPlayerTeam(demoter.getUniqueId());
        if (team == null) {
            plugin.getMessageManager().send(sender, "notInTeam");
            return;
        }
        if (!team.isLeader(demoter.getUniqueId())) {
            plugin.getMessageManager().send(sender, "notEnoughPermissionInTeam");
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            plugin.getMessageManager().send(sender, "playerNotFound", "player", args[1]);
            return;
        }
        if (!team.hasMember(target.getUniqueId())) {
            plugin.getMessageManager().send(sender, "targetNotInTeam", "player", target.getName());
            return;
        }
        if (target.getUniqueId().equals(demoter.getUniqueId())) {
            plugin.getMessageManager().send(sender, "cannotTargetSelf");
            return;
        }

        TeamRank current = team.getMemberRank(target.getUniqueId());
        TeamRank prev = current.previous();
        if (prev == null) {
            plugin.getMessageManager().send(sender, "cannotDemote", "player", target.getName());
            return;
        }

        team.setMemberRank(target.getUniqueId(), prev);
        plugin.getTeamManager().saveTeam(team);
        plugin.getMessageManager().send(sender, "demoted", "player", target.getName(), "rank", prev.getDisplayName());
        plugin.getMessageManager().send(target, "demotedTarget", "rank", prev.getDisplayName(), "team", team.getName());
    }
}