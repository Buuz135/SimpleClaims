package com.buuz135.simpleclaims.claim;

public class InteractionResult {
    private final boolean allowed;
    private final boolean blockedByHeight;
    private final int blockY;
    private final int minHeight;

    private InteractionResult(boolean allowed, boolean blockedByHeight, int blockY, int minHeight) {
        this.allowed = allowed;
        this.blockedByHeight = blockedByHeight;
        this.blockY = blockY;
        this.minHeight = minHeight;
    }

    public static InteractionResult allowed() {
        return new InteractionResult(true, false, 0, 0);
    }

    public static InteractionResult blockedByHeight(int blockY, int minHeight) {
        return new InteractionResult(false, true, blockY, minHeight);
    }

    public static InteractionResult blockedByPermission() {
        return new InteractionResult(false, false, 0, 0);
    }

    public boolean isAllowed() {
        return allowed;
    }

    public boolean isBlockedByHeight() {
        return blockedByHeight;
    }

    public int getBlockY() {
        return blockY;
    }

    public int getMinHeight() {
        return minHeight;
    }
}
