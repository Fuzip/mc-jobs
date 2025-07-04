package dev.fuzip.jobs.entities;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.ChatColor;

public class JobEntity {
  private String id;
  private String name;
  private ChatColor color;
  private Map<String, List<JobXpEntity>> actionsXpMap;
  private Map<Integer, JobRewardEntity> rewardsMap;

  public JobEntity(String id, String name, String color) {
    this.id = id;
    this.name = name;
    this.color = ChatColor.valueOf(color);
    this.actionsXpMap = new HashMap<>();
    this.rewardsMap = new HashMap<>();
  }

  public String getId() {
    return this.id;
  }

  public String getName() {
    return this.name;
  }

  public ChatColor getColor() {
    return this.color;
  }

  public Map<String, List<JobXpEntity>> getActionsXpMap() {
    return this.actionsXpMap;
  }

  public Map<Integer, JobRewardEntity> getRewardsMap() {
    return rewardsMap;
  }
}
