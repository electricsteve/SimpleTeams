package com.simpleteams.plugin.commands;

import com.simpleteams.plugin.SimpleTeams;
import com.simpleteams.plugin.team.Team;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class InfoCommand extends TeamSubCommand {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public InfoCommand(SimpleTeams plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "info";
    }

    @Override
    public String getDescription() {
        return "View team information";
    }

    @Override
    public String getUsage() {
        return "info [team]";
    }

    @Override
    public String getPermission() {
        return "simpleteams.player.info";
    }

    @Override
    public boolean isPlayerOnly() {
        return false;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Team team;
        if (args.length > 1) {
            Optional<Team> opt = plugin.getTeamManager().getTeamByName(args[1]);
            if (opt.isEmpty()) {
                plugin.getMessageManager().send(sender, "teamNotFound", "team", args[1]);
                return;
            }
            team = opt.get();
        } else if (sender instanceof Player p) {
            team = plugin.getTeamManager().getPlayerTeam(p.getUniqueId());
            if (team == null) {
                plugin.getMessageManager().send(sender, "notInTeam");
                return;
            }
        } else {
            plugin.getMessageManager().send(sender, "usageJoin");
            return;
        }

        plugin.getMessageManager().sendRaw(sender, "infoHeader", "team", team.getName());
        plugin.getMessageManager().sendRaw(sender, "infoPrefix", "prefix", team.getPrefix());
        String statusKey = team.isOpen() ? "infoStatusOpen" : "infoStatusClosed";
        String statusStr = plugin.getMessageManager().getRaw(statusKey);
        plugin.getMessageManager().sendRaw(sender, "infoStatus", "status", statusStr);
        plugin.getMessageManager().sendRaw(sender, "infoMembers",
                "count", String.valueOf(team.getMemberCount()),
                "max", String.valueOf(plugin.getConfigManager().getMaxMembers()));
        plugin.getMessageManager().sendRaw(sender, "infoCreated",
                "date", DATE_FORMAT.format(new Date(team.getCreatedAt())));

        team.getMembers().entrySet().stream()
                .sorted((a, b) -> Integer.compare(b.getValue().getPriority(), a.getValue().getPriority()))
                .forEach(entry -> {
                    String playerName = Optional.ofNullable(Bukkit.getOfflinePlayer(entry.getKey()).getName()).orElse("Unknown");
                    plugin.getMessageManager().sendRaw(sender, "infoMemberEntry",
                            "player", playerName,
                            "rank", entry.getValue().getDisplayName());
                });

        plugin.getMessageManager().sendRaw(sender, "infoFooter");
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length != 2) {
            return List.of();
        }
        return plugin.getTeamManager().getAllTeams().stream()
                .map(Team::getName)
                .filter(n -> n.toLowerCase().startsWith(args[1].toLowerCase()))
                .collect(Collectors.toList());
    }
}