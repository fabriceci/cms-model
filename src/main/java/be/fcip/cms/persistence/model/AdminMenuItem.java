package be.fcip.cms.persistence.model;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class AdminMenuItem implements Comparable<AdminMenuItem>{

    private String name;
    private String description;
    private int order;
    private String icon;
    private String url;
    private List<String> roles;
    private List<AdminMenuItem> children;

    @Override
    public int compareTo(AdminMenuItem item){

        if(this.getOrder() > item.getOrder()){
            return -1;
        } else if (this.getOrder() < item.getOrder()){
            return 1;
        } else {
            return 0;
        }
    }

}
