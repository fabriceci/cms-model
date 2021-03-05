package be.fcip.cms.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class CmsArrayUtils {

    public static String[] cleanArray(final String[] v) {
        if(v==null) return null;
        List<String> list = new ArrayList<>();
        for (String s : v) {
            if(!StringUtils.isEmpty(s) && !s.equals("null")){
                list.add(s);
            }
        }
        return list.size() > 0 ? list.toArray(new String[list.size()]) : null;
    }

    public static boolean isArray(Object obj) {
        return obj != null && obj.getClass().isArray();
    }

}
