package be.fcip.cms.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
public class CmsAssetsUtils {

    private final static String CSS_MODEL_BEGIN = "<link rel=\"stylesheet\" type=\"text/css\" href=\"";
    private final static String CSS_MODEL_END = "\">";
    private final static String JS_MODEL_BEGIN = "<script src=\"";
    private final static String JS_MODEL_END = "\"></script>";
    public final static String ASSETS_PATH = "/public/assets/";
    private static final Pattern minifyPattern = Pattern.compile("(?:.*)?-([0-9]+)");

    public static String  getAssets(String name){
        if(StringUtils.isEmpty(name)) return null;
        return doWork(name);
    }

    public static String getAssets(List<String> list) {

        StringBuilder sb = new StringBuilder();
        for (String s : list) {
            sb.append(doWork(s)).append('\n');
        }
        return sb.toString();
    }

    private static String doWork(final String name){
        if(name.endsWith("js")){
            return getJs(name);
        } else if(name.endsWith("css")){
            return getCss(name);
        } else{
            return getAssetPath(name);
        }
    }

    public static String getJs(final String name){
        if(name == null) return null;
        return JS_MODEL_BEGIN + ASSETS_PATH + name + "?" + CmsUtils.CMS_UNIQUE_NUMBER + JS_MODEL_END;
    }

    public static String getCss(final String name){
        if(name == null) return null;
        return CSS_MODEL_BEGIN + ASSETS_PATH + name + "?" + CmsUtils.CMS_UNIQUE_NUMBER + CSS_MODEL_END;
    }

    public static String getAssetPath(final String name){
        if(name == null) return null;
        return WebConfigConstants.RESOURCES_LOCATION + name;
    }

    public static void setupMinify() {
        try {
            Path path = Paths.get(CmsAssetsUtils.ASSETS_PATH.substring(1) + "min");
            Files.createDirectories(path);
            List<File> collect = Files.list(path).map(Path::toFile).collect(Collectors.toList());

            for (File file : collect) {
                String baseName = FilenameUtils.getBaseName(file.getName());
                Matcher matcher = minifyPattern.matcher(baseName);
                if(matcher.find()){
                    CmsUtils.CMS_MINIFY_NUMBER = Long.parseLong(matcher.group(1));
                    break;
                }
            }
        } catch (IOException e){
            log.error("Error creating the minify path", e);
        }

        if(CmsUtils.CMS_MINIFY_NUMBER == null){
            CmsUtils.CMS_MINIFY_NUMBER = new Date().getTime();
        }
    }

    public static void reloadMinify() {
        try {
            Path path = Paths.get(CmsAssetsUtils.ASSETS_PATH.substring(1) + "min");
            List<File> collect = Files.list(path).map(Path::toFile).collect(Collectors.toList());
            List<File> toDelete = new ArrayList<>();
            // delete old minification
            for (File file : collect) {
                String baseName = FilenameUtils.getBaseName(file.getName());
                Matcher matcher = minifyPattern.matcher(baseName);
                if(matcher.find()){
                    Long number = Long.parseLong(matcher.group(1));
                    if(number.equals(CmsUtils.CMS_MINIFY_NUMBER)){
                        toDelete.add(file);
                    }
                    break;
                }
            }
            for (File file : toDelete) {
                file.delete();
            }
        } catch (IOException e) {
            log.error("Error cleaning the minify path", e);
        }

        CmsUtils.CMS_MINIFY_NUMBER = new Date().getTime();
    }

    public static void minify(String name, List<String> fileList){

        if(fileList == null || fileList.isEmpty()){
            throw new RuntimeException("FileList is empty");
        }

        String extension = null;
        // check if files exist
        for (String s : fileList) {
            if(extension == null){
                extension = FilenameUtils.getExtension(s);
                if(!Arrays.asList("css", "js").contains(extension)){
                    throw new RuntimeException("Only css/js file are allowed");
                }
            } else {
                if(!extension.equals(FilenameUtils.getExtension(s))){
                    throw new RuntimeException(String.format("Extension of files list have to be the same (%s,%s)", extension, FilenameUtils.getExtension(s)));
                }
            }
            if(!Paths.get(s).toFile().exists()){
                throw new RuntimeException(String.format("File '%s' not exist", s));
            }
        }

        // minification
        Path minificationFolderPath = Paths.get(CmsAssetsUtils.ASSETS_PATH.substring(1) + "min");
        String filePath = minificationFolderPath.toFile().getAbsolutePath() + "/" + name + "-" + CmsUtils.CMS_MINIFY_NUMBER + "." + extension;


        // check if shell command is present
        if(runProcess("command", Arrays.asList("-v", "minify"), null) != 0){
            throw new RuntimeException("command not found: minify");
        }

        if(runProcess("minify", fileList, new File(filePath)) != 0){
            throw new RuntimeException("error during exec of command minify");
        }
        if(extension.equals("css")) {
            if(runProcess("command", Arrays.asList("-v","postcss"), null) != 0){
                throw new RuntimeException("command not found: postcss");
            }
            if (runProcess("postcss", Arrays.asList(filePath, "--use", "autoprefixer", "cssnano", "-r", "--no-map"), null) != 0) {
                throw new RuntimeException("rror during exec of command postcss");
            }
        }
    }


    private static int runProcess(String command, List<String> args, File output) {
        List<String> commandList = new ArrayList<>();
        commandList.add(command);
        if(args != null) {
            commandList.addAll(args);
        }
        ProcessBuilder builder = new ProcessBuilder(commandList);
        if(output != null) {
            builder.redirectOutput(output);
        }
        try {
            return builder.start().waitFor();
        } catch (IOException | InterruptedException e) {
            log.error("Error executing command : " + command, e);
        }
        return -1;
    }
    private static int runProcess(String command){
        try {
            Process process = Runtime.getRuntime().exec(command);

            StringBuilder output = new StringBuilder();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            int i = process.waitFor();
            if(i != 0){
                log.error("Error executing command " + command + ": \n: " + output.toString() );
            }
            return i;
        } catch(Exception e){log.error("Error executing command : " + command, e); }
        return -1;
    }
}
