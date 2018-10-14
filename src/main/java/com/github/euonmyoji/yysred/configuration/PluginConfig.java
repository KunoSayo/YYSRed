package com.github.euonmyoji.yysred.configuration;

import com.github.euonmyoji.yysred.RedPlugin;
import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

/**
 * @author yinyangshi
 */
public class PluginConfig {
    private static CommentedConfigurationNode cfg;
    private static ConfigurationLoader<CommentedConfigurationNode> loader;

    public static void init() {
        loader = HoconConfigurationLoader.builder()
                .setPath(RedPlugin.plugin.cfgDir.resolve("config.conf")).build();
        loadNode();

        TypeToken<String> type = new TypeToken<String>() {
        };
        CommentedConfigurationNode redSettings = cfg.getNode("red-settings");
        RedPlugin.ECONOMY_SERVICE.getCurrencies().forEach(currency -> {
            CommentedConfigurationNode currencyNode = redSettings.getNode(currency.getId());
            currencyNode.getNode("min-everyoneGet").getInt(Integer.MAX_VALUE);
            currencyNode.getNode("max-everyoneGet").getInt(Integer.MAX_VALUE);
            currencyNode.getNode("min-perRedTotal").getInt(Integer.MAX_VALUE);
            currencyNode.getNode("max-perRedTotal").getInt(Integer.MAX_VALUE);
            currencyNode.getNode("min-reds").getInt(Integer.MAX_VALUE);
            currencyNode.getNode("max-reds").getInt(Integer.MAX_VALUE);
            try {
                currencyNode.getNode("sendPermissions").getList(type, new ArrayList<String>() {{
                    add("yysred.send." + currency.getId());
                }});
                currencyNode.getNode("getPermissions").getList(type, new ArrayList<String>() {{
                    add("yysred.get." + currency.getId());
                }});
                currencyNode.getNode("allow-types").getList(type, new ArrayList<String>() {{

                }});
            } catch (ObjectMappingException e) {
                RedPlugin.logger.warn("ObjectMappingException while parsing currency settings:" + currency.getId(), e);
            }
        });

        cfg.getNode("lang").getString("zh_CN");

        save();
    }


    private static void loadNode() {
        try {
            cfg = loader.load(ConfigurationOptions.defaults().setShouldCopyDefaults(true));
        } catch (IOException e) {
            RedPlugin.logger.warn("load plugin config failed, creating new one", e);
            cfg = loader.createEmptyNode(ConfigurationOptions.defaults());
        }
    }

    private static void save() {
        try {
            loader.save(cfg);
        } catch (IOException e) {
            RedPlugin.logger.warn("Saving plugin config error!", e);
        }
    }

    public static Locale getUsingLang() {
        String[] args = cfg.getNode("lang").getString(Locale.getDefault().toString()).split("_", 2);
        return new Locale(args[0], args[1]);
    }
}
