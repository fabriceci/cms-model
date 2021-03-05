package be.fcip.cms.hook;

import be.fcip.cms.persistence.model.AdminMenuItem;

import java.util.List;

public interface IAdminMenu {

    List<AdminMenuItem> getItems();

}
