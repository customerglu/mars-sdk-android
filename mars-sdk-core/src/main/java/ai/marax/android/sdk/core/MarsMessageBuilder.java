package ai.marax.android.sdk.core;

import java.util.Map;

/*
 * builder for MarsElement (alias MarsEvent)
 * */
public class MarsMessageBuilder {
    private String eventName = null;

    public MarsMessageBuilder setEventName(String eventName) {
        this.eventName = eventName;
        return this;
    }


    private String userId = null;

    public MarsMessageBuilder setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    private MarsProperty property;

    public MarsMessageBuilder setProperty(MarsProperty property) {
        this.property = property;
        return this;
    }

    public MarsMessageBuilder setProperty(MarsPropertyBuilder builder) {
        this.property = builder.build();
        return this;
    }

    public MarsMessageBuilder setProperty(Map<String, Object> map) {
        if (this.property == null) property = new MarsProperty();
        property.putValue(map);
        return this;
    }

    private MarsUserProperty userProperty;

    public MarsMessageBuilder setUserProperty(MarsUserProperty userProperty) {
        this.userProperty = userProperty;
        return this;
    }

    public MarsMessageBuilder setUserProperty(Map<String, Object> map) {
        this.userProperty = new MarsUserProperty();
        userProperty.putValue(map);
        return this;
    }

    // TODO:  need to figure out to use it as integrations
    private MarsOption option;

    public MarsMessageBuilder setMarsOption(MarsOption option) {
        this.option = option;
        return this;
    }

    public MarsMessage build() {
        MarsMessage event = new MarsMessage();
        if (this.userId != null) event.setUserId(this.userId);
//        if (this.maraxId != null) event.setMaraxId(this.maraxId);
        if (this.eventName != null) event.setEventName(this.eventName);
        if (this.property != null) event.setProperty(this.property);
        if (this.userProperty != null) event.setUserProperty(this.userProperty);
        return event;
    }
}

