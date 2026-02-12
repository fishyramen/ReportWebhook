package com.example.reportwebhook;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.Arrays;

public final class ReportCommand implements CommandExecutor {

    private final ReportWebhookPlugin plugin;
    private final WebhookClient webhookClient;

    public ReportCommand(ReportWebhookPlugin plugin) {
        this.plugin = plugin;
        this.webhookClient = new WebhookClient(plugin);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player reporter)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /report <player> <reason>");
            return true;
        }

        Player reported = Bukkit.getPlayerExact(args[0]);
        if (reported == null) {
            sender.sendMessage(ChatColor.RED + "That player is not online.");
            return true;
        }

        String reason = String.join(" ", Arrays.copyOfRange(args, 1, args.length)).trim();
        if (reason.isEmpty()) {
            sender.sendMessage(ChatColor.RED + "Please provide a reason.");
            return true;
        }

        String webhookUrl = plugin.getConfig().getString("webhook_url", "").trim();
        if (webhookUrl.isEmpty()) {
            sender.sendMessage(ChatColor.RED + "Webhook URL is not set. Ask an admin to set it in plugins/ReportWebhook/config.yml");
            return true;
        }

        boolean includeUuids = plugin.getConfig().getBoolean("include_uuids", false);
        int color = plugin.getConfig().getInt("embed_color", 16711680);
        String usernameOverride = plugin.getConfig().getString("webhook_username", "").trim();
        String avatarUrl = plugin.getConfig().getString("webhook_avatar_url", "").trim();

        ReportPayload payload = ReportPayload.create(
                reporter,
                reported,
                reason,
                reporter.getWorld().getName(),
                Bukkit.getServer().getName(),
                Instant.now(),
                includeUuids,
                color,
                usernameOverride.isEmpty() ? null : usernameOverride,
                avatarUrl.isEmpty() ? null : avatarUrl
        );

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                webhookClient.send(webhookUrl, payload);
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to send webhook report: " + e.getMessage());
            }
        });

        sender.sendMessage(ChatColor.GREEN + "Report sent.");
        return true;
    }
}
