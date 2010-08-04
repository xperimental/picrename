package net.sourcewalker.picrename;

import java.io.File;

public class FileNameTools {

    static String removeExtension(String basename) {
        String[] tokens = basename.split("\\.");
        String name;
        if (tokens.length > 1) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < tokens.length - 1; i++) {
                sb.append(tokens[i]);
            }
            name = sb.toString();
        } else {
            name = tokens[0];
        }
        return name;
    }

    static String getExtension(File file) {
        String[] tokens = file.getName().toLowerCase().split("\\.");
        if (tokens.length > 1) {
            return "." + tokens[tokens.length - 1];
        } else {
            return "";
        }
    }

}
