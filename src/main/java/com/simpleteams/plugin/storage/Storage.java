package com.simpleteams.plugin.storage;

import com.simpleteams.plugin.team.Team;

import java.util.Map;
import java.util.UUID;

public interface Storage {
    void saveTeam(Team team);
    void deleteTeam(UUID teamId);
    Map<UUID, Team> loadAllTeams();
    void saveAllTeams(Map<UUID, Team> teams);
}