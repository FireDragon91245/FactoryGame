package GameItems;

import GameBuildings.Buildings;

public class InventoryConfig {

    public final Buildings[] thisImportsFrom;
    public final Buildings[] thisExportsTo;
    public final int importSpeed;
    public final int exportSpeed;
    public final int inventorySpace;
    public final Directions[] importDirections;
    public final Directions[] exportDirections;
    public final Buildings[] otherBuildingsBlockedImport;
    public final Buildings[] otherBuildingsBlockedExport;
    public final Directions[] blockedInputDirections;
    public final Directions[] blockedOutputDirections;


    public InventoryConfig(Buildings[] importsFrom, Buildings[] exportsTo, int importSpeed, int exportSpeed, int inventorySpace, Directions[] importDirections, Directions[] exportDirections, Buildings[] otherBuildingsBlockedImport, Buildings[] otherBuildingsBlockedExport, Directions[] blockedInputDirections, Directions[] blockedOutputDirections) {
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
