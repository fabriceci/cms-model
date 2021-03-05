package be.fcip.cms.service;

import be.fcip.cms.persistence.model.BlockEntity;
import com.mitchellbosecke.pebble.error.PebbleException;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

public interface IPeebleService {

    String parseString(String data, Map<String, Object> model)  throws IOException, PebbleException;
    String parseBlock(BlockEntity block, Map<String, Object> model) throws IOException, PebbleException;
    void fillModelMap(Map<String, Object> model, HttpServletRequest request);
}
