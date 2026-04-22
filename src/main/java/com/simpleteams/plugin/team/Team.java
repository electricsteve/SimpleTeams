package com.simpleteams.plugin.team;

import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class Team {

    private final UUID id;
    private String name;
    private String prefix;
    private final Map<UUID, TeamRank> members;
    private final Set<UUID> bannedPlayers;
    private boolean open;
    private long createdAt;

    public Team(String name, UUID leader) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.prefix = "<dark_gray>[<white>" + name + "</white>]</dark_gray> ";
        this.members = new HashMap<>();
        this.bannedPlayers = new HashSet<>();
        this.open = false;
        this.createdAt = System.currentTimeMillis();
        this.members.put(leader, TeamRank.LEADER);
    }

    public Team(UUID id, ConfigurationSection section) {
        this.id = id;
        this.name = section.getString("name", "Unknown");
        this.prefix = section.getString("prefix", "<dark_gray>[<white>" + name + "</white>]</dark_gray> ");
        this.open = section.getBoolean("open", false);
        this.createdAt = section.getLong("createdAt", System.currentTimeMillis());
        this.members = new HashMap<>();
        this.bannedPlayers = new HashSet<>();

        ConfigurationSection membersSection = section.getConfigurationSection("members");
        if (membersSection != null) {
            for (String uuidStr : membersSection.getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(uuidStr);
                    TeamRank rank = TeamRank.valueOf(membersSection.getString(uuidStr, "MEMBER"));
                    members.put(uuid, rank);
                } catch (Exception ignored) {
                }
            }
        }

        for (String uuidStr : section.getStringList("banned")) {
            try {
                bannedPlayers.add(UUID.fromString(uuidStr));
            } catch (Exception ignored) {
            }
        }
    }

    public void save(ConfigurationSection section) {
        section.set("name", name);
        section.set("prefix", prefix);
        section.set("open", open);
        section.set("createdAt", createdAt);

        ConfigurationSection membersSection = section.createSection("members");
        for (Map.Entry<UUID, TeamRank> entry : members.entrySet()) {
            membersSection.set(entry.getKey().toString(), entry.getValue().name());
        }
        section.set("banned", bannedPlayers.stream().map(UUID::toString).toList());
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public Map<UUID, TeamRank> getMembers() {
        return new HashMap<>(members);
    }

    public boolean hasMember(UUID uuid) {
        return members.containsKey(uuid);
    }

    public TeamRank getMemberRank(UUID uuid) {
        return members.get(uuid);
    }

    public void setMemberRank(UUID uuid, TeamRank rank) {
        members.put(uuid, rank);
    }

    public void removeMember(UUID uuid) {
        members.remove(uuid);
    }

    public int getMemberCount() {
        return members.size();
    }

    public boolean isBanned(UUID uuid) {
        return bannedPlayers.contains(uuid);
    }

    public void banPlayer(UUID uuid) {
        bannedPlayers.add(uuid);
        members.remove(uuid);
    }

    public void unbanPlayer(UUID uuid) {
        bannedPlayers.remove(uuid);
    }

    public Set<UUID> getBannedPlayers() {
        return new HashSet<>(bannedPlayers);
    }

    public boolean isLeader(UUID uuid) {
        return members.get(uuid) == TeamRank.LEADER;
    }

    public UUID getLeader() {
        return members.entrySet().stream()
                .filter(e -> e.getValue() == TeamRank.LEADER)
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }
}