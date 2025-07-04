package dev.fuzip.jobs.managers;

import dev.fuzip.jobs.Jobs;
import dev.fuzip.jobs.entities.JobEntity;
import dev.fuzip.jobs.entities.JobRewardEntity;
import dev.fuzip.jobs.entities.JobXpEntity;
import dev.fuzip.jobs.entities.PlayerEntity;
import java.util.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

/**
 * Manages jobs, including their configurations, actions, rewards, and associated processing logic.
 * This class facilitates interaction between the plugin, the job definitions, and the players
 * performing job-related actions.
 */
public class JobManager {
  private final Jobs plugin;
  private final Map<String, JobEntity> jobs;

  public JobManager() {
    this.plugin = (Jobs) Bukkit.getPluginManager().getPlugin("Jobs");
    this.jobs = new HashMap<>();

    if (this.plugin == null) {
      throw new RuntimeException("Plugin Jobs not found!");
    }

    this.loadJobsConfig();
    this.plugin.getLogger().info("[Jobs] Loaded " + jobs.size() + " jobs.");
  }

  /**
   * Retrieves the mapping of job identifiers to their corresponding {@link JobEntity} objects.
   *
   * @return a map where the key is the job ID (as a {@code String}) and the value is the {@link
   *     JobEntity} representing the details of the job, including actions and rewards.
   */
  public Map<String, JobEntity> getJobs() {
    return jobs;
  }

  /**
   * Retrieves the XP value associated with a specific action for a specified job and material. If
   * the job is not found or no matching action and material combination exists, returns 0.
   *
   * @param job the name of the job for which the XP is being retrieved
   * @param action the action performed within the specified job
   * @param material the material associated with the action
   * @return the XP associated with the given job, action, and material combination, or 0 if no
   *     matching entry is found
   */
  public int getXpForAction(String job, String action, Material material) {
    JobEntity jobEntity = this.jobs.get(job);

    if (jobEntity == null) {
      return 0;
    }

    for (Map.Entry<String, List<JobXpEntity>> entry : jobEntity.getActionsXpMap().entrySet()) {
      if (!Objects.equals(entry.getKey(), action)) {
        continue;
      }

      for (JobXpEntity jobXpEntity : entry.getValue()) {
        if (jobXpEntity.getMaterial() == material) {
          return jobXpEntity.getXp();
        }
      }
    }

    return 0;
  }

  /**
   * Gives job rewards to a player based on their current job level. Checks reward milestones (e.g.,
   * every 10 levels, every 50 levels) and provides the corresponding rewards if the player's level
   * matches these milestones.
   *
   * @param jobEntity the job entity for which to check rewards
   * @param playerEntity the player entity containing the job level information
   */
  public void giveRewards(JobEntity jobEntity, PlayerEntity playerEntity) {
    int currentLevel = playerEntity.getJobLevel(jobEntity.getId());
    List<JobRewardEntity> playerRewards = new ArrayList<>();

    for (Map.Entry<Integer, JobRewardEntity> entry : jobEntity.getRewardsMap().entrySet()) {
      int rewardLevel = entry.getKey();
      if (currentLevel % rewardLevel == 0) {
        playerRewards.add(entry.getValue());
      }
    }

    for (JobRewardEntity reward : playerRewards) {
      ItemStack rewardItem = new ItemStack(reward.getMaterial(), reward.getQuantity());
      playerEntity.getPlayer().getInventory().addItem(rewardItem);
      playerEntity
          .getPlayer()
          .sendMessage(
              jobEntity.getColor()
                  + "["
                  + jobEntity.getName()
                  + "]"
                  + ChatColor.WHITE
                  + " Vous avez reçu x"
                  + rewardItem.getAmount()
                  + " "
                  + rewardItem.getType().name()
                  + " !");
    }
  }

