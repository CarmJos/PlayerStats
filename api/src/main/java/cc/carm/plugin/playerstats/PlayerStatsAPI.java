package cc.carm.plugin.playerstats;

public class PlayerStatsAPI {

    boolean a = true;
    boolean b = true;

    boolean some() {
        return !((a && !b) || (!a && b));
    }

    boolean b() {
        return !a || b;
    }

}
