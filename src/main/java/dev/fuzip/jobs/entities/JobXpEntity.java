package dev.fuzip.jobs.entities;

import org.bukkit.Material;

/** Define the XP given by a Material for a Job. */
public class JobXpEntity {

  private final Material material;
  private final int xp;

  public JobXpEntity(Material material, int xp) {
    this.material = material;
    this.xp = xp;
  }

  public Material getMaterial() {
    return material;
  }

  public int getXp() {
    return xp;
  }
}