  /**
   * Loads the job configuration from the plugin's config.yml file into memory.
   *
   * <p>This method reads job definitions under the "jobs" section in the configuration file and
   * initializes `JobEntity` objects for each job, mapping their actions and rewards accordingly.
   *
   * <p>Actions and rewards for each job are loaded using the helper methods `loadJobActions` and
   * `loadJobRewards`. If no job configuration is found, the method disables the plugin and logs an
   * appropriate message.
   *
   * <p>Key steps performed by this method: - Retrieve the "jobs" configuration section. - For each
   * job ID, create a `JobEntity` object with its ID and name. - Populate the `actionsXpMap` of the
   * `JobEntity` using `loadJobActions`. - Populate the `rewardsMap` of the `JobEntity` using
   * `loadJobRewards`. - Store the initialized `JobEntity` in the internal `jobs` map.
   *
   * <p>This method ensures that jobs are properly registered in memory for further use and
   * validates the presence of required configuration sections.
   */
  private void loadJobsConfig() {
    ConfigurationSection jobsSection = this.plugin.getConfig().getConfigurationSection("jobs");

    if (jobsSection == null) {
      this.plugin
          .getLogger()
          .info(Color.RED + "[Jobs] No jobs found in config.yml! Disabling plugin...");
      Bukkit.getPluginManager().disablePlugin(plugin);
      return;
    }

    for (String jobId : jobsSection.getKeys(false)) {
      JobEntity jobEntity =
          new JobEntity(
              jobId,
              jobsSection.getString(jobId + ".name"),
              jobsSection.getString(jobId + ".color"));

      ConfigurationSection actionsSection = jobsSection.getConfigurationSection(jobId + ".xp");

      if (actionsSection != null) {
        jobEntity.getActionsXpMap().putAll(this.loadJobActions(jobId, actionsSection));
      }

      ConfigurationSection rewardsSection = jobsSection.getConfigurationSection(jobId + ".rewards");
      if (rewardsSection != null) {
        jobEntity.getRewardsMap().putAll(this.loadJobRewards(jobId, rewardsSection));
      }

      this.jobs.put(jobId, jobEntity);
    }
  }

  /**
   * Loads job action XP mappings from the given configuration section. This method processes the
   * actions and their associated XP values for a specific job based on the data provided in the
   * configuration section.
   *
   * @param job the job ID for which actions are being loaded
   * @param actionsSection the configuration section containing the action definitions and XP values
   * @return a map where each key is an action name, and the value is a JobXpEntity representing the
   *     material and associated XP
   */
  private Map<String, List<JobXpEntity>> loadJobActions(
      String job, ConfigurationSection actionsSection) {
    Map<String, List<JobXpEntity>> jobXpMap = new HashMap<>();

    for (String action : actionsSection.getKeys(false)) {
      ConfigurationSection materialSection = actionsSection.getConfigurationSection(action);

      if (materialSection == null) {
        this.plugin
            .getLogger()
            .info(
                Color.YELLOW
                    + "[Jobs] No materials found for job "
                    + job
                    + " action "
                    + action
                    + " in config.yml!");
        continue;
      }

      List<JobXpEntity> jobXpEntities = new ArrayList<>();

      for (String materialKey : materialSection.getKeys(false)) {
        Material material = Material.matchMaterial(materialKey.toUpperCase());
        int materialXp = materialSection.getInt(materialKey);

        jobXpEntities.add(new JobXpEntity(material, materialXp));
      }

      jobXpMap.put(action, jobXpEntities);
    }

    return jobXpMap;
  }

  /**
   * Loads job reward mappings from the given configuration section. This method processes the
   * reward definitions for a specific job based on the data provided under the configuration
   * section and returns a map of rewards corresponding to specific levels.
   *
   * @param job the job ID for which rewards are being loaded
   * @param rewardsSection the configuration section containing the reward definitions, where keys
   *     represent levels and values represent reward data
   * @return a map where each key is a job level (as an Integer), and the value is a JobRewardEntity
   *     containing the material and quantity for the reward
   */
  private Map<Integer, JobRewardEntity> loadJobRewards(
      String job, ConfigurationSection rewardsSection) {
    Map<Integer, JobRewardEntity> rewardsMap = new HashMap<>();

    for (String levelKey : rewardsSection.getKeys(false)) {
      int level;
      try {
        level = Integer.parseInt(levelKey.replace("every_", "").replace("_levels", ""));
      } catch (NumberFormatException e) {
        this.plugin
            .getLogger()
            .info(
                Color.RED
                    + "[Jobs] Invalid level format in rewards for job "
                    + job
                    + ": "
                    + levelKey);
        Bukkit.getPluginManager().disablePlugin(this.plugin);
        continue;
      }

      for (Map<?, ?> item : rewardsSection.getMapList(levelKey + ".item")) {
        Material material = Material.matchMaterial(((String) item.get("name")).toUpperCase());
        int quantity = (int) item.get("quantity");

        rewardsMap.put(level, new JobRewardEntity(material, quantity));
      }
    }

    return rewardsMap;
  }
}
