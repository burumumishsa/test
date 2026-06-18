package com.brumisharuma.clashsmp;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class CommandLogger extends JavaPlugin implements Listener {

    private FileConfiguration cfg;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        cfg = getConfig();

        Bukkit.getPluginManager().registerEvents(this, this);

        sendStatusWebhook(
                cfg.getString("webhooks.admin"),
                "🟢 Connected to the server",
                "*Listening for commands...*",
                65280
        );
    }

    @Override
    public void onDisable() {
        sendStatusWebhook(
                cfg.getString("webhooks.admin"),
                "🔴 Server Stopped",
                "*Stopped listening for commands...*",
                16711680
        );
    }

    // ---------------- COMMAND LOGGER ----------------

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {

        Player p = e.getPlayer();

        String raw = e.getMessage().toLowerCase();
        String withoutSlash = raw.startsWith("/") ? raw.substring(1) : raw;

        String[] split = withoutSlash.split(" ");

        String cmd = split[0];
        String args = "";

        if (split.length > 1) {
            args = withoutSlash.substring(cmd.length() + 1);
        }

        String webhook = getWebhook(cmd);
        int color = getColor(cmd);

        Location l = p.getLocation();

        String time = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        String json =
                "{"
                        + "\"embeds\":[{"
                        + "\"title\":\"" + escape(p.getName() + " executed") + "\","
                        + "\"description\":\"/" + escape(cmd + (args.isEmpty() ? "" : " " + args)) + "\","
                        + "\"color\":" + color + ","
                        + "\"footer\":{\"text\":\"Time: " + time +
                        " | Coords: " + (int) l.getX() + "x " +
                        (int) l.getY() + "y " +
                        (int) l.getZ() + "z\"}"
                        + "}]"
                        + "}";

        sendWebhook(webhook, json);
    }

    // ---------------- RELOAD COMMAND ----------------

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (command.getName().equalsIgnoreCase("clreload")) {

            if (!sender.hasPermission("commandlogger.reload")) {
                sender.sendMessage("§cNo permission.");
                return true;
            }

            reloadConfig();
            cfg = getConfig();

            sender.sendMessage("§aCommandLogger config reloaded.");
            return true;
        }

        return false;
    }

    // ---------------- WEBHOOK ROUTING ----------------

    private String getWebhook(String cmd) {

        List<String> admin = cfg.getStringList("commands.admin");
        List<String> player = cfg.getStringList("commands.player");
        List<String> other = cfg.getStringList("commands.other");

        if (admin.contains(cmd)) return cfg.getString("webhooks.admin");
        if (player.contains(cmd)) return cfg.getString("webhooks.player");
        if (other.contains(cmd)) return cfg.getString("webhooks.other");

        return cfg.getString("webhooks.fallback");
    }

    private int getColor(String cmd) {

        List<String> admin = cfg.getStringList("commands.admin");
        List<String> player = cfg.getStringList("commands.player");
        List<String> other = cfg.getStringList("commands.other");

        if (admin.contains(cmd)) return 16711680; // RED
        if (player.contains(cmd)) return 65280;   // GREEN
        if (other.contains(cmd)) return 255;      // BLUE

        return 8421504; // GREY
    }

    // ---------------- STATUS EMBEDS ----------------

    private void sendStatusWebhook(String webhook, String title, String body, int color) {
        try {
            String time = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            String json =
                    "{"
                            + "\"embeds\":[{"
                            + "\"title\":\"" + escape(title) + "\","
                            + "\"description\":\"" + escape(body) + "\","
                            + "\"color\":" + color + ","
                            + "\"footer\":{\"text\":\"" + time + "\"}"
                            + "}]"
                            + "}";

            sendWebhook(webhook, json);

        } catch (Exception ignored) {}
    }

    // ---------------- HTTP ----------------

    private void sendWebhook(String webhook, String json) {
        try {
            URL url = new URL(webhook);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");

            OutputStream os = conn.getOutputStream();
            os.write(json.getBytes(StandardCharsets.UTF_8));
            os.flush();
            os.close();

            conn.getInputStream().close();

        } catch (Exception ignored) {}
    }

    private String escape(String s) {
        return s.replace("\"", "\\\"");
    }
}