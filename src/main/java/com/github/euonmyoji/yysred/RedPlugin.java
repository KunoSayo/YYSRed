package com.github.euonmyoji.yysred;

import com.github.euonmyoji.yysred.data.Red;
import com.google.inject.Inject;
import org.bstats.sponge.Metrics2;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.economy.EconomyService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

/**
 * @author yinyangshi
 */
@Plugin(id = "yysred", name = "Yinyangshi Red Plugin", version = RedPlugin.VERSION, authors = "yinyangshi",
        description = "Red Plugin")
public class RedPlugin {
    public static RedPlugin plugin;
    static final String VERSION = "${VERSION}";

    public static final EconomyService ECONOMY_SERVICE = Sponge.getServiceManager().provide(EconomyService.class).orElseThrow(NoSuchFieldError::new);

    private static final HashMap<String, Red> CACHE = new HashMap<>();

    public static void remove(Red red) {
        synchronized (CACHE) {
            CACHE.remove(red.getID());
        }
    }

    public static void add(Red red) {
        synchronized (CACHE) {
            if (CACHE.containsKey(red.getID())) {
                throw new IllegalArgumentException("The red id is present!");
            }
            CACHE.put(red.getID(), red);
        }
    }

    @Inject
    @ConfigDir(sharedRoot = false)
    public Path cfgDir;

    public static Logger logger;

    @Inject
    public void setLogger(Logger l) {
        logger = l;
    }

    @Inject
    private Metrics2 metrics;

    @Listener
    public void onPreInit(GamePreInitializationEvent event) {
        plugin = this;
        try {
            Files.createDirectories(cfgDir);
        } catch (IOException e) {
            logger.warn("Creating dir failed", e);
        }
    }

    @Listener
    public void onStarted(GameStartedServerEvent event) {
        Task.builder().async().execute(() -> {
            synchronized (CACHE) {

            }
        }).intervalTicks(20).submit(this);
    }
}
