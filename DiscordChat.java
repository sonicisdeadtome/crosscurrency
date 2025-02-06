package com.yourplugin.discordchat;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class DiscordChat extends JavaPlugin implements Listener {

    private String webhookUrl;

    @Override
    public void onEnable() {
        saveDefaultConfig(); // Saves config.yml if it doesn't exist presently 
        webhookUrl = getConfig().getString("webhook-url", "");
        
        if (webhookUrl.isEmpty()) {
            getLogger().warning("ERROR NO WEBHOOK - ADD WEBHOOK");
        } else {
            getLogger().info("Webhook loaded successfully.");
        }

        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        String playerName = event.getPlayer().getName();
        String message = event.getMessage();
        sendToDiscord(playerName, message);
    }

    private void sendToDiscord(String playerName, String message) {
        if (webhookUrl.isEmpty()) return; // Don't send if no webhook is added presently

        try {
            URL url = new URL(webhookUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            JSONObject json = new JSONObject();
            json.put("username", playerName);
            json.put("content", message);

            String jsonString = json.toJSONString();
            byte[] output = jsonString.getBytes(StandardCharsets.UTF_8);

            OutputStream os = connection.getOutputStream();
            os.write(output);
            os.flush();
            os.close();

            connection.getResponseCode(); // Send the request
        } catch (Exception e) {
            getLogger().warning("ERROR Failed to send to Discord: " + e.getMessage());
        }
    }
}