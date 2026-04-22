package com.simpleteams.plugin.commands;

import com.simpleteams.plugin.SimpleTeams;
import com.simpleteams.plugin.team.Team;
import com.simpleteams.plugin.team.TeamRank;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PromoteCommand extends TeamSubCommand {

    public PromoteCommand(SimpleTeams plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "promote";
    }

    @Override
    public String getDescription() {
        return "Promote a team member";
    }

    @Override
    public String getUsage() {
        return "promote <player>";
    }

    @Override
    public String getPermission() {
        return "simpleteams.player.promote";
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 2) {
            plugin.getMessageManager().send(sender, "usagePromote");
            return;
        }
        Player promoter = (Player) sender;
        Team team = plugin.getTeamManager().getPlayerTeam(promoter.getUniqueId());
        if (team == null) {
            plugin.getMessageManager().send(sender, "notInTeam");
            return;
        }
        if (!team.isLeader(promoter.getUniqueId())) {
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
        if (target.getUniqueId().equals(promoter.getUniqueId())) {
            plugin.getMessageManager().send(sender, "cannotTargetSelf");
            return;
        }

        TeamRank current = team.getMemberRank(target.getUniqueId());
        TeamRank next = current.next();
        if (next == null) {
            plugin.getMessageManager().send(sender, "cannotPromoteLeader");
            return;
        }

        team.setMemberRank(target.getUniqueId(), next);
        plugin.getTeamManager().saveTeam(team);
        plugin.getMessageManager().send(sender, "promoted", "player", target.getName(), "rank", next.getDisplayName());
        plugin.getMessageManager().send(target, "promotedTarget", "rank", next.getDisplayName(), "team", team.getName());
    }
}