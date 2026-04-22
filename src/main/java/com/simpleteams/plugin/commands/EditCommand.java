package com.simpleteams.plugin.commands;

import com.simpleteams.plugin.SimpleTeams;
import com.simpleteams.plugin.team.Team;
import com.simpleteams.plugin.util.TeamNameValidator;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class EditCommand extends TeamSubCommand {

    public EditCommand(SimpleTeams plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "edit";
    }

    @Override
    public String getDescription() {
        return "Edit team prefix or name";
    }

    @Override
    public String getUsage() {
        return "edit <prefix|name> <value>";
    }

    @Override
    public String getPermission() {
        return "simpleteams.player.edit";
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 3) {
            plugin.getMessageManager().send(sender, "usageEdit");
            return;
        }
        Player player = (Player) sender;
        Team team = plugin.getTeamManager().getPlayerTeam(player.getUniqueId());
        if (team == null) {
            plugin.getMessageManager().send(sender, "notInTeam");
            return;
        }
        if (!team.isLeader(player.getUniqueId())) {
            plugin.getMessageManager().send(sender, "notEnoughPermissionInTeam");
            return;
        }

        String action = args[1].toLowerCase();
        String value = String.join(" ", Arrays.copyOfRange(args, 2, args.length));

        switch (action) {
            case "prefix" -> {
                String stripped = MiniMessage.miniMessage().stripTags(value);
                int maxLen = plugin.getConfigManager().getMaxPrefixLength();
                if (stripped.length() > maxLen) {
                    plugin.getMessageManager().send(sender, "prefixTooLong", "max", String.valueOf(maxLen));
                    return;
                }
                team.setPrefix(value + " ");
                plugin.getTeamManager().saveTeam(team);
                plugin.getMessageManager().send(sender, "prefixChanged", "prefix", value);
            }
            case "name" -> {
                TeamNameValidator nameValidator = new TeamNameValidator(plugin);
                TeamNameValidator.ValidationResult vr = nameValidator.validate(value);
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
                if (plugin.getTeamManager().getTeamByName(value).isPresent()) {
                    plugin.getMessageManager().send(sender, "teamNameTaken", "team", value);
                    return;
                }
                team.setName(value);
                plugin.getTeamManager().saveTeam(team);
                plugin.getMessageManager().send(sender, "nameChanged", "name", value);
            }
            default -> plugin.getMessageManager().send(sender, "usageEdit");
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 2) {
            return List.of("prefix", "name");
        }
        return List.of();
    }
}