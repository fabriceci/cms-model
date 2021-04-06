package be.fcip.cms.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MenuItem {

    private long pageId;
    private long dataId;
    private String title;
    private String slug;
    private List<MenuItem> children;
    private String type;
    private boolean active;
    private List<String> roles;

    private String icon;
    private String menuClass;
    private String menuContent;
    private boolean menuContentOnly;
}
