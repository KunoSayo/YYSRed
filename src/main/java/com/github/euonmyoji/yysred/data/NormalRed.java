package com.github.euonmyoji.yysred.data;

import com.github.euonmyoji.yysred.RedPlugin;
import com.github.euonmyoji.yysred.util.Log;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.text.Text;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.spongepowered.api.text.Text.of;

/**
 * @author yinyangshi
 */
public class NormalRed implements Red {
    private String id;
    private String fromName;
    private UUID from;
    private int leftRed;
    private int totalRed;
    private int totalMoney;
    private BigDecimal everyoneGet;
    private Currency currency;
    private List<String> needPermissions;
    private LocalDateTime sendTime = LocalDateTime.now();

    public NormalRed(User from, String id, Currency currency, int totalMoney, int totalRed, List<String> needPermissions) {
        this.id = id;
        this.from = from.getUniqueId();
        this.fromName = from.getName();
        this.totalMoney = totalMoney;
        this.currency = currency;
        this.totalRed = totalRed;
        this.needPermissions = needPermissions;
        leftRed = totalRed;
        everyoneGet = BigDecimal.valueOf(totalMoney / totalRed);
        if (totalMoney % totalRed != 0) {
            throw new IllegalArgumentException("Everyone get must be int!");
        }
    }

    @Override
    public synchronized void getRed(Player p, String[] args) {
        if (leftRed <= 0) {
            p.sendMessage(of("[红包]这个红包已经没有了"));
        } else if (needPermissions.stream().noneMatch(s -> p.hasPermission(s) && p.getPermissionValue(p.getActiveContexts(), s).asBoolean())) {
            p.sendMessage(of("[红包]你没有权限领取这个红包(需要拥有权限并且权限值为true)"));
        } else {
            Optional<UniqueAccount> optAccount = RedPlugin.ECONOMY_SERVICE.getOrCreateAccount(p.getUniqueId());
            if (optAccount.isPresent()) {
                UniqueAccount account = optAccount.get();
                ResultType result = account.deposit(currency, everyoneGet, Cause.builder().append(RedPlugin.plugin).build(EventContext.empty())).getResult();
                if (result == ResultType.SUCCESS) {
                    p.sendMessage(Text.builder().append(of("[红包]你获得了" + everyoneGet)).append(currency.getSymbol()).build());
                    Log.log(String.format("%s获得了%s的红包的%s%s", p.getName(), fromName, everyoneGet, currency.getSymbol().toPlain()));
                    leftRed--;
                    if (leftRed <= 0) {
                        cancel();
                    }
                } else {
                    RedPlugin.logger.info("The deposit result of {} is not successful! it's {}", fromName, result);
                    p.sendMessage(of("[红包]领取红包失败 " + result));
                }
            } else {
                p.sendMessage(of("[红包]无法获得你的经济账户"));
            }
        }
    }

    @Override
    public void cancel() {
        if (leftRed > 0) {
            BigDecimal returnValue = everyoneGet.multiply(BigDecimal.valueOf(leftRed));
            Optional<UniqueAccount> optAccount = RedPlugin.ECONOMY_SERVICE.getOrCreateAccount(from);
            if (optAccount.isPresent()) {
                UniqueAccount account = optAccount.get();
                ResultType result = account.deposit(currency, returnValue,
                        Cause.builder().append(RedPlugin.plugin).build(EventContext.empty())).getResult();
                Sponge.getServer().getPlayer(from)
                        .ifPresent(player -> player.sendMessage(of("[红包]由于你的红包长时间没人领完，已将未领完金额退还给你。")));
                if (result == ResultType.SUCCESS) {
                    Sponge.getServer().getPlayer(from)
                            .ifPresent(player -> player.sendMessage(of("[红包]退还" + returnValue + currency.getSymbol().toPlain() + "成功")));
                } else {
                    Sponge.getServer().getPlayer(from)
                            .ifPresent(player -> player.sendMessage(of(
                                    "[红包]退还失败！" + result + "，应退还金额:" + returnValue)));
                    RedPlugin.logger.warn("The deposit result of {} is not successful! it's {}, should return {}", fromName, result, returnValue);
                }
            } else {
                RedPlugin.logger.warn("Try to return the money to {}, but can't found his account!  UUID:{}, should return {}", fromName, from, returnValue);
            }
        }

    }

    @Override
    public UUID getWhoSend() {
        return from;
    }

    @Override
    public String getWhoSendName() {
        return fromName;
    }

    @Override
    public Currency getCurrency() {
        return currency;
    }

    @Override
    public LocalDateTime getSendTime() {
        return sendTime;
    }

    @Override
    public synchronized int getLeft() {
        return leftRed;
    }

    @Override
    public synchronized int getTotalReds() {
        return totalRed;
    }

    @Override
    public int getTotalMoney() {
        return totalMoney;
    }

    @Override
    public Type getType() {
        return Type.NORMAL;
    }

    @Override
    public String getID() {
        return id;
    }
}
