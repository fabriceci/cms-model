package be.fcip.cms.pebble.view;

import be.fcip.cms.model.db.PageableResult;
import be.fcip.cms.util.CmsHttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class PaginationHelper {

    public static String getPageableNavigation(PageableResult result, HttpServletRequest request, Long delta){
        return getPageableNavigation(result, request, delta, true, true);
    }

    public static String getPageableNavigation(PageableResult pageableResult, HttpServletRequest request, Long delta,  boolean preNext, boolean firstEnd){

        StringBuilder sb = new StringBuilder();
        String queryString = request.getQueryString();
        Map<String, String> map;
        if(!StringUtils.isEmpty(queryString)){
            map = CmsHttpUtils.queryStringToMap(queryString);
        } else {
            map = new HashMap<>();
        }

        int currentPage = Math.toIntExact(pageableResult.getCurrentPage());
        int totalPage = Math.toIntExact(pageableResult.getTotalPage());
        int arrInf = (int)Math.floor(delta/2);

        int min = 1;
        int max = totalPage;

        if(currentPage >= delta){
            min = currentPage - arrInf;
        }
        max = Math.toIntExact(min + (delta-1));
        if(max > totalPage){
            int diff = max - totalPage;
            max = totalPage;
            min = min - diff;
        }
        if(min < 1) min = 1;

        boolean btnPrevious = false;
        boolean btnFirst = false;

        if(preNext && min > 1){
            btnPrevious = true;
        }

        if(firstEnd && min > 2){
            btnFirst = true;
        }
        boolean btnNext = false;
        boolean btnEnd = false;
        if(max < totalPage){
            btnNext = true;
        }
        if(max < totalPage-1){
            btnEnd = true;
        }

        //String resultQueryString = CmsUtils.mapToQueryString(map);
       // sb.append("<nav role=\"navigation\"><ul class=\"" + ulClass + "\">");
        if(btnFirst){
            addPaginationLi(sb, -1, 1, map, "first", "...");
        }
        if(btnPrevious){
            addPaginationLi(sb, -1, currentPage - 1 , map, "previous", "<");
        }
        boolean activePage;
        for(; min < (max+1) ; min++){
            addPaginationLi(sb, currentPage, min, map, null, null);
        }
        if(preNext && btnNext){
            addPaginationLi(sb, -1, currentPage - 1 , map, "next", ">");
        }
        if(firstEnd && btnEnd){
            addPaginationLi(sb, -1, totalPage, map, "last", "...");
        }
       // sb.append("</ul></nav>");

        return sb.toString();
    }



    private static void addPaginationLi(StringBuilder sb, int currentPage, int page, Map<String, String> queryMap, String extraClass, String value ){
        boolean activePage = false;
        if(page == currentPage){
            activePage = true;
        }

        sb.append("<li><a href=\"?");
        queryMap.put("page", String.valueOf(page));
        sb.append(CmsHttpUtils.mapToQueryString(queryMap));
        sb.append("\"");
        if(!StringUtils.isEmpty(extraClass)){
            sb.append(" class=\"" + extraClass + "\"");
        } else if(activePage) {
            sb.append(" class=\"active\"");
        }

        sb.append(">");
        if(StringUtils.isEmpty(value)) {
            sb.append(page);
        }
        else{
            sb.append(value);
        }
        sb.append("</a></li>");
    }


}
