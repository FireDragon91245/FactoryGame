package GameBuildings;

import GameItems.Items;

@SuppressWarnings("record")
public final class OreConfig {
    private final int freeSpaceRequired;
    private final double spawnChance;
    private final OreGenerationTypes depositType;
    private final double initialOreChance;
    private final double oreChanceFalloff;
    private final int maxSpread;
    private final Buildings buildingID;
    private final Items generatesItem;
    private final int itemMinBaseAmount;
    private final int itemMaxBaseAmount;
    private final int itemDistanceFalloff;

    public OreConfig(int freeSpaceRequired, double spawnChance, OreGenerationTypes depositType, double initialOreChance, double oreChanceFalloff, int maxSpread, Buildings buildingID, Items generatesItem, int itemMinBaseAmount, int itemMaxBaseAmount, int itemDistanceFalloff) {
        this.freeSpaceRequired = freeSpaceRequired;
        this.spawnChance = spawnChance;
        this.depositType = depositType;
        this.initialOreChance = initialOreChance;
        this.oreChanceFalloff = oreChanceFalloff;
        this.maxSpread = maxSpread;
        this.buildingID = buildingID;
        this.generatesItem = generatesItem;
        this.itemMinBaseAmount = itemMinBaseAmount;
        this.itemMaxBaseAmount = itemMaxBaseAmount;
        this.itemDistanceFalloff = itemDistanceFalloff;
    }

    public int getFreeSpaceRequired() {
        return freeSpaceRequired;
    }

    public double getSpawnChance() {
        return spawnChance;
    }

    public OreGenerationTypes getDepositType() {
        return depositType;
    }

    public double getInitialOreChance() {
        return initialOreChance;
    }

    public double getOreChanceFalloff() {
        return oreChanceFalloff;
    }

    public int getMaxSpread() {
        return maxSpread;
    }

    public Buildings getBuildingID() {
        return buildingID;
    }

    public Items getGeneratesItem() {
        return generatesItem;
    }

    public int getItemMinBaseAmount() {
        return itemMinBaseAmount;
    }

    public int getItemMaxBaseAmount() {
        return itemMaxBaseAmount;
    }

    public int getItemDistanceFalloff() {
        return itemDistanceFalloff;
    }

    @Override
    public String toString() {
        return String.format("FreeSpaceReq: %s, SpawnChance: %s, GenType: %s, InitOreChance: %s, ChanceFalloff: %s, MaxSpread: %s, BType: %s", freeSpaceRequired, spawnChance, depositType, initialOreChance, oreChanceFalloff, maxSpread, buildingID);
    }

}
