package be.fcip.cms.util;

import org.hamcrest.collection.IsMapContaining;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;

public class CmsTypeUtilsTest {

    @Test
    @DisplayName("Safe Cast Test")
    @SuppressWarnings("unchecked")
    public void safeCast(){
        Object stringInput = "test";
        Object integerInput = 1;
        Object longInput = 1L;
        Object floatInput = 3.4f;
        Object doubleInput = 3.2d;
        Object listInput = Arrays.asList("1", "2");
        List<String> listOutput = CmsTypeUtils.safeCast(listInput, List.class);
        Map<String, String> mapExpected = new HashMap(); mapExpected.put("a", "b");
        Object mapInput = new HashMap<>(mapExpected);
        Map<String, String> mapOutput = CmsTypeUtils.safeCast(mapInput, Map.class);

        assertNull(CmsTypeUtils.safeCast(null, String.class));
        assertEquals(1, CmsTypeUtils.safeCast(integerInput, Integer.class));
        assertEquals(1L, CmsTypeUtils.safeCast(longInput, Long.class));
        assertEquals(3.4f, CmsTypeUtils.safeCast(floatInput, Float.class));
        assertEquals(3.2d, CmsTypeUtils.safeCast(doubleInput, Double.class));
        assertEquals("test", CmsTypeUtils.safeCast(stringInput, String.class));
        assertNotNull(listOutput);
        assertEquals(Arrays.asList("1", "2"), listOutput);
        assertThat(listOutput, hasSize(2));
        assertNotNull(mapOutput);
        assertEquals(mapExpected, mapOutput);
        assertThat(mapOutput.size(), is(1));
        assertThat(mapOutput, IsMapContaining.hasEntry("a", "b"));

    }

    @Test
    @DisplayName("Integer Cast Test")
    public void castToInteger(){
        assertEquals(20, CmsTypeUtils.toInteger("20"));
        assertEquals(20, CmsTypeUtils.toInteger(20));
        assertEquals(20, CmsTypeUtils.toInteger(20L));
        assertNull(CmsTypeUtils.toInteger(5.0f));
        assertNull(CmsTypeUtils.toInteger(5.0d));
        assertNull(CmsTypeUtils.toInteger(null));
        assertNull(CmsTypeUtils.toInteger(new Object()));
        assertNull(CmsTypeUtils.toInteger(""));
    }

    @Test
    @DisplayName("Long Cast Test")
    public void castToLong(){
        assertEquals(20, CmsTypeUtils.toLong("20"));
        assertEquals(20, CmsTypeUtils.toLong(20));
        assertEquals(20, CmsTypeUtils.toLong(20L));
        assertNull(CmsTypeUtils.toLong(5.0f));
        assertNull(CmsTypeUtils.toLong(5.0d));
        assertNull(CmsTypeUtils.toLong(null));
        assertNull(CmsTypeUtils.toLong(new Object()));
        assertNull(CmsTypeUtils.toLong(""));
    }

    @Test
    @DisplayName("Double Cast Test")
    public void castToDouble(){
        assertEquals(20d, CmsTypeUtils.toDouble("20"));
        assertEquals(20d, CmsTypeUtils.toDouble(20d));
        assertEquals(20d, CmsTypeUtils.toDouble(20f));
        assertNull(CmsTypeUtils.toDouble(null));
        assertNull(CmsTypeUtils.toDouble(new Object()));
        assertNull(CmsTypeUtils.toDouble(""));
        assertNull(CmsTypeUtils.toDouble(5));
    }

    @Test
    @DisplayName("Boolean Cast Test")
    public void castToBoolean(){
        assertEquals(true, CmsTypeUtils.toBoolean("true"));
        assertEquals(false, CmsTypeUtils.toBoolean("false"));
        assertNull(CmsTypeUtils.toBoolean(null));
        assertNull(CmsTypeUtils.toBoolean(new Object()));
        assertNull(CmsTypeUtils.toBoolean(""));
        assertNull(CmsTypeUtils.toBoolean(5));
    }

    @Test
    @DisplayName("String Cast Test")
    public void castToString(){
        Object test = "object";
        assertEquals("object", CmsTypeUtils.toString(test));
        assertNull(CmsTypeUtils.toString(null));
        assertNull(CmsTypeUtils.toString(new Object()));
        assertNull(CmsTypeUtils.toString(5));
    }

    @Test
    @DisplayName("List Cast Test")
    public void castToList(){
        Object input = Arrays.asList("a", "b", "c");
        List<String> expected = Arrays.asList("a", "b", "c");
        List<String> output = CmsTypeUtils.toList(input);

        assertNotNull(output);
        assertNotSame(expected, output);
        assertEquals(expected, output);
        assertThat(output, hasSize(3));
        assertNull(CmsTypeUtils.toList(null));
        assertNull(CmsTypeUtils.toList(new Object()));
        assertNull(CmsTypeUtils.toList(5));
    }


    @Test
    @DisplayName("Map Cast Test")
    public void castToMap(){

        Map<String, String> expected = new HashMap<>();
        expected.put("n", "node");
        expected.put("c", "c++");
        expected.put("j", "java");
        expected.put("p", "python");

        Object input = new HashMap<>(expected);

        Map<String, String> output = CmsTypeUtils.toMap(input);

        assertNotNull(output);
        assertNotSame(expected, output);
        assertEquals(expected, output);
        assertThat(output.size(), is(4));
        assertThat(output, IsMapContaining.hasEntry("n", "node"));
        assertThat(output, not(IsMapContaining.hasEntry("r", "ruby")));
    }

    @Test
    @DisplayName("Locale Cast Test")
    public void castToLocale(){
        Locale expected = new Locale("en");
        Object input = new Locale("en");
        assertEquals(expected, CmsTypeUtils.toLocale(input));
        assertEquals(expected, CmsTypeUtils.toLocale("en"));
        assertNull(CmsTypeUtils.toLocale(null));
        assertNull(CmsTypeUtils.toLocale(new Object()));
        assertNull(CmsTypeUtils.toLocale(5));
    }

    @Test
    @DisplayName("Locale to String Test")
    public void castlocaleToString(){
        Object input = new Locale("en");
        assertEquals("en", CmsTypeUtils.localeToString(input));
        assertEquals(null, CmsTypeUtils.localeToString("en"));
        assertNull(CmsTypeUtils.localeToString(null));
        assertNull(CmsTypeUtils.localeToString(new Object()));
        assertNull(CmsTypeUtils.localeToString(5));
    }
}
