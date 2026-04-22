package com.simpleteams.plugin.commands;

import com.simpleteams.plugin.SimpleTeams;
import com.simpleteams.plugin.team.Team;
import com.simpleteams.plugin.util.TeamNameValidator;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CreateCommand extends TeamSubCommand {
    private final TeamNameValidator nameValidator;

    public CreateCommand(SimpleTeams plugin) {
        super(plugin);
        this.nameValidator = new TeamNameValidator(plugin);
    }

    @Override
    public String getName() {
        return "create";
    }

    @Override
    public String getDescription() {
        return "Create a new team";
    }

    @Override
    public String getUsage() {
        return "create <name>";
    }

    @Override
    public String getPermission() {
        return "simpleteams.player.create";
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 2) {
            plugin.getMessageManager().send(sender, "usageCreate");
            return;
        }
        Player player = (Player) sender;
        String name = args[1];

        if (plugin.getTeamManager().hasTeam(player.getUniqueId())) {
            plugin.getMessageManager().send(sender, "alreadyInTeam");
            return;
        }

        TeamNameValidator.ValidationResult vr = nameValidator.validate(name);
        switch (vr) {
            case TOO_SHORT -> {
                plugin.getMessageManager().send(sender, "nameTooShort", "min", String.valueOf(plugin.getConfigManager().getMinNameLength()));
                return;
            }
            case TOO_LONG -> {
                plugin.getMessageManager().send(sender, "nameTooLong", "max", String.valueOf(plugin.getConfigManager().getMaxNameLength()));
                return;
            }
            case INVALID_CHARS -> {
                plugin.getMessageManager().send(sender, "nameInvalidChars");
                return;
            }
            case BLACKLISTED -> {
                plugin.getMessageManager().send(sender, "nameBlacklisted");
                return;
            }
            default -> {
            }
        }

        if (plugin.getTeamManager().getTeamByName(name).isPresent()) {
            plugin.getMessageManager().send(sender, "teamNameTaken", "team", name);
            return;
        }

        Team team = plugin.getTeamManager().createTeam(name, player.getUniqueId());
        if (team == null) {
            plugin.getMessageManager().send(sender, "teamLimitReached");
            return;
        }
        plugin.getMessageManager().send(sender, "teamCreated", "team", name);
    }
}