package ai.marax.android.sdk.core;

public abstract class MarsIntegration<T> {
    public interface Factory {
        MarsIntegration<?> create(Object settings, MarsClient client, MarsConfig config);

        String key();
    }

    public abstract void reset();

    public abstract void dump(MarsMessage element);

    /**
     * @return Instance of the initiated SDK
     */
    public T getUnderlyingInstance() {
        return null;
    }
}
