package com.simpleteams.plugin.commands;

import com.simpleteams.plugin.SimpleTeams;
import org.bukkit.command.CommandSender;

public class ReloadSubCommand extends TeamSubCommand {

    public ReloadSubCommand(SimpleTeams plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public String getDescription() {
        return "Reload config and messages";
    }

    @Override
    public String getUsage() {
        return "reload";
    }

    @Override
    public String getPermission() {
        return "simpleteams.admin.reload";
    }

    @Override
    public boolean isPlayerOnly() {
        return false;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        plugin.reload();
        plugin.getMessageManager().send(sender, "reloadSuccess");
    }
}