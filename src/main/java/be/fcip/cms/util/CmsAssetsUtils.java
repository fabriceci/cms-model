package be.fcip.cms.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
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

    public static class ValidationException extends Exception{
        public ValidationException(final String message) {
            super(message);
        }
    }
    public static void validate(Path path) throws ValidationException{
        String extension = FilenameUtils.getExtension(path.getFileName().toString());
        switch (extension){
            case "scss":
            case "css":
                // check if shell command is present
                if(CmsCommandUtils.exec("command", Arrays.asList("-v", "sass")).isSuccess()){
                    CmsCommandUtils.CommandResponse sass = CmsCommandUtils.exec("sass", Arrays.asList("--no-source-map", path.toFile().getAbsolutePath()));
                    if(sass.hasError()){
                        throw new ValidationException(sass.getOutput());
                    }
                } else{
                    log.warn("Unable to make validation for (s)css, sass command not found");
                }
                break;

            case "js":
                if(CmsCommandUtils.exec("command", Arrays.asList("-v", "node")).isSuccess()){
                    CmsCommandUtils.CommandResponse node = CmsCommandUtils.exec("node", Arrays.asList("--check", path.toFile().getAbsolutePath()));
                    if(node.hasError()){
                        throw new ValidationException(node.getOutput());
                    }
                } else{
                    log.warn("Unable to make validation, node command not found");
                }
                break;

            default:
                log.warn("No validator for type : " + extension);
        }

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

    public static void minify(String name, List<String> filesParams){

        if(filesParams == null || filesParams.isEmpty()){
            throw new RuntimeException("FileList is empty");
        }
        List<String> files = new ArrayList<>(filesParams); // prevent UnsupportedOperationException from list created by Arrays.asList():
        String extension = null;
        boolean hasScss = false;
        List<String> scssFiles = null;
        // check if files exist & scss
        for (String s : files) {
            String fileExt = FilenameUtils.getExtension(s);
            if(extension == null){
                extension = FilenameUtils.getExtension(s);
                if(extension.equals("scss")){
                    extension = "css";
                }
                if(!Arrays.asList("css", "js", "scss").contains(extension)){
                    throw new RuntimeException("Only css/js file are allowed");
                }
            } else {
                if(extension.equals("js") && !extension.equals(fileExt)){
                    throw new RuntimeException(String.format("Extension of files list have to be the same (%s,%s)", extension, fileExt));
                } else if(!Arrays.asList("scss", "css").contains(fileExt)){
                    throw new RuntimeException(String.format("Extension of files list have to be the same (%s,%s)", extension, fileExt));
                }
            }
            if(!Paths.get(s).toFile().exists()){
                throw new RuntimeException(String.format("File '%s' not exist", s));
            }

            // handle scss
            if(fileExt.equals("scss")){
                if(scssFiles == null) scssFiles = new ArrayList<>();
                scssFiles.add(s);
                hasScss = true;
                continue;
            }

            files.add(s);
        }

        if(hasScss){
            // check if shell command is present
            if(CmsCommandUtils.exec("command", Arrays.asList("-v", "sass")).code != 0){
                throw new RuntimeException("command not found: sass");
            }
            for (String scssFile : scssFiles) {
                String output = FilenameUtils.getFullPath(scssFile) + "compiled/" + FilenameUtils.getBaseName(scssFile) + ".css";
                if (CmsCommandUtils.exec("sass", Arrays.asList("--no-source-map", scssFile, output)).hasError()) {
                    throw new RuntimeException("error during exec of command sass");
                }
                files.add(output);
            }
        }

        // minification
        Path minificationFolderPath = Paths.get(CmsAssetsUtils.ASSETS_PATH.substring(1) + "min");
        String filePath = minificationFolderPath.toFile().getAbsolutePath() + "/" + name + "-" + CmsUtils.CMS_MINIFY_NUMBER + "." + extension;


        // check if shell command is present
        if(CmsCommandUtils.exec("command", Arrays.asList("-v", "minify")).hasError()){
            throw new RuntimeException("command not found: minify");
        }

        if(CmsCommandUtils.exec("minify", files, new File(filePath)).hasError()){
            throw new RuntimeException("error during exec of command minify");
        }
        if(extension != null && extension.equals("css")) {
            if(CmsCommandUtils.exec("command", Arrays.asList("-v","postcss")).hasError()){
                throw new RuntimeException("command not found: postcss");
            }
            if (CmsCommandUtils.exec("postcss", Arrays.asList(filePath, "--use", "autoprefixer", "cssnano", "-r", "--no-map"), null).code != 0) {
                throw new RuntimeException("error during exec of command postcss");
            }
        }
    }
}
