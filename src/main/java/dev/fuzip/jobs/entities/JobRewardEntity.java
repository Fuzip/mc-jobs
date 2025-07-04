package dev.fuzip.jobs.entities;

import org.bukkit.Material;

public class JobRewardEntity {
  private final Material material;
  private final int quantity;

  public JobRewardEntity(Material material, int quantity) {
    this.material = material;
    this.quantity = quantity;
  }

  public Material getMaterial() {
    return material;
  }

  public int getQuantity() {
    return quantity;
  }
}
