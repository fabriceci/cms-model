package be.fcip.cms.service;

import com.mitchellbosecke.pebble.template.PebbleTemplate;

public interface IPebbleServiceCacheProvider {

    PebbleTemplate getCompiledTemplate(String content, String key);
}
