package be.fcip.cms.util;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
public class CmsCommandUtils {

    public static CommandResponse exec(String fullCommand){
        File file = null;
        return exec(fullCommand, file);
    }
    public static CommandResponse exec(String fullCommand, File fileOuput){
        if(StringUtils.isEmpty(fullCommand)) throw new RuntimeException("command is null");
        ArrayList<String> commandList = new ArrayList<>(Arrays.asList(fullCommand.trim().split(" ")));
        String commandName = commandList.get(0);
        commandList.remove(0); // keep args
        return exec(commandName, commandList, fileOuput);
    }

    public static CommandResponse exec(String commandName, List<String> args){
        return exec(commandName, args, null);
    }

    public static CommandResponse exec(String commandName, List<String> args, File fileOuput) {
        List<String> commandList = new ArrayList<>();
        commandList.add(commandName);
        if(args != null) {
            commandList.addAll(args);
        }
        ProcessBuilder builder = new ProcessBuilder(commandList);
        if(fileOuput != null) {
            builder.redirectOutput(fileOuput);
        }
        try {
            Process p = builder.start();
            CompletableFuture<String> soutFut = readOutStream(p.getInputStream());
            CompletableFuture<String> serrFut = readOutStream(p.getErrorStream());

            // for automatic log
            //CompletableFuture<String> resultFut = soutFut.thenCombine(serrFut, (stdout, stderr) -> {
            // print to current stderr the stderr of process and return the stdout
            //    System.err.println(stderr);
            //    return stdout;
            //});
            int code = p.waitFor();
            try {
                return new CommandResponse(code, code == 0 ? soutFut.get() : serrFut.get());
            } catch (ExecutionException e) {
                return new CommandResponse(code, null);
            }
        } catch (IOException | InterruptedException e) {
            log.error("Error executing command : " + commandName, e);
        }
        return new CommandResponse(-1, null);
    }

    @AllArgsConstructor
    public static class CommandResponse{
        public int code = -1;
        private String output;

        public String getOutput() {
            return output == null ? "" : output;
        }

        public boolean hasError() {
            return code != 0;
        }
        public  boolean isSuccess(){
            return code == 0;
        }
    }

    static CompletableFuture<String> readOutStream(InputStream is) {
        return CompletableFuture.supplyAsync(() -> {
            try (
                    InputStreamReader isr = new InputStreamReader(is);
                    BufferedReader br = new BufferedReader(isr);
            ){
                StringBuilder res = new StringBuilder();
                String inputLine;
                while ((inputLine = br.readLine()) != null) {
                    res.append(inputLine).append(System.lineSeparator());
                }
                return res.toString();
            } catch (Throwable e) {
                throw new RuntimeException("problem with executing program", e);
            }
        });
    }
}
