package GameContent;

import GameCore.Main;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GameContentExtractor {
    public void extractAll(String path) {
        if (!new File(path).exists()) {
            Main.getClient().setErrorGameStateException(new FileNotFoundException(String.format("Didn't find file %s, unable to extract game content files!", path)));
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
            Main.getClient().setErrorGameStateException(e);
        }
        try {
            zip.close();
        } catch (IOException ignored) {
        }
    }

    public ArrayList<String> findPossibleRootFolder() {
        ArrayList<String> list = new ArrayList<>();
        String path = Main.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        list.add(new File(path).getParentFile().getAbsolutePath());
        list.add(new File(path).getParentFile().getAbsolutePath() + "\\FactoryGame");
        return list;
    }
}
