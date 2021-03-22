package be.fcip.cms.util;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CmsTokenUtilsTest {

    @Test
    public void parseTest(){
        Map<String, String> inputMap = new HashMap<>();
        inputMap.put("title", "a");
        inputMap.put("my_name", "b");

        String template = "[title] [my_name]";

        // check result & if map is modified
        assertEquals("a b", CmsTokenUtils.parse(template, Collections.unmodifiableMap(inputMap)));
    }
}
