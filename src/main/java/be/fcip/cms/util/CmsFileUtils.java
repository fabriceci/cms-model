package be.fcip.cms.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class CmsFileUtils {

    public final static String UPLOAD_DIRECTORY_PRIVATE = "private/";
    public final static String UPLOAD_DIRECTORY_PUBLIC = "public/";

    public static String getFilePath(File file, String limit) {

        File parent = file.getParentFile();
        if (limit.equals(parent.getName())) {
            return "";
        } else {
            return getFilePath(parent, limit) + "/" + parent.getName();
        }
    }

    public static File uploadFile(MultipartFile uploadFile, boolean isPrivate) throws IOException {
        return uploadFile(uploadFile, isPrivate, null);
    }
    public static File uploadFile(MultipartFile uploadFile, boolean isPrivate, String prePath) throws IOException {
        return uploadFile(uploadFile, isPrivate, prePath, null);
    }

    public static File uploadFile(MultipartFile uploadFile, boolean isPrivate, String prePath, String baseName) throws IOException {

        String ext = FilenameUtils.getExtension(uploadFile.getOriginalFilename());

        if(StringUtils.isEmpty(baseName)) {
            baseName = FilenameUtils.getBaseName(uploadFile.getOriginalFilename());
        }
        String prefix = CmsStringUtils.toSlug(baseName);
        File serverFile = null;
        if (isPrivate) {
            if (prefix.length() < 3) {
                prefix += "___";
            }
            serverFile = File.createTempFile(prefix, "." + ext, getUploadDirectory(true));
        } else {
            String filePath = getUploadDirectory(false).getAbsolutePath();
            if (!StringUtils.isEmpty(prePath)) {
                filePath += "/" + prePath;
            }
            filePath += "/" + prefix + "." + ext;
            serverFile = new File(filePath);
        }
        serverFile.getParentFile().mkdirs();
        BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
        stream.write(uploadFile.getBytes());
        stream.close();
        return serverFile;
    }

    public static File getUploadDirectory(boolean isPrivate) {
        // check if upload path exist
        File fileDir = new File((isPrivate) ? UPLOAD_DIRECTORY_PRIVATE : UPLOAD_DIRECTORY_PUBLIC);

        if (!fileDir.exists()) {
            try {
                FileUtils.forceMkdir(fileDir);
            } catch (IOException e) {
                log.error("Impossible to create the upload directory : " + fileDir.getPath(), e);
                return null;
            }
        }
        return fileDir;
    }


    /**
     * Attempts to calculate the size of a file or directory.
     *
     * <p> Since the operation is non-atomic, the returned value may be inaccurate.
     * However, this method is quick and does its best.
     */
    public static long size (Path path) {

        final AtomicLong size = new AtomicLong(0);

        try
        {
            Files.walkFileTree (path, new SimpleFileVisitor<Path>()
            {
                @Override public FileVisitResult
                visitFile(Path file, BasicFileAttributes attrs) {

                    size.addAndGet (attrs.size());
                    return FileVisitResult.CONTINUE;
                }

                @Override public FileVisitResult
                visitFileFailed(Path file, IOException exc) {

                    System.out.println("skipped: " + file + " (" + exc + ")");
                    // Skip folders that can't be traversed
                    return FileVisitResult.CONTINUE;
                }

                @Override public FileVisitResult
                postVisitDirectory (Path dir, IOException exc) {

                    if (exc != null)
                        System.out.println("had trouble traversing: " + dir + " (" + exc + ")");
                    // Ignore errors traversing a folder
                    return FileVisitResult.CONTINUE;
                }
            });
        }
        catch (IOException e)
        {
            throw new AssertionError ("walkFileTree will not throw IOException if the FileVisitor does not");
        }

        return size.get();
    }


}
