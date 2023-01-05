package GameItems;

public class InventoryConfig {

    public final String[] thisImportsFrom;
    public final String[] thisExportsTo;
    public final int importSpeed;
    public final int exportSpeed;
    public final int inventorySpace;
    public final Directions[] importDirections;
    public final Directions[] exportDirections;
    public final String[] otherBuildingsBlockedImport;
    public final String[] otherBuildingsBlockedExport;
    public final Directions[] blockedInputDirections;
    public final Directions[] blockedOutputDirections;


    public InventoryConfig(String[] importsFrom, String[] exportsTo, int importSpeed, int exportSpeed, int inventorySpace, Directions[] importDirections, Directions[] exportDirections, String[] otherBuildingsBlockedImport, String[] otherBuildingsBlockedExport, Directions[] blockedInputDirections, Directions[] blockedOutputDirections) {
        this.thisImportsFrom = importsFrom;
        this.thisExportsTo = exportsTo;
        this.importSpeed = importSpeed;
        this.exportSpeed = exportSpeed;
        this.inventorySpace = inventorySpace;
        this.importDirections = importDirections;
        this.exportDirections = exportDirections;
        this.otherBuildingsBlockedImport = otherBuildingsBlockedImport;
        this.otherBuildingsBlockedExport = otherBuildingsBlockedExport;
        this.blockedInputDirections = blockedInputDirections;
        this.blockedOutputDirections = blockedOutputDirections;
    }
}
