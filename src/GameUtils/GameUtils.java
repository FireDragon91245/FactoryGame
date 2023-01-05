package GameUtils;

import java.io.*;
import java.util.ArrayList;

public class GameUtils {


    private static final char[] forbiddenCharacters = {'<', '>', ':', '"', '/', '\\', '|', '?', '*'};

    public static char[] getForbiddenFileNameChars() {
        return forbiddenCharacters;
    }

    public static ArrayList<String> ChopValue(String value, int valueLength) {
        String rest = value;
        ArrayList<String> list = new ArrayList<>();
        int limit = 0;
        while (limit++ < 100) {
            IndexInformation info = FindLastSpaceBeforeIndex(rest, valueLength);
            if (info.found) {
                list.add(rest.substring(0, info.index));
                rest = rest.substring(info.index + 1);
            } else {
                if (info.index == -1) {
                    list.add(rest.substring(0, valueLength - 1) + '-');
                    rest = rest.substring(valueLength - 1);
                } else {
                    list.add(rest);
                    break;
                }
            }
        }

        return list;
    }

    public static String listToString(ArrayList<?> list) {
        StringBuilder sb = new StringBuilder("[");
        for (Object o : list) {
            sb.append(o.toString());
            sb.append(", ");
        }
        sb.replace(sb.length() - 2, sb.length(), "]");
        return sb.toString();
    }

    private static IndexInformation FindLastSpaceBeforeIndex(String s, int index) {
        if (index >= s.length()) {
            return new IndexInformation(false, -2);
        }

        for (int i = index; i >= 0; i--) {
            if (s.charAt(i) == ' ') {
                return new IndexInformation(true, i);
            }
        }

        return new IndexInformation(false, -1);
    }

    public static int GetCharCountOfInt(int number) {
        if (number < 100000) {
            if (number < 100) {
                if (number < 10) {
                    return 1;
                } else {
                    return 2;
                }
            } else {
                if (number < 1000) {
                    return 3;
                } else {
                    if (number < 10000) {
                        return 4;
                    } else {
                        return 5;
                    }
                }
            }
        } else {
            if (number < 10000000) {
                if (number < 1000000) {
                    return 6;
                } else {
                    return 7;
                }
            } else {
                if (number < 100000000) {
                    return 8;
                } else {
                    if (number < 1000000000) {
                        return 9;
                    } else {
                        return 10;
                    }
                }
            }
        }
    }

    public static boolean containsChar(String s, char c) {
        for (char chr : s.toCharArray()) {
            if (chr == c) {
                return true;
            }
        }
        return false;
    }

    public static String readFile(String absolutePath) throws IOException {
        if (!new File(absolutePath).exists()) {
            throw new FileNotFoundException(String.format("File to read was not found at the expected location %s", absolutePath));
        }

        BufferedReader reader = new BufferedReader(new FileReader(absolutePath));

        StringBuilder builder = new StringBuilder();
        String line = reader.readLine();

        while (line != null) {
            builder.append(line);
            builder.append(System.lineSeparator());
            line = reader.readLine();
        }

        return builder.toString();
    }

    public static String readFile(File fi) throws IOException{
        return readFile(fi.getAbsolutePath());
    }

    private static class IndexInformation {
        public final boolean found;
        public final int index;

        private IndexInformation(boolean found, int index) {
            this.found = found;
            this.index = index;
        }
    }
}
