package ai.marax.android.sdk.core;

import android.text.TextUtils;

public class ScreenPropertyBuilder extends MarsPropertyBuilder {
    private String name;

    public ScreenPropertyBuilder setScreenName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public MarsProperty build() {
        MarsProperty property = new MarsProperty();
        if (TextUtils.isEmpty(name)) {
            MarsLogger.logError("name can not be empty");
        } else {
            property.put("name", name);
        }
        return property;
    }
}
