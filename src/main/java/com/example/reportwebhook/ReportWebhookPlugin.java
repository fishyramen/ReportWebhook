package com.example.reportwebhook;

import org.bukkit.plugin.java.JavaPlugin;

public final class ReportWebhookPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();

        if (getCommand("report") != null) {
            getCommand("report").setExecutor(new ReportCommand(this));
        } else {
            getLogger().warning("Command 'report' not found. Check plugin.yml");
        }
    }
}
