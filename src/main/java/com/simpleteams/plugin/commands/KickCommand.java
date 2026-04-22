package com.simpleteams.plugin.commands;

import com.simpleteams.plugin.SimpleTeams;
import com.simpleteams.plugin.team.Team;
import com.simpleteams.plugin.team.TeamRank;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class KickCommand extends TeamSubCommand {

    public KickCommand(SimpleTeams plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "kick";
    }

    @Override
    public String getDescription() {
        return "Kick a member from your team";
    }

    @Override
    public String getUsage() {
        return "kick <player>";
    }

    @Override
    public String getPermission() {
        return "simpleteams.player.kick";
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 2) {
            plugin.getMessageManager().send(sender, "usageKick");
            return;
        }
        Player kicker = (Player) sender;
        Team team = plugin.getTeamManager().getPlayerTeam(kicker.getUniqueId());
        if (team == null) {
            plugin.getMessageManager().send(sender, "notInTeam");
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            plugin.getMessageManager().send(sender, "playerNotFound", "player", args[1]);
            return;
        }
        if (target.getUniqueId().equals(kicker.getUniqueId())) {
            plugin.getMessageManager().send(sender, "cannotTargetSelf");
            return;
        }
        if (!team.hasMember(target.getUniqueId())) {
            plugin.getMessageManager().send(sender, "targetNotInTeam", "player", target.getName());
            return;
        }

        TeamRank kickerRank = team.getMemberRank(kicker.getUniqueId());
        TeamRank targetRank = team.getMemberRank(target.getUniqueId());
        if (!kickerRank.isAtLeast(TeamRank.MODERATOR) || kickerRank.getPriority() <= targetRank.getPriority()) {
            plugin.getMessageManager().send(sender, "cannotKickHigherRank");
            return;
        }

        plugin.getTeamManager().kickMember(target.getUniqueId(), team, kicker.getUniqueId());
        plugin.getMessageManager().send(target, "playerKickedTarget", "team", team.getName());
        broadcast(team, target.getUniqueId(), "playerKickedBroadcast", "player", target.getName());
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length != 2 || !(sender instanceof Player p)) {
            return List.of();
        }
        Team team = plugin.getTeamManager().getPlayerTeam(p.getUniqueId());
        if (team == null) {
            return List.of();
        }
        return team.getMembers().keySet().stream()
                .map(uuid -> Optional.ofNullable(Bukkit.getOfflinePlayer(uuid).getName()).orElse(""))
                .filter(n -> !n.isEmpty() && n.toLowerCase().startsWith(args[1].toLowerCase()))
                .collect(Collectors.toList());
    }
}