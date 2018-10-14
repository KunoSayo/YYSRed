package com.github.euonmyoji.yysred.util;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

/**
 * @author yinyangshi
 */
public class Util {

    public static Text toText(String s) {
        return TextSerializers.FORMATTING_CODE.deserialize(s);
    }
}
