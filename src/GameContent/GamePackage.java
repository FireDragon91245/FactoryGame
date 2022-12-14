package GameContent;

public class GamePackage {

    public final String buildingCfg;
    public final String guiCfg;
    public final String oreCfg;
    public final String ItemCfg;
    public final boolean frameUpdates;
    public final boolean tickUpdates;
    public final float packageVersion;
    public final String packageDisplayName;
    public final String[] packageDescription;
    public final GamePackageDependency[] dependencies;

    private String packageId;

    public GamePackage(String buildingCfg, String guiGfg, String oreCfg, String itemCfg, boolean frameUpdates, boolean tickUpdates, float packageVersion, String packageDisplayName, String[] packageDescription, GamePackageDependency[] dependencies) {
        this.buildingCfg = buildingCfg;
        this.guiCfg = guiGfg;
        this.oreCfg = oreCfg;
        ItemCfg = itemCfg;
        this.frameUpdates = frameUpdates;
        this.tickUpdates = tickUpdates;
        this.packageVersion = packageVersion;
        this.packageDisplayName = packageDisplayName;
        this.packageDescription = packageDescription;
        this.dependencies = dependencies;
    }

    public String getPackageId() {
        return packageId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }
}
