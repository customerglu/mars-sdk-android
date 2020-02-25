package ai.marax.android.sdk.core;

import android.text.TextUtils;

public class TrackPropertyBuilder extends MarsPropertyBuilder {
    private String category = null;

    public TrackPropertyBuilder setCategory(String category) {
        this.category = category;
        return this;
    }

    private String label = "";

    public TrackPropertyBuilder setLabel(String label) {
        this.label = label;
        return this;
    }

    private String value = "";

    public TrackPropertyBuilder setValue(String value) {
        this.value = value;
        return this;
    }

    @Override
    public MarsProperty build() {
        MarsProperty property = new MarsProperty();
        if (TextUtils.isEmpty(category)) {
            MarsLogger.logError("category can not be null or empty");
        } else {
            property.putValue("category", this.category);
            property.putValue("label", this.label);
            property.putValue("value", this.value);
        }
        return property;
    }
}
