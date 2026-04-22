package com.simpleteams.plugin.commands;

import com.simpleteams.plugin.SimpleTeams;
import com.simpleteams.plugin.manager.TeamManager.JoinResult;
import com.simpleteams.plugin.team.Team;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class JoinCommand extends TeamSubCommand {

    public JoinCommand(SimpleTeams plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "join";
    }

    @Override
    public String getDescription() {
        return "Join an open team";
    }

    @Override
    public String getUsage() {
        return "join <team>";
    }

    @Override
    public String getPermission() {
        return "simpleteams.player.join";
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 2) {
            plugin.getMessageManager().send(sender, "usageJoin");
            return;
        }
        Player player = (Player) sender;

        Optional<Team> opt = plugin.getTeamManager().getTeamByName(args[1]);
        if (opt.isEmpty()) {
            plugin.getMessageManager().send(sender, "teamNotFound", "team", args[1]);
            return;
        }
        Team team = opt.get();

        JoinResult result = plugin.getTeamManager().joinTeam(player, team);
        switch (result) {
            case SUCCESS -> {
                plugin.getMessageManager().send(sender, "teamJoined", "team", team.getName());
                broadcast(team, player.getUniqueId(), "teamJoinedBroadcast", "player", player.getName());
            }
            case ALREADY_IN_TEAM -> plugin.getMessageManager().send(sender, "alreadyInTeam");
            case BANNED -> plugin.getMessageManager().send(sender, "bannedFromTeam", "team", team.getName());
            case CLOSED -> plugin.getMessageManager().send(sender, "teamIsClosed");
            case FULL -> plugin.getMessageManager().send(sender, "teamFull",
                    "count", String.valueOf(team.getMemberCount()),
                    "max", String.valueOf(plugin.getConfigManager().getMaxMembers()));
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length != 2) {
            return List.of();
        }
        return plugin.getTeamManager().getOpenTeams().stream()
                .map(Team::getName)
                .filter(n -> n.toLowerCase().startsWith(args[1].toLowerCase()))
                .collect(Collectors.toList());
    }
}