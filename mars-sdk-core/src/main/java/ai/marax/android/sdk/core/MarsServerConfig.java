package ai.marax.android.sdk.core;

import com.google.gson.annotations.SerializedName;

import java.util.List;

class MarsServerConfig {
    @SerializedName("maraxId")
    String maraxId;
    @SerializedName("activeCampaigns")
    List<String>  campaigns;
    @SerializedName("activeOffers")
    List<String> offers;
    @SerializedName("updatedAt")
    String updatedAt;
}
