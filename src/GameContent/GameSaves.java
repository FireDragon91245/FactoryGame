package GameContent;

import GameCore.Main;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class GameSaves {

    public static void save(String saveName){
        String json = Main.getGsonMaster().toJson(Main.getClient().buildingCore().getAllBuildings());
        String path = Main.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        File f = new File(path + "saves\\" + saveName);
        try {
            if(!f.createNewFile()){
                Main.getClient().setErrorGameStateException(new IOException("The creation of the save file with name: " + saveName + " failed"));
            }
        } catch (IOException e) {
            Main.getClient().setErrorGameStateException(e);
        }

        try (PrintWriter writer = new PrintWriter(f)){
            writer.print(json);
        }
        catch (IOException e) {
            Main.getClient().setErrorGameStateException(e);
        }
    }

}
