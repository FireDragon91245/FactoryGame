package GameBuildings;

import GameItems.Items;

@SuppressWarnings("record")
public final class OreConfig
{
        public final int freeSpaceRequired;
        public final double spawnChance;
        public final OreGenerationTypes depositType;
        public final double initialOreChance;
        public final double oreChanceFalloff;
        public final int maxSpread;
        public final Buildings buildingID;
        public final Items generatesItem;
        public final int itemMinBaseAmount;
        public final int itemMaxBaseAmount;
        public final int itemDistanceFalloff;

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

    @Override
    public String toString() {
        return String.format("FreeSpaceReq: %s, SpawnChance: %s, GenType: %s, InitOreChance: %s, ChanceFalloff: %s, MaxSpread: %s, BType: %s", freeSpaceRequired, spawnChance, depositType, initialOreChance, oreChanceFalloff, maxSpread, buildingID);
    }

}
