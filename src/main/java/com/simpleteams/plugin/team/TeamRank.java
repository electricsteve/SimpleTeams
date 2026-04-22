package com.simpleteams.plugin.team;

public enum TeamRank {

    MEMBER(1, "Member"),
    MODERATOR(2, "Moderator"),
    CO_LEADER(3, "Co-Leader"),
    LEADER(4, "Leader");

    private final int priority;
    private final String displayName;

    TeamRank(int priority, String displayName) {
        this.priority = priority;
        this.displayName = displayName;
    }

    public int getPriority() {
        return priority;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isAtLeast(TeamRank other) {
        return this.priority >= other.priority;
    }

    public TeamRank next() {
        TeamRank[] values = values();
        int idx = ordinal() + 1;
        if (idx >= values.length - 1) {
            return null;
        }
        return values[idx];
    }

    public TeamRank previous() {
        int idx = ordinal() - 1;
        if (idx < 0) {
            return null;
        }
        return values()[idx];
    }
}