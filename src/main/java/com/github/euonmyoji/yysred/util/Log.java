package com.github.euonmyoji.yysred.util;

import com.github.euonmyoji.yysred.RedPlugin;
import org.spongepowered.api.scheduler.Task;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * @author yinyangshi
 */
public class Log {
    private static final Path PATH = RedPlugin.plugin.cfgDir.resolve("logs");

    static {
        if (Files.notExists(PATH)) {
            try {
                Files.createDirectory(PATH);
            } catch (IOException e) {
                RedPlugin.logger.warn("create log dir error", e);
            }
        }
    }

    public static void log(String msg) {
        Task.builder().async().execute(() -> {
            synchronized (Log.class) {
                try {
                    try (PrintWriter out = new PrintWriter(Files.newBufferedWriter(PATH.resolve(getFileName()),
                            StandardOpenOption.CREATE, StandardOpenOption.APPEND))) {
                        out.println(getTime() + msg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    RedPlugin.logger.debug("记录log异常", e);
                }
            }
        }).submit(RedPlugin.plugin);
    }

    private static String getTime() {
        return String.format("[%s]", LocalTime.now());
    }

    private static String getFileName() {
        return LocalDate.now() + ".log";
    }
}
