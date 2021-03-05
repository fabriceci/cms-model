package be.fcip.cms.hook;

import com.mitchellbosecke.pebble.extension.Function;

import java.util.Map;

public interface IPebbleExtension {

    void addFunctions(Map<String, Function> functions);

}
