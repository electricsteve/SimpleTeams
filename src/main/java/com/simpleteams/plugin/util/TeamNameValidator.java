package com.simpleteams.plugin.util;

import com.simpleteams.plugin.SimpleTeams;

import java.util.regex.Pattern;

public class TeamNameValidator {

    public enum ValidationResult {
        OK,
        TOO_SHORT,
        TOO_LONG,
        INVALID_CHARS,
        BLACKLISTED
    }

    private final SimpleTeams plugin;

    public TeamNameValidator(SimpleTeams plugin) {
        this.plugin = plugin;
    }

    public ValidationResult validate(String name) {
        int min = plugin.getConfigManager().getMinNameLength();
        int max = plugin.getConfigManager().getMaxNameLength();

        if (name.length() < min) {
            return ValidationResult.TOO_SHORT;
        }
        if (name.length() > max) {
            return ValidationResult.TOO_LONG;
        }

        String allowedChars = plugin.getConfigManager().getAllowedNameChars();
        if (!Pattern.matches("[" + allowedChars + "]+", name)) {
            return ValidationResult.INVALID_CHARS;
        }

        for (String blacklisted : plugin.getConfigManager().getBlacklistedNames()) {
            if (name.equalsIgnoreCase(blacklisted)) {
                return ValidationResult.BLACKLISTED;
            }
        }

        return ValidationResult.OK;
    }
}