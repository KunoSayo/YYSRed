package com.github.euonmyoji.yysred.data;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.text.Text;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static org.spongepowered.api.text.Text.of;

/**
 * @author yinyangshi
 */
public interface Red {
    /**
     * 领取红包
     *
     * @param p    谁领
     * @param args 领取红包时玩家给的参数
     */
    void getRed(Player p, String[] args);

    /**
     * 获得谁发了这个红包
     *
     * @return uuid
     */
    UUID getWhoSend();

    /**
     * 获得谁发了这个红包
     *
     * @return 名字
     */
    String getWhoSendName();

    /**
     * 获得货币
     *
     * @return 货币
     */
    Currency getCurrency();

    /**
     * 获得红包发送时间
     *
     * @return 红包发送时间
     */
    LocalDateTime getSendTime();

    /**
     * 获得剩余红包数量
     *
     * @return 红包剩余数量
     */
    int getLeft();

    /**
     * 获得红包总数
     *
     * @return 红包总数
     */
    int getTotalReds();

    /**
     * 获得红包总金额
     *
     * @return total money
     */
    int getTotalMoney();

    /**
     * 获得红包相关信息
     *
     * @return 红包相关信息
     */
    default Text getGetInfo() {
        return Text.builder().append(of("----------------" + getType().getDisplayName() + "----------------" +
                "\n•红包发送者:" + getWhoSendName() +
                "，红包发送时间:" + getSendTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) +
                "\n•红包总金额:" + getTotalMoney())).append(getCurrency().getSymbol())
                .append(of("\n•红包数量:" + getLeft() + "/" + getTotalReds() +
                        "\n----------------------------------------")).build();
    }

    /**
     * 获得红包类型
     *
     * @return 红包类型
     */
    Type getType();

    String getID();

    /**
     * 取消红包 并且根据设置决定是否退钱
     */
    void cancel();
}
