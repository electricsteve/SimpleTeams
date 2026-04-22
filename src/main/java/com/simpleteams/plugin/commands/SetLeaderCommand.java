package com.simpleteams.plugin.commands;

import com.simpleteams.plugin.SimpleTeams;
import com.simpleteams.plugin.team.Team;
import com.simpleteams.plugin.team.TeamRank;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;

public class SetLeaderCommand extends TeamSubCommand {

    public SetLeaderCommand(SimpleTeams plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "setleader";
    }

    @Override
    public String getDescription() {
        return "Admin: transfer team leadership";
    }

    @Override
    public String getUsage() {
        return "setleader <team> <player>";
    }

    @Override
    public String getPermission() {
        return "simpleteams.admin.setleader";
    }

    @Override
    public boolean isPlayerOnly() {
        return false;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 3) {
            plugin.getMessageManager().send(sender, "usageSetleader");
            return;
        }
        Optional<Team> opt = plugin.getTeamManager().getTeamByName(args[1]);
        if (opt.isEmpty()) {
            plugin.getMessageManager().send(sender, "teamNotFound", "team", args[1]);
            return;
        }
        Team team = opt.get();

        Player target = Bukkit.getPlayer(args[2]);
        if (target == null) {
            plugin.getMessageManager().send(sender, "playerNotFound", "player", args[2]);
            return;
        }
        if (!team.hasMember(target.getUniqueId())) {
            plugin.getMessageManager().send(sender, "targetNotInTeam", "player", target.getName());
            return;
        }

        UUID oldLeader = team.getLeader();
        if (oldLeader != null) {
            team.setMemberRank(oldLeader, TeamRank.MEMBER);
        }
        team.setMemberRank(target.getUniqueId(), TeamRank.LEADER);
        plugin.getTeamManager().saveTeam(team);

        plugin.getMessageManager().send(sender, "setleaderSuccess",
                "team", team.getName(), "player", target.getName());
        broadcast(team, null, "setleaderBroadcast", "player", target.getName());
    }
}