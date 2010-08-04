package net.sourcewalker.picrename;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileNameFilter {

    private static final FileNameFilter[] FILTERS;

    static {
        FILTERS = new FileNameFilter[] { new FileNameFilter("^\\d+$", ""),
                new FileNameFilter("DSC\\d+", "") };
    }

    public static String filterName(String name) {
        for (FileNameFilter filter : FILTERS) {
            name = filter.apply(name);
        }
        return name;
    }

    private Pattern pattern;
    private String replacement;

    public FileNameFilter(String pattern, String replacement) {
        this.pattern = Pattern.compile(pattern);
        this.replacement = replacement;
    }

    public String apply(String input) {
        Matcher match = pattern.matcher(input);
        if (match.matches()) {
            return match.replaceAll(replacement);
        } else {
            return input;
        }
    }

}
