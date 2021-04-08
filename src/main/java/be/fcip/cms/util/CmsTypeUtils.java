package be.fcip.cms.util;

import org.apache.commons.lang3.LocaleUtils;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CmsTypeUtils {

    public static <T> T safeCast(Object o, Class<T> clazz) {
        return clazz != null && clazz.isInstance(o) ? clazz.cast(o) : null;
    }

    @SuppressWarnings("unchecked")
    public static <T extends List<?>> T toList(Object obj) {
        try{ return (obj instanceof List) ? (T) obj : null; } catch(ClassCastException ignore){ };
        return null;
    }

    public static <T extends List<?>> T toList(Object o, T defaultValue){
        T result = toList(o);
        return result != null? result : defaultValue;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Map<?, ?>> T toMap(Object obj) {
        try{ return (obj instanceof Map) ? (T) obj : null; } catch(ClassCastException ignore){};
        return null;
    }

    public static <T extends Map<?, ?>> T toMap(Object o, T defaultValue){
        T result = toMap(o);
        return result != null? result : defaultValue;
    }

    public static Integer toInteger(Object o){
        if( o instanceof String){ try { return Integer.parseInt((String)o); }catch(NumberFormatException ignore){ return null;} }
        if(o instanceof Long){ return ((Long)o).intValue(); }
        return o instanceof Integer ? (Integer)o : null;
    }

    public static int toInteger(Object o, int defaultValue){
        Integer result = toInteger(o);
        return result != null? result : defaultValue;
    }

    public static Long toLong(Object o){

        if(o instanceof String){ try { return Long.parseLong((String)o); }catch(NumberFormatException ignore){ return null;} }
        if(o instanceof Integer){ return ((Integer)o).longValue(); }
        return o instanceof Long ? (Long)o : null;
    }

    public static long toLong(Object o, long defaultValue){
        Long result = toLong(o);
        return result != null? result : defaultValue;
    }

    public static Double toDouble(Object o){
        if( o instanceof String){ try { return Double.parseDouble((String)o); }catch(NumberFormatException ignore){ return null;} }
        if( o instanceof Float) { return (double)(Float)o; }
        return o instanceof Double ? (Double)o : null;
    }

    public static double toDouble(Object o, double defaultValue){
        Double result = toDouble(o);
        return result != null? result : defaultValue;
    }

    public static String toString(Object o){
        return o instanceof String ? (String)o : null;
    }

    public static String toString(Object o, String defaultValue){
        String result = toString(o);
        return result != null? result : defaultValue;
    }

    public static Locale toLocale(Object o){
        if( o instanceof String) { return LocaleUtils.toLocale((String)o); }
        return o instanceof Locale ? (Locale)o : null;
    }

    public static Locale toLocale(Object o, Locale defaultValue){
        Locale result = toLocale(o);
        return result != null? result : defaultValue;
    }

    public static Boolean toBoolean(Object o) {
        if (o instanceof String) {
            String value = (String) o;
            if(value.equals("true")) return true;
            else if(value.equals("false")) return false;
            else return null;
        }

        return o instanceof Boolean ? (Boolean) o : null;
    }

    public static Boolean toBoolean(Object o, Boolean defaultValue) {
        Boolean result = toBoolean(o);
        return result != null? result : defaultValue;
    }

    public static String localeToString(Object o){
        return o instanceof Locale ? ((Locale)o).toString() : null;
    }

}
