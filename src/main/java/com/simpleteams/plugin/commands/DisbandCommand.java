package com.simpleteams.plugin.commands;

import com.simpleteams.plugin.SimpleTeams;
import com.simpleteams.plugin.team.Team;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.Optional;

public class DisbandCommand extends TeamSubCommand {

    public DisbandCommand(SimpleTeams plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "disband";
    }

    @Override
    public String getDescription() {
        return "Disband your team";
    }

    @Override
    public String getUsage() {
        return "disband [team]";
    }

    @Override
    public String getPermission() {
        return "simpleteams.player.disband";
    }

    @Override
    public boolean isPlayerOnly() {
        return false;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Team team;
        boolean admin = args.length > 1 && sender.hasPermission("simpleteams.admin.disband");
        if (admin) {
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
            if (!team.isLeader(p.getUniqueId())) {
                plugin.getMessageManager().send(sender, "notEnoughPermissionInTeam");
                return;
            }
        } else {
            plugin.getMessageManager().send(sender, "playerOnly");
            return;
        }

        String teamName = team.getName();
        String disbandKey = admin ? "teamDisbandedAdmin" : "teamDisbanded";

        team.getMembers().keySet().stream()
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull)
                .filter(p -> !(sender instanceof Player sp) || !p.getUniqueId().equals(sp.getUniqueId()))
                .forEach(p -> plugin.getMessageManager().send(p, "teamDisbanded", "team", teamName));

        plugin.getTeamManager().disbandTeam(team.getId());
        plugin.getMessageManager().send(sender, disbandKey, "team", teamName);
    }
}