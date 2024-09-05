package cc.carm.plugin.playerstats.data;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public abstract class NumericStatsType<N extends Number> extends StatsType<N> {

    public static NumericStatsType<Long> ofLong(int id, String key, Long defaultValue) {
        return new NumericStatsType<Long>(id, key, Long.class, defaultValue) {
            @Override
            public @NotNull Long parse(@NotNull String data) {
                return Long.parseLong(data);
            }
        };
    }

    public static NumericStatsType<Integer> ofInteger(int id, String key, Integer defaultValue) {
        return new NumericStatsType<Integer>(id, key, Integer.class, defaultValue) {
            @Override
            public @NotNull Integer parse(@NotNull String data) {
                return Integer.parseInt(data);
            }
        };
    }

    public static NumericStatsType<Double> ofDouble(int id, String key, Double defaultValue) {
        return new NumericStatsType<Double>(id, key, Double.class, defaultValue) {
            @Override
            public @NotNull Double parse(@NotNull String data) {
                return Double.parseDouble(data);
            }
        };
    }

    public static NumericStatsType<Float> ofFloat(int id, String key, Float defaultValue) {
        return new NumericStatsType<Float>(id, key, Float.class, defaultValue) {
            @Override
            public @NotNull Float parse(@NotNull String data) {
                return Float.parseFloat(data);
            }
        };
    }

    protected NumericStatsType(int id, String key, Class<N> valueClass, N defaultValue) {
        super(id, key, valueClass, defaultValue);
    }

    @Override
    public @Nullable String serialize(@NotNull N data) {
        return Objects.toString(data);
    }

    @Override
    public int compare(N number1, N number2) {
        return Double.compare(number1.doubleValue(), number2.doubleValue());
    }
}
