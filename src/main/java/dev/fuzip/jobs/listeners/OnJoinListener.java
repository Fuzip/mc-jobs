package dev.fuzip.jobs.listeners;

import dev.fuzip.jobs.managers.PlayerDataManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class OnJoinListener implements Listener {

  private final PlayerDataManager playerDataManager;

  public OnJoinListener(PlayerDataManager playerDataManager) {
    this.playerDataManager = playerDataManager;
  }

  @EventHandler
  public void onJoin(PlayerJoinEvent event) {
    playerDataManager.initDataPlayer(event.getPlayer());
  }
}
