package be.fcip.cms.service;

import be.fcip.cms.persistence.model.PageEntity;
import com.mitchellbosecke.pebble.error.PebbleException;
import org.springframework.ui.ModelMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface IRenderPageService {

    String renderPage(HttpServletRequest request, HttpServletResponse response, PageEntity content, ModelMap model) throws IOException, PebbleException, ServletException;
}

