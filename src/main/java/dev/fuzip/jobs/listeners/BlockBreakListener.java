package dev.fuzip.jobs.listeners;

import dev.fuzip.jobs.entities.JobEntity;
import dev.fuzip.jobs.entities.PlayerEntity;
import dev.fuzip.jobs.managers.JobManager;
import dev.fuzip.jobs.managers.LevelManager;
import dev.fuzip.jobs.managers.PlayerDataManager;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakListener implements Listener {

  private final JobManager jobManager;
  private final PlayerDataManager playerDataManager;
  private final LevelManager levelManager;

  public BlockBreakListener(JobManager jobManager, PlayerDataManager playerDataManager) {
    this.jobManager = jobManager;
    this.playerDataManager = playerDataManager;
    this.levelManager = new LevelManager(jobManager);
  }

  @EventHandler
  public void onBlockBreak(BlockBreakEvent event) {
    PlayerEntity playerEntity = playerDataManager.getPlayerEntity(event.getPlayer());
    Material blockType = event.getBlock().getType();

    for (JobEntity jobEntity : this.jobManager.getJobs().values()) {
      int xp = jobManager.getXpForAction(jobEntity.getId(), "break", blockType);
      if (xp > 0) {
        levelManager.addXpToPlayer(jobEntity, xp, playerEntity);
        playerDataManager.savePlayerEntity(playerEntity);
      }
    }
  }
}
