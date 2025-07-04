package dev.fuzip.jobs.managers;

import dev.fuzip.jobs.Jobs;
import dev.fuzip.jobs.entities.JobEntity;
import dev.fuzip.jobs.entities.PlayerEntity;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

/**
 * The PlayerDataManager class is responsible for managing player-related data in the "Jobs" plugin.
 * It handles the initialization, loading, saving, and updating of player data stored in a YAML
 * file. Each player's data includes information about jobs, XP, and levels.
 *
 * <p>Core Responsibilities: - Initialize new player data if not already present. - Load player data
 * from the stored data file. - Save updated player data back to the file. - Manage data for
 * multiple jobs per player, including XP and levels.
 *
 * <p>Dependencies: - Uses the Bukkit API for player management. - Relies on the YamlConfiguration
 * and FileConfiguration classes to handle YAML-based data storage.
 */
public class PlayerDataManager {

  private final Jobs plugin;
  private final Map<String, JobEntity> jobs;
  private final File dataFile;
  private final FileConfiguration dataConfig;

  public PlayerDataManager(Jobs plugin, Map<String, JobEntity> jobs) {
    this.plugin = plugin;
    this.jobs = jobs;
    this.dataFile = new File(plugin.getDataFolder(), "data.yml");
    this.dataConfig = YamlConfiguration.loadConfiguration(dataFile);
  }

  /**
   * Retrieves the player data for the given player by loading it from persistent storage.
   *
   * @param player the {@code Player} object representing the player whose data is being retrieved
   * @return a {@code PlayerEntity} object containing the player's data, including job XP and levels
   */
  public PlayerEntity getPlayerEntity(Player player) {
    return this.loadPlayerEntity(player);
  }

  /**
   * Saves the player data, including job XP and levels, to persistent storage.
   *
   * @param playerData the {@code PlayerEntity} object representing the player whose data needs to
   *     be saved
   */
  public void savePlayerEntity(PlayerEntity playerData) {
    UUID uuid = playerData.getPlayer().getUniqueId();
    ConfigurationSection playerSection = dataConfig.createSection("players." + uuid);

    Map<String, Integer> jobsXp = playerData.getAllJobsXp();
    Map<String, Integer> jobsTotalXp = playerData.getJobxTotalXpMap();
    Map<String, Integer> jobsLevels = playerData.getAllJobsLevels();

    for (JobEntity jobEntity : this.jobs.values()) {
      ConfigurationSection jobSection = playerSection.createSection(jobEntity.getId());

      jobSection.set("xp", jobsXp.getOrDefault(jobEntity.getId(), 0));
      jobSection.set("totalXp", jobsTotalXp.getOrDefault(jobEntity.getId(), 0));
      jobSection.set("level", jobsLevels.getOrDefault(jobEntity.getId(), 1));
    }

    try {
      dataConfig.save(dataFile);
    } catch (IOException e) {
      plugin
          .getLogger()
          .severe("[Jobs] Unable to save player data for " + uuid + ": " + e.getMessage());
    }
  }

  /**
   * Loads the player data from a configuration section, including the job XP and levels, and
   * initializes a {@code PlayerEntity} object for the specified player.
   *
   * @param player the {@code Player} object representing the player whose data is being loaded
   * @return a {@code PlayerEntity} object containing the loaded player data, including their job XP
   *     and levels
   */
  private PlayerEntity loadPlayerEntity(Player player) {
    UUID uuid = player.getUniqueId();
    ConfigurationSection playerSection = dataConfig.getConfigurationSection("players." + uuid);

    PlayerEntity playerData = new PlayerEntity(player);

    if (playerSection == null) {
      return playerData;
    }

    for (String jobId : playerSection.getKeys(false)) {
      ConfigurationSection jobSection = playerSection.getConfigurationSection(jobId);
      if (jobSection != null) {
        int xp = jobSection.getInt("xp", 0);
        int totalXp = jobSection.getInt("totalXp", 0);
        int level = jobSection.getInt("level", 1);
        playerData.setJobXp(jobId, xp);
        playerData.setJobTotalXp(jobId, totalXp);
        playerData.setJobLevel(jobId, level);
      }
    }

    return playerData;
  }

  /**
   * Initializes player data by checking if existing data is available and creating new data if
   * necessary. If the player's data does not exist in the configuration, it initializes and saves
   * it.
   *
   * @param player the {@code Player} object representing the player whose data needs to be
   *     initialized
   */
  public void initDataPlayer(Player player) {
    UUID uuid = player.getUniqueId();
    ConfigurationSection playerSection = dataConfig.getConfigurationSection("players." + uuid);

    if (playerSection == null) {
      Bukkit.getLogger().info("[Jobs] Initialize data for " + player.getName());
      savePlayerEntity(new PlayerEntity(player));
    }
  }
}
