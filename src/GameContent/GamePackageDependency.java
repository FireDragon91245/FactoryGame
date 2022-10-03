package GameContent;

@SuppressWarnings("record")
public class GamePackageDependency {

    public final String packageName;
    public final float packageMinVersion;
    public final float packageMaxVersion;

    public GamePackageDependency(String packageName, float packageMinVersion, float packageMaxVersion) {
        this.packageName = packageName;
        this.packageMinVersion = packageMinVersion;
        this.packageMaxVersion = packageMaxVersion;
    }
}
