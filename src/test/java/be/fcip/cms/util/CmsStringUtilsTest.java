package be.fcip.cms.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CmsStringUtilsTest {

    @Test
    public void capitalizeFirstLetterTest(){
        String t1 = "foo";
        String t2 = "Foo";
        String t3 = "fOO";
        String t4 = "FOO";
        assertEquals("Foo", CmsStringUtils.capitalizeFirstLetter(t1));
        assertEquals("Foo", CmsStringUtils.capitalizeFirstLetter(t2));
        assertEquals("FOO", CmsStringUtils.capitalizeFirstLetter(t3));
        assertEquals("FOO", CmsStringUtils.capitalizeFirstLetter(t4));
    }

    @Test
    public void returnSlugifiedString_ToSlugTest() {
        // given
        String string = "Hello world";

        // when
        String result = CmsStringUtils.toSlug(string);

        // then
        assertEquals("hello-world", result);
    }

    @Test
    public void trimWhiteSpacesOtherThanSpace_ToSlugTest() {
        // given
        String string = "\tHello \tworld \r\t";

        // when
        String result = CmsStringUtils.toSlug(string);

        // then
        assertEquals("hello-world", result);
    }


    @Test
    public void replaceAccents_ToSlugTest() {
        // given
        String string = "ÃÕÇÁÓÊÉÚàéè";

        // when
        String result = CmsStringUtils.toSlug(string);

        // then
        assertEquals("aocaoeeuaee", result);
    }


    @Test
    public void replacePlusSignToSeparator_ToSlugTest() {
        // given
        String string = "\tHello+\tworld \r\t";

        // when
        String result = CmsStringUtils.toSlug(string);

        // then
        assertEquals("hello-world", result);
    }

    @Test
    public void returnEmptyStringIfNullGiven_ToSlugTest() {
        // given
        String string = null;

        // when
        String result = CmsStringUtils.toSlug(string);

        // then
        assertEquals("", result);
    }

    @Test
    public void normalizeRepeatedHyphensToSingleHyphenWithHyphenSeparator_ToSlugTest() {
        //given
        String string = "a---b___c";

        //when
        String result = CmsStringUtils.toSlug(string);

        //then
        assertEquals("a-b___c", result);
    }
}
