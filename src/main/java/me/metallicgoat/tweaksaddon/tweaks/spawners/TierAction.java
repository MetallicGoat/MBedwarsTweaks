package me.metallicgoat.tweaksaddon.tweaks.spawners;

public enum TierAction {
    GEN_UPGRADE,
    BED_DESTROY,
    GAME_OVER;

    public String getId() {
        switch (this) {
            case GEN_UPGRADE:
                return "gen-upgrade";
            case BED_DESTROY:
                return "bed-destroy";
            case GAME_OVER:
                return "game-over";
            default:
                return null;
        }
    }
}
