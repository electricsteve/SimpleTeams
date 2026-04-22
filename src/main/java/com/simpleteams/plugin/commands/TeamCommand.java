package com.simpleteams.plugin.commands;

import com.simpleteams.plugin.SimpleTeams;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TeamCommand implements TabExecutor {

    private final SimpleTeams plugin;
    private final Map<String, SubCommand> subCommands = new LinkedHashMap<>();

    public TeamCommand(SimpleTeams plugin) {
        this.plugin = plugin;
        register();
    }

    private void register() {
        add(new CreateCommand(plugin));
        add(new JoinCommand(plugin));
        add(new LeaveCommand(plugin));
        add(new InfoCommand(plugin));
        add(new ListCommand(plugin));
        add(new TopCommand(plugin));
        add(new OpenCommand(plugin));
        add(new CloseCommand(plugin));
        add(new KickCommand(plugin));
        add(new PromoteCommand(plugin));
        add(new DemoteCommand(plugin));
        add(new DisbandCommand(plugin));
        add(new AdminKickCommand(plugin));
        add(new BanCommand(plugin));
        add(new UnbanCommand(plugin));
        add(new EditCommand(plugin));
        add(new InviteCommand(plugin));
        add(new AcceptCommand(plugin));
        add(new DenyCommand(plugin));
        add(new ChatCommand(plugin));
        add(new SetLeaderCommand(plugin));
        add(new ReloadSubCommand(plugin));
        add(new HelpSubCommand(plugin, subCommands));
    }

    private void add(SubCommand cmd) {
        subCommands.put(cmd.getName().toLowerCase(), cmd);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
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
            return true;
        }

        SubCommand sub = subCommands.get(args[0].toLowerCase());
        if (sub == null) {
            plugin.getMessageManager().send(sender, "unknownCommand");
            return true;
        }

        if (!sender.hasPermission(sub.getPermission())) {
            plugin.getMessageManager().send(sender, "noPermission");
            return true;
        }

        if (sub.isPlayerOnly() && !(sender instanceof Player)) {
            plugin.getMessageManager().send(sender, "playerOnly");
            return true;
        }

        sub.execute(sender, args);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 1) {
            return subCommands.entrySet().stream()
                    .filter(e -> sender.hasPermission(e.getValue().getPermission()))
                    .map(Map.Entry::getKey)
                    .filter(n -> n.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }

        SubCommand sub = subCommands.get(args[0].toLowerCase());
        if (sub == null) {
            return List.of();
        }
        return sub.tabComplete(sender, args);
    }
}