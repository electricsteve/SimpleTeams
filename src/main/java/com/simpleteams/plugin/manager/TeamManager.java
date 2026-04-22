package com.simpleteams.plugin.manager;

import com.simpleteams.plugin.SimpleTeams;
import com.simpleteams.plugin.storage.Storage;
import com.simpleteams.plugin.team.Team;
import com.simpleteams.plugin.team.TeamInvite;
import com.simpleteams.plugin.team.TeamRank;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TeamManager {

    private final SimpleTeams plugin;
    private final Storage storage;

    private final Map<UUID, Team> teams = new ConcurrentHashMap<>();
    private final Map<UUID, UUID> playerTeamCache = new ConcurrentHashMap<>();
    private final Map<UUID, TeamInvite> pendingInvites = new ConcurrentHashMap<>();
    private final Set<UUID> teamChatEnabled = ConcurrentHashMap.newKeySet();

    public TeamManager(SimpleTeams plugin, Storage storage) {
        this.plugin = plugin;
        this.storage = storage;
        loadTeams();
    }

    private void loadTeams() {
        teams.putAll(storage.loadAllTeams());
        rebuildCache();
        plugin.getLogger().info("Loaded " + teams.size() + " teams.");
    }

    private void rebuildCache() {
        playerTeamCache.clear();
        for (Team team : teams.values()) {
            for (UUID member : team.getMembers().keySet()) {
                playerTeamCache.put(member, team.getId());
            }
        }
    }

    public void saveTeam(Team team) {
        storage.saveTeam(team);
    }

    public void saveAllTeams() {
        storage.saveAllTeams(teams);
    }

    public Team getTeam(UUID id) {
        return teams.get(id);
    }

    public Team getPlayerTeam(UUID playerId) {
        UUID teamId = playerTeamCache.get(playerId);
        return teamId != null ? teams.get(teamId) : null;
    }

    public boolean hasTeam(UUID playerId) {
        return playerTeamCache.containsKey(playerId);
    }

    public Set<Team> getAllTeams() {
        return new HashSet<>(teams.values());
    }

    public Collection<Team> getOpenTeams() {
        return teams.values().stream().filter(Team::isOpen).toList();
    }

    public Optional<Team> getTeamByName(String name) {
        return teams.values().stream()
                .filter(t -> t.getName().equalsIgnoreCase(name))
                .findFirst();
    }

    public Team createTeam(String name, UUID leader) {
        if (hasTeam(leader)) {
            return null;
        }
        if (teams.size() >= plugin.getConfigManager().getMaxTeams()) {
            return null;
        }

        Team team = new Team(name, leader);
        teams.put(team.getId(), team);
        playerTeamCache.put(leader, team.getId());
        storage.saveTeam(team);
        return team;
    }

    public void disbandTeam(UUID teamId) {
        Team team = teams.get(teamId);
        if (team == null) {
            return;
        }

        for (UUID member : team.getMembers().keySet()) {
            playerTeamCache.remove(member);
            teamChatEnabled.remove(member);
        }
        teams.remove(teamId);
        storage.deleteTeam(teamId);
    }

    public enum JoinResult {
        SUCCESS, ALREADY_IN_TEAM, BANNED, CLOSED, FULL
    }

    public JoinResult joinTeam(Player player, Team team) {
        if (hasTeam(player.getUniqueId())) {
            return JoinResult.ALREADY_IN_TEAM;
        }
        if (team.isBanned(player.getUniqueId())) {
            return JoinResult.BANNED;
        }
        if (!team.isOpen() && !player.hasPermission("simpleteams.admin.bypass")) {
            return JoinResult.CLOSED;
        }
        if (team.getMemberCount() >= plugin.getConfigManager().getMaxMembers()) {
            return JoinResult.FULL;
        }

        team.setMemberRank(player.getUniqueId(), TeamRank.MEMBER);
        playerTeamCache.put(player.getUniqueId(), team.getId());
        storage.saveTeam(team);
        return JoinResult.SUCCESS;
    }

    public void leaveTeam(Player player) {
        Team team = getPlayerTeam(player.getUniqueId());
        if (team == null) {
            return;
        }

        if (team.isLeader(player.getUniqueId())) {
            disbandTeam(team.getId());
        } else {
            team.removeMember(player.getUniqueId());
            playerTeamCache.remove(player.getUniqueId());
            teamChatEnabled.remove(player.getUniqueId());
            storage.saveTeam(team);
        }
    }

    public void kickMember(UUID targetId, Team team, UUID kickerId) {
        team.removeMember(targetId);
        playerTeamCache.remove(targetId);
        teamChatEnabled.remove(targetId);
        storage.saveTeam(team);
    }

    public void banMember(UUID targetId, Team team) {
        team.banPlayer(targetId);
        playerTeamCache.remove(targetId);
        teamChatEnabled.remove(targetId);
        storage.saveTeam(team);
    }

    public void unbanMember(UUID targetId, Team team) {
        team.unbanPlayer(targetId);
        storage.saveTeam(team);
    }

    public void sendInvite(UUID invitee, UUID teamId, UUID inviter) {
        int expirySeconds = plugin.getConfigManager().getInviteExpireSeconds();
        pendingInvites.put(invitee, new TeamInvite(teamId, inviter, expirySeconds));

        long delayTicks = (long) expirySeconds * 20L;
        plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, () -> {
            TeamInvite invite = pendingInvites.get(invitee);
            if (invite != null && invite.isExpired()) {
                pendingInvites.remove(invitee);
                Player p = plugin.getServer().getPlayer(invitee);
                if (p != null) {
                    Team team = teams.get(invite.getTeamId());
                    String teamName = team != null ? team.getName() : "?";
                    plugin.getMessageManager().send(p, "inviteExpired", "team", teamName);
                }
            }
        }, delayTicks);
    }

    public TeamInvite getPendingInvite(UUID invitee) {
        TeamInvite invite = pendingInvites.get(invitee);
        if (invite == null) {
            return null;
        }
        if (invite.isExpired()) {
            pendingInvites.remove(invitee);
            return null;
        }
        return invite;
    }

    public void clearInvite(UUID invitee) {
        pendingInvites.remove(invitee);
    }

    public boolean hasOutgoingInvite(UUID teamId, UUID invitee) {
        TeamInvite invite = pendingInvites.get(invitee);
        return invite != null && invite.getTeamId().equals(teamId) && !invite.isExpired();
    }

    public boolean isTeamChatEnabled(UUID playerId) {
        return teamChatEnabled.contains(playerId);
    }

    public boolean toggleTeamChat(UUID playerId) {
        if (teamChatEnabled.contains(playerId)) {
            teamChatEnabled.remove(playerId);
            return false;
        } else {
            teamChatEnabled.add(playerId);
            return true;
        }
    }

    public void disableTeamChat(UUID playerId) {
        teamChatEnabled.remove(playerId);
    }
}