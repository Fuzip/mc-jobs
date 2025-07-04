package dev.fuzip.jobs.managers;

import dev.fuzip.jobs.entities.JobEntity;
import dev.fuzip.jobs.entities.PlayerEntity;
import org.bukkit.ChatColor;

/**
 * The LevelManager class provides utility methods for calculating experience points (XP) related to
 * player levels. This is primarily used to determine the total XP required to reach a specified
 * level based on predefined formulas.
 */
public class LevelManager {
  private final JobManager jobManager;

  public LevelManager(JobManager jobManager) {
    this.jobManager = jobManager;
  }

  /**
   * Calculates the total experience points (XP) required to reach a specific level.
   *
   * @param level the level for which the required XP is to be calculated. Should be a positive
   *     integer.
   * @return the total XP required to reach the specified level, or 0 if the level is less than 1.
   */
  public static int getXpForLevel(int level) {
    if (level >= 1 && level <= 16) {
      return (int) (Math.pow(level, 2) + 6 * level);
    }

    if (level >= 17 && level <= 31) {
      return (int) (2.5 * Math.pow(level, 2) - 40.5 * level + 360);
    }

    if (level >= 32) {
      return (int) (4.5 * Math.pow(level, 2) - 162.5 * level + 2220);
    }

    return 0;
  }

  /**
   * Adds experience points (XP) to a player for a specified job and checks if the player levels up.
   * Updates both the job-specific XP and total XP for the player. Sends a message to the player
   * indicating the amount of XP gained for the job.
   *
   * @param jobEntity the job to which the XP should be added
   * @param xpToAdd the amount of XP to be added to the player's job
   * @param playerEntity the player to whom the XP is added
   */
  public void addXpToPlayer(JobEntity jobEntity, int xpToAdd, PlayerEntity playerEntity) {
    playerEntity.addJobXp(jobEntity.getId(), xpToAdd);
    playerEntity.addJobTotalXp(jobEntity.getId(), xpToAdd);
    playerEntity
        .getPlayer()
        .sendMessage(
            jobEntity.getColor()
                + "["
                + jobEntity.getName()
                + "] "
                + ChatColor.WHITE
                + "Vous avez gagné "
                + xpToAdd
                + " XP !");

    this.checkLevelUp(jobEntity, playerEntity);
  }

  /**
   * Checks if the player has accumulated enough experience points (XP) in a specific job to level
   * up. If the player's XP meets or exceeds the threshold required for the next level, the player
   * levels up, reducing their current XP accordingly and calling {@code onLevelUp} to handle any
   * level-up logic (e.g., notifying the player, granting rewards).
   *
   * @param jobEntity the job associated with the experience points, containing job-specific
   *     information
   * @param playerEntity the player for whom the level-up check is performed, containing their XP
   *     and level details
   */
  private void checkLevelUp(JobEntity jobEntity, PlayerEntity playerEntity) {
    int maxXpLevel = LevelManager.getXpForLevel(playerEntity.getJobLevel(jobEntity.getId()));

    while (playerEntity.getJobXp(jobEntity.getId()) >= maxXpLevel) {
      playerEntity.setJobXp(
          jobEntity.getId(), playerEntity.getJobXp(jobEntity.getId()) - maxXpLevel);
      playerEntity.setJobLevel(jobEntity.getId(), playerEntity.getJobLevel(jobEntity.getId()) + 1);
      this.onLevelUp(jobEntity, playerEntity);
    }
  }

  /**
   * Handles the logic for a player's level-up event in a specific job. Sends a message to the
   * player notifying them of their new level and grants rewards based on their current level.
   *
   * @param jobEntity the job entity associated with the level-up event, containing job-specific
   *     details
   * @param playerEntity the player entity who leveled up, containing their current level and other
   *     job-related data
   */
  private void onLevelUp(JobEntity jobEntity, PlayerEntity playerEntity) {
    int currentLevel = playerEntity.getJobLevel(jobEntity.getId());
    playerEntity
        .getPlayer()
        .sendMessage(
            jobEntity.getColor()
                + "["
                + jobEntity.getName()
                + "] "
                + ChatColor.WHITE
                + "Vous êtes passé au niveau "
                + currentLevel
                + " !");

    this.jobManager.giveRewards(jobEntity, playerEntity);
  }
}
