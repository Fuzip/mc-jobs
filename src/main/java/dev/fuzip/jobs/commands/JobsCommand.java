package dev.fuzip.jobs.commands;

import dev.fuzip.jobs.entities.PlayerEntity;
import dev.fuzip.jobs.managers.JobManager;
import dev.fuzip.jobs.managers.LevelManager;
import dev.fuzip.jobs.managers.PlayerDataManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class JobsCommand implements CommandExecutor {

  private final JobManager jobManager;
  private final PlayerDataManager playerDataManager;

  public JobsCommand(JobManager jobManager, PlayerDataManager playerDataManager) {
    this.jobManager = jobManager;
    this.playerDataManager = playerDataManager;
  }

  @Override
  public boolean onCommand(
      @NotNull CommandSender sender,
      @NotNull Command command,
      @NotNull String label,
      @NotNull String[] args) {
    if (!(sender instanceof Player player)) {
      sender.sendMessage("Cette commande doit être exécutée par un joueur.");
      return true;
    }

    // /jobs
    if (args.length == 0) {
      handlePlayerJobsInfo(player, player.getName());
      return true;
    }

    // /jobs <username>
    if (args.length == 1) {
      handlePlayerJobsInfo(player, args[0]);
      return true;
    }

    // Bad command usage
    player.sendMessage(ChatColor.RED + "Usage incorrect. Veuillez vérifier la commande.");
    return true;
  }

  private void handlePlayerJobsInfo(Player requester, String username) {
    Player target = Bukkit.getPlayer(username);

    if (target == null) {
      requester.sendMessage(ChatColor.RED + "Le joueur " + username + " n'est pas en ligne.");
      return;
    }

    PlayerEntity playerData = this.playerDataManager.getPlayerEntity(target);

    requester.sendMessage("Métiers de " + target.getName() + " :");

    jobManager
        .getJobs()
        .forEach(
            (jobId, jobEntity) -> {
              int level = playerData.getJobLevel(jobEntity.getId());
              int currentXp = playerData.getJobXp(jobEntity.getId());
              int nextLevelXp = LevelManager.getXpForLevel(level + 1);

              requester.sendMessage(
                  ChatColor.BLUE
                      + jobEntity.getName()
                      + ChatColor.WHITE
                      + " - Niveau "
                      + level
                      + " ("
                      + currentXp
                      + "/"
                      + nextLevelXp
                      + " XP)");
            });
  }
}
