package GameContent;

import GameCore.GameCore;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;
import GameCore.Main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public class GameContentExtractor {
    public static void extractAll(String path) {
        if (!new File(path).exists()) {
            GameCore.setErrorGameStateException(new FileNotFoundException(String.format("Didn't find file %s, unable to extract game content files!", path)));
        }
        ZipFile zip = new ZipFile(path);
        try {
            List<FileHeader> headers = zip.getFileHeaders();
            for (FileHeader header : headers) {
                if (!header.getFileName().endsWith(".class") && !header.getFileName().endsWith(".java") && !header.isDirectory()) {
                    if(header.getFileName().contains("META-INF")){
                        continue;
                    }
                    zip.extractFile(header, new File(path).getParentFile().getAbsolutePath() + "\\");
                }
            }
        } catch (ZipException e) {
            GameCore.setErrorGameStateException(e);
        }
        try {
            zip.close();
        } catch (IOException ignored) {
        }
    }

    public static void findPossibleRootFolder() {
        String path = Main.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        GameCore.addRootFolders(new File(path).getParentFile().getAbsolutePath());
        GameCore.addRootFolders(new File(path).getParentFile().getAbsolutePath() + "\\FactoryGame");
    }
}
