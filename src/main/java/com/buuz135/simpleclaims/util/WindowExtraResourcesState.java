package com.buuz135.simpleclaims.util;

import com.hypixel.hytale.protocol.ExtraResources;
import com.hypixel.hytale.protocol.io.ChannelConnection;
import com.hypixel.hytale.server.core.io.netty.NettyUtil;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class WindowExtraResourcesState {
    private WindowExtraResourcesState() {}

    public static final AttributeKey<Map<Integer, ExtraResources>> EXTRA_BY_WINDOW_ID =
            AttributeKey.valueOf("simpleclaims_extra_by_window_id");

    public static final AttributeKey<ExtraResources> NEXT_OPEN_EXTRA =
            AttributeKey.valueOf("simpleclaims_next_open_extra");

    public static final AttributeKey<Set<Integer>> BENCH_WINDOW_IDS =
            AttributeKey.valueOf("simpleclaims_bench_window_ids");

    private static Field CHANNEL_FIELD;

    public static Channel getNettyChannel(ChannelConnection connection) {
        if (connection instanceof NettyUtil.NettyChannelConnection nettyChannelConnection)
            return nettyChannelConnection.channel();
        return null;
    }

    public static Set<Integer> getOrCreateBenchSet(ChannelConnection chConn) {
        Channel ch = getNettyChannel(chConn);
        if (ch == null) return null;
        Set<Integer> s = ch.attr(BENCH_WINDOW_IDS).get();
        if (s == null) {
            s = ConcurrentHashMap.newKeySet();
            ch.attr(BENCH_WINDOW_IDS).set(s);
        }
        return s;
    }

    public static Map<Integer, ExtraResources> getOrCreateMap(ChannelConnection chConn) {
        Channel ch = getNettyChannel(chConn);
        if (ch == null) return new ConcurrentHashMap<>();
        Map<Integer, ExtraResources> m = ch.attr(EXTRA_BY_WINDOW_ID).get();
        if (m == null) {
            m = new ConcurrentHashMap<>();
            ch.attr(EXTRA_BY_WINDOW_ID).set(m);
        }
        return m;
    }
}