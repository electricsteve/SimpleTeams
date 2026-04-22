package com.simpleteams.plugin.commands;

import com.simpleteams.plugin.SimpleTeams;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChatCommand extends TeamSubCommand {

    public ChatCommand(SimpleTeams plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "chat";
    }

    @Override
    public String getDescription() {
        return "Toggle team-only chat mode";
    }

    @Override
    public String getUsage() {
        return "chat";
    }

    @Override
    public String getPermission() {
        return "simpleteams.player.chat";
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!plugin.getConfigManager().isTeamChatEnabled()) {
            plugin.getMessageManager().send(sender, "teamChatDisabledConfig");
            return;
        }
        Player player = (Player) sender;
        if (!plugin.getTeamManager().hasTeam(player.getUniqueId())) {
            plugin.getMessageManager().send(sender, "notInTeam");
            return;
        }
        boolean nowEnabled = plugin.getTeamManager().toggleTeamChat(player.getUniqueId());
        plugin.getMessageManager().send(sender, nowEnabled ? "teamChatEnabled" : "teamChatDisabled");
    }
}