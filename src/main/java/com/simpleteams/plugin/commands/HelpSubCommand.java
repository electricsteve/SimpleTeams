package com.simpleteams.plugin.commands;

import com.simpleteams.plugin.SimpleTeams;
import org.bukkit.command.CommandSender;

import java.util.Map;

public class HelpSubCommand extends TeamSubCommand {

    private final Map<String, SubCommand> subCommands;

    public HelpSubCommand(SimpleTeams plugin, Map<String, SubCommand> subCommands) {
        super(plugin);
        this.subCommands = subCommands;
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "Show this help menu";
    }

    @Override
    public String getUsage() {
        return "help";
    }

    @Override
    public String getPermission() {
        return "simpleteams.player.help";
    }

    @Override
    public boolean isPlayerOnly() {
        return false;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        plugin.getMessageManager().sendRaw(sender, "helpHeader");
        for (SubCommand sub : subCommands.values()) {
            if (!sender.hasPermission(sub.getPermission())) {
                continue;
            }
            plugin.getMessageManager().sendRaw(sender, "helpEntry",
                    "usage", sub.getUsage(),
                    "description", sub.getDescription());
        }
        plugin.getMessageManager().sendRaw(sender, "helpFooter");
    }
}