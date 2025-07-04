package dev.fuzip.jobs;

import dev.fuzip.jobs.commands.JobsCommand;
import dev.fuzip.jobs.listeners.BlockBreakListener;
import dev.fuzip.jobs.listeners.OnJoinListener;
import dev.fuzip.jobs.managers.JobManager;
import dev.fuzip.jobs.managers.PlayerDataManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * The Jobs class serves as the main entry point for the Jobs plugin in a Bukkit/Spigot Minecraft
 * server. It extends the {@link JavaPlugin} class, implementing lifecycle methods to manage the
 * plugin's behavior during enablement and disablement.
 *
 * <p>When enabled, it registers event listeners, sets up command executors, and initializes
 * required configurations and resources. Upon disablement, it performs cleanup operations to ensure
 * proper resource handling.
 *
 * <p>Core responsibilities: - Initialize plugin configurations and resources such as config.yml and
 * data.yml. - Set up and manage interactions between {@link JobManager}, {@link PlayerDataManager},
 * and plugin commands. - Register relevant event listeners for player interactions and game
 * actions. - Provide the "jobs" command to interact with plugin functionality.
 */
public final class Jobs extends JavaPlugin {
  @Override
  public void onEnable() {
    this.getLogger().info("[Jobs] Initialising plugin...");

    // Load config & data
    saveDefaultConfig();
    this.saveResource("data.yml", false);

    // Init managers
    JobManager jobManager = new JobManager();
    PlayerDataManager playerDataManager = new PlayerDataManager(this, jobManager.getJobs());
    JobsCommand jobsCommand = new JobsCommand(jobManager, playerDataManager);

    // Register Events
    Bukkit.getPluginManager()
        .registerEvents(new BlockBreakListener(jobManager, playerDataManager), this);
    Bukkit.getPluginManager().registerEvents(new OnJoinListener(playerDataManager), this);

    // Commands
    this.getCommand("jobs").setExecutor(jobsCommand);

    this.getLogger().info("[Jobs] Enabled !");
  }

  @Override
  public void onDisable() {
    this.getLogger().info("[Jobs] Disabled !");
  }
}
