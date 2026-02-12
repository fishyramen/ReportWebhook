package com.example.reportwebhook;

import org.bukkit.entity.Player;

import java.time.Instant;

/**
 * Builds a Discord-style webhook payload using embeds.
 * NOTE: This intentionally does NOT include x/y/z coordinates.
 */
final class ReportPayload {

    private final String json;

    private ReportPayload(String json) {
        this.json = json;
    }

    String toJson() {
        return json;
    }

    static ReportPayload create(
            Player reporter,
            Player reported,
            String reason,
            String worldName,
            String serverName,
            Instant utcTime,
            boolean includeUuids,
            int embedColor,
            String webhookUsername,
            String webhookAvatarUrl
    ) {
        String reporterValue = includeUuids
                ? reporter.getName() + " (" + reporter.getUniqueId() + ")"
                : reporter.getName();

        String reportedValue = includeUuids
                ? reported.getName() + " (" + reported.getUniqueId() + ")"
                : reported.getName();

        String timeValue = utcTime.toString();

        StringBuilder sb = new StringBuilder(512);
        sb.append('{');

        boolean hasTopLevel = false;

        if (webhookUsername != null && !webhookUsername.isBlank()) {
            sb.append("\"username\":\"").append(escapeJson(webhookUsername)).append("\"");
            hasTopLevel = true;
        }

        if (webhookAvatarUrl != null && !webhookAvatarUrl.isBlank()) {
            if (hasTopLevel) sb.append(',');
            sb.append("\"avatar_url\":\"").append(escapeJson(webhookAvatarUrl)).append("\"");
            hasTopLevel = true;
        }

        if (hasTopLevel) sb.append(',');

        // embeds
        sb.append("\"embeds\":[{");
        sb.append("\"title\":\"\uD83D\uDEA8 New Player Report\",");
        sb.append("\"color\":").append(embedColor).append(',');

        sb.append("\"fields\":[");
        appendField(sb, "Reporter", reporterValue, true);
        sb.append(',');
        appendField(sb, "Reported", reportedValue, true);
        sb.append(',');
        appendField(sb, "Reason", reason, false);
        sb.append(',');
        appendField(sb, "Server", serverName, true);
        sb.append(',');
        appendField(sb, "World", worldName, true);
        sb.append(',');
        appendField(sb, "Time (UTC)", timeValue, true);
        sb.append(']');

        sb.append(",\"footer\":{\"text\":\"ReportWebhook\"}");
        sb.append("}]}");

        return new ReportPayload(sb.toString());
    }

    private static void appendField(StringBuilder sb, String name, String value, boolean inline) {
        sb.append('{');
        sb.append("\"name\":\"").append(escapeJson(name)).append("\",");
        sb.append("\"value\":\"").append(escapeJson(value)).append("\",");
        sb.append("\"inline\":").append(inline);
        sb.append('}');
    }

    private static String escapeJson(String s) {
        if (s == null) return "";
        StringBuilder out = new StringBuilder(s.length() + 16);
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '"' -> out.append("\\\"");
                case '\\' -> out.append("\\\\");
                case '\b' -> out.append("\\b");
                case '\f' -> out.append("\\f");
                case '\n' -> out.append("\\n");
                case '\r' -> out.append("\\r");
                case '\t' -> out.append("\\t");
                default -> {
                    if (c < 0x20) {
                        out.append(String.format("\\u%04x", (int) c));
                    } else {
                        out.append(c);
                    }
                }
            }
        }
        return out.toString();
    }
}
