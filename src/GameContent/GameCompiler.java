package GameContent;

import GameCore.GuiElements.Intractable;
import GameCore.Main;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLClassLoader;

public class GameCompiler {

    public static boolean needsCompiling(String code, String className, String path) {
        if (new File(path + "RuntimeContent\\" + className + ".java").exists()) {
            if (!new File(path + "RuntimeContent\\" + className + ".class").exists()) {
                return true;
            }
            String compiledCode = GameConfigLoader.readFile("\\RuntimeContent\\" + className + ".java");
            if(compiledCode == null){
                return true;
            }
            return !(code.equals(compiledCode.substring(0, compiledCode.length() - 2)));
        }
        return true;
    }

    public static Class<? extends Intractable> compileCode(String code, String className) {
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

        return loadClass(fiResult, path, className);
    }

    @SuppressWarnings("unchecked")
    private static Class<? extends Intractable> loadClass(File fiResult, String path, String className) {
        try (URLClassLoader loader = URLClassLoader.newInstance(new URL[]{new File(path).toURI().toURL()})) {
            Class<?> cls = Class.forName("RuntimeContent." + className, true, loader);
            if (!Intractable.class.isAssignableFrom(cls)) {
                Main.getClient().setErrorGameStateException(new ClassCastException("Cannot cast the runtime compiled class: " + className + " from file: " + fiResult.getAbsolutePath() + " to the required interface: " + Intractable.class.getName()));
                return null;
            }
            return (Class<? extends Intractable>) cls;
        } catch (IOException | ClassNotFoundException e) {
            Main.getClient().setErrorGameStateException(e);
        }
        Main.getClient().setErrorGameStateException(new ClassNotFoundException("Couldn't find the runtime compiled class: " + className + " int the expected locations! Expected Location: " + fiResult.getAbsolutePath()));
        return null;
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
}
