package be.fcip.cms.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

@Slf4j
public class CmsStringUtils {

    private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");
    private static final Pattern EDGESDHASHES = Pattern.compile("(^-|-$)");

    public static String toSlug(String input) {
        if(StringUtils.isEmpty(input)) return "";
        String nowhitespace = WHITESPACE.matcher(input).replaceAll("-");
        String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
        String slug = NONLATIN.matcher(normalized).replaceAll("");
        slug = EDGESDHASHES.matcher(slug).replaceAll("");
        return slug.toLowerCase(Locale.ENGLISH);
    }

    public static String capitalizeFirstLetter(String original) {
        String trimedString = StringUtils.trim(original);
        if (trimedString == null || trimedString.length() == 0) {
            return original;
        }
        return trimedString.substring(0, 1).toUpperCase() + trimedString.substring(1).toLowerCase();
    }
}
