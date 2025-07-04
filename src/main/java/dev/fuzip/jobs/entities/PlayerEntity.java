package dev.fuzip.jobs.entities;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.entity.Player;

public class PlayerEntity {
  private final Player player;
  private final Map<String, Integer> jobsXpMap;
  private final Map<String, Integer> jobxTotalXpMap;
  private final Map<String, Integer> jobsLevelMap;

  public PlayerEntity(Player player) {
    this.player = player;
    this.jobsXpMap = new HashMap<>();
    this.jobxTotalXpMap = new HashMap<>();
    this.jobsLevelMap = new HashMap<>();
  }

  public Player getPlayer() {
    return player;
  }

  public int getJobXp(String jobId) {
    return this.jobsXpMap.getOrDefault(jobId, 0);
  }

  public void setJobXp(String jobId, int xp) {
    this.jobsXpMap.put(jobId, xp);
  }

  public void addJobXp(String jobId, int xp) {
    this.jobsXpMap.replace(jobId, this.jobsXpMap.get(jobId) + xp);
  }

  public int getJobTotalXp(String jobId) {
    return this.jobxTotalXpMap.getOrDefault(jobId, 0);
  }

  public void addJobTotalXp(String jobId, int xp) {
    this.jobxTotalXpMap.replace(jobId, this.jobxTotalXpMap.get(jobId) + xp);
  }

  public void setJobTotalXp(String jobId, int xp) {
    this.jobxTotalXpMap.put(jobId, xp);
  }

  public int getJobLevel(String jobId) {
    return this.jobsLevelMap.getOrDefault(jobId, 1);
  }

  public void setJobLevel(String job, int level) {
    this.jobsLevelMap.put(job, level);
  }

  public Map<String, Integer> getAllJobsXp() {
    return new HashMap<>(jobsXpMap);
  }

  public Map<String, Integer> getJobxTotalXpMap() {
    return new HashMap<>(jobxTotalXpMap);
  }

  public Map<String, Integer> getAllJobsLevels() {
    return new HashMap<>(jobsLevelMap);
  }
}
