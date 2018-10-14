package com.github.euonmyoji.yysred.manager;

import com.github.euonmyoji.yysred.RedPlugin;
import com.github.euonmyoji.yysred.configuration.PluginConfig;
import com.google.common.base.Charsets;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.text.Text;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

/**
 * @author yinyangshi
 */
public class LanguageManager {
    private static Locale locale;
    private static Path langFile;
    private static ResourceBundle res;

    public static String getString(String key, String def) {
        String s = getStringSafely(key);
        //noinspection StringEquality  就是看引用是不是一样的()
        return key == s ? def : s;
    }

    private static void init() {
        try {
            Path langFolder = RedPlugin.plugin.cfgDir.resolve("lang");
            if (Files.notExists(langFolder)) {
                Files.createDirectory(langFolder);
            }
            try {
                if (Files.notExists(langFile)) {
                    Sponge.getAssetManager().getAsset(RedPlugin.plugin, "lang/" + locale.toString().toLowerCase() + ".lang")
                            .orElseThrow(() -> new FileNotFoundException("asset didn't found locale language file!"))
                            .copyToFile(langFile);
                }
            } catch (FileNotFoundException ignore) {
                RedPlugin.logger.info("locale language file not found");
                langFile = RedPlugin.plugin.cfgDir.resolve("lang").resolve(Locale.CHINA.toString().toLowerCase() + ".lang");
                Sponge.getAssetManager().getAsset(RedPlugin.plugin, "lang/" + Locale.CHINA.toString().toLowerCase() + ".lang")
                        .orElseThrow(() -> new IOException("asset didn't found language file!"))
                        .copyToFile(langFile);
            }
        } catch (IOException e) {
            RedPlugin.logger.error("IOE", e);
        }
    }

    public static void reload() {
        try {
            locale = PluginConfig.getUsingLang();
            langFile = RedPlugin.plugin.cfgDir.resolve("lang").resolve(locale.toString() + ".lang");
            init();
            res = new PropertyResourceBundle(new InputStreamReader(Files.newInputStream(langFile), Charsets.UTF_8));
        } catch (IOException e) {
            RedPlugin.logger.error("reload language file error!", e);
        }
    }

    private static String getStringSafely(String key) {
        try {
            return res.getString(key);
        } catch (Exception e) {
            return key;
        }
    }

    private LanguageManager() {
        throw new UnsupportedOperationException();
    }
}
