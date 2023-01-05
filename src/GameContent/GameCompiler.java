package GameContent;

import GameCore.Main;
import GameUtils.GameUtils;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLClassLoader;

public class GameCompiler {

    private static boolean needsCompiling(String code, String className, String path) {
        if (new File(path + "RuntimeContent\\" + className + ".java").exists()) {
            if (!new File(path + "RuntimeContent\\" + className + ".class").exists()) {
                return true;
            }
            String compiledCode = null;
            try {
                compiledCode = GameUtils.readFile(Main.getGamePath() + "\\RuntimeContent\\" + className + ".java");
            } catch (IOException ignored) {}

            if(compiledCode == null){
                return true;
            }
            return !(code.equals(compiledCode.substring(0, compiledCode.length() - 2)));
        }
        return true;
    }

    public static Class<?> compileCode(String code, String className) {
        String path = Main.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        if (path.endsWith(".jar") || path.endsWith(".zip")) {
            path = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParentFile().getAbsolutePath() + "\\";
        }

        File fiResult;
        if (needsCompiling(code, className, path)) {
            fiResult = compile(code, className, path);
        } else {
            fiResult = getCompiledFile(className, path);
        }
        if (!fiResult.exists()) {
            return null;
        }

        return loadClass(fiResult, path, "RuntimeContent." + className);
    }

    public static Class<?> loadClass(File classFile, String rootPath, String className) {
        try (URLClassLoader loader = URLClassLoader.newInstance(new URL[]{new File(rootPath).toURI().toURL()})) {
            return Class.forName(className, true, loader);
        } catch (IOException | ClassNotFoundException e) {
            Main.getClient().setErrorGameStateException(e);
        }
        Main.getClient().setErrorGameStateException(new ClassNotFoundException("Couldn't find the class: " + className + " int the expected locations! Expected Location: " + classFile.getPath()));
        return null;
    }

    public static Class<?> loadClass(File classFile, String className){
        if(!classFile.exists()){
            Main.getClient().setErrorGameStateException(new ClassNotFoundException("Couldn't find the class: " + className + " int the expected locations! Expected Location: " + classFile.getPath()));
            return null;
        }
        return loadClass(classFile, classFile.getParentFile().getAbsolutePath(), className);
    }

    private static File getCompiledFile(String className, String path) {
        return new File(path + "RuntimeContent\\" + className + ".class");
    }

    private static File compile(String code, String className, String path) {
        boolean success = new File(path + "RuntimeContent\\").mkdir();
        if (!success && !new File(path + "RuntimeContent\\").exists()) {
            Main.getClient().setErrorGameStateException(new IOException("Trying to create file on path" + path + "RuntimeContent\\ but failed, GameCompiler Error"));
        }
        File fi = new File(path + "RuntimeContent\\" + className + ".java");
        try {
            success = fi.createNewFile();
        } catch (IOException e) {
            Main.getClient().setErrorGameStateException(e);
        }
        if (!success && !new File(path + "RuntimeContent\\" + className + ".java").exists()) {
            Main.getClient().setErrorGameStateException(new IOException("Trying to create file on path" + path + "RuntimeContent\\" + className + ".java but failed, GameCompiler Error"));
        }
        try (PrintWriter writer = new PrintWriter(fi)) {
            writer.print(code);
        } catch (FileNotFoundException e) {
            Main.getClient().setErrorGameStateException(e);
        }

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        int res = compiler.run(null, null, null, fi.getAbsolutePath());
        if (res != 0) {
            Main.getClient().setErrorGameStateException(new IllegalArgumentException("Compiler error with code " + res + " on file " + fi.getAbsolutePath()));
        }
        File fiResult = new File(path + "RuntimeContent\\" + className + ".class");
        if (!fiResult.exists()) {
            Main.getClient().setErrorGameStateException(new FileNotFoundException("Compiler result didn't result the expected file, file expected: " + fiResult.getAbsolutePath()));
        }
        return fiResult;
    }

    public static Class<?> compileFile(File sourceFile, String rootFolder, String className) {
        if(!sourceFile.exists()){
            Main.getClient().setErrorGameStateException(new FileNotFoundException(String.format("The file to compile at %s does not exist! Class import: %s", sourceFile.getPath(), className)));
            return null;
        }
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        int res = compiler.run(null, null, null, sourceFile.getAbsolutePath());
        if(res != 0){
            Main.getClient().setErrorGameStateException(new IllegalArgumentException(String.format("While compiling %s class a compiler error occurred, target File: %s", className, sourceFile.getAbsolutePath())));
            return null;
        }
        File result = new File(rootFolder + className + ".class");
        if(!result.exists()){
            Main.getClient().setErrorGameStateException(new FileNotFoundException(String.format("Compiling the class %s resulted in a missing .class file! Target File: %s", className, sourceFile.getAbsolutePath())));
            return null;
        }

        return loadClass(result, rootFolder,"RuntimeContent." + className + "." + className);
    }

    public static Class<?> compileFile(File sourceFile, String className){
        if(!sourceFile.exists()){
            Main.getClient().setErrorGameStateException(new FileNotFoundException(String.format("The file to compile at %s does not exist! Class import: %s", sourceFile.getPath(), className)));
            return null;
        }
        return compileFile(sourceFile, sourceFile.getParentFile().getAbsolutePath(), className);
    }
}
