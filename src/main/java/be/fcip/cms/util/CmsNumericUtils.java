package be.fcip.cms.util;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class CmsNumericUtils {

    public static String twoDigit(int number) {
        return String.format("%02d", number);
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static Long objectToLong(Object o){
        Long result = null;
        if(o instanceof String){
            try {
                result = Long.parseLong((String) o);
            } catch(NumberFormatException ignored) {}
        } else if (o instanceof Integer){
            Integer integer = (Integer)o;
            result = integer.longValue();
        } else if (o instanceof  Long){
            result = (Long)o;
        } else if (o instanceof Double){
            Double d = (Double)o;
            result = d.longValue();
        }

        return result;
    }

    public static List<Long> getLoopList(Long from, Long to) {
        List<Long> list = new ArrayList<>();

        // -
        if (from > to) {
            while (from.longValue() != to.longValue()) {
                list.add(from);
                from--;
            }
            // +
        } else {
            while (from.longValue() != to.longValue()) {
                list.add(from);
                from++;
            }
        }

        return list;
    }
}
