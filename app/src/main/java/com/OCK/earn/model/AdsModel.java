package com.OCK.earn.model;

public class AdsModel {
    String AppID, banner, interstitial;

    AdsModel() {

    }

    AdsModel(String AppID, String banner, String interstitial) {

        this.AppID = AppID;
        this.banner = banner;
        this.interstitial = interstitial;

    }

    public String getAppID() {
        return AppID;
    }

    public void setAppID(String appID) {
        this.AppID = appID;
    }

    public String getBanner() {
        return banner;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }

    public String getInterstitial() {
        return interstitial;
    }

    public void setInterstitial(String interstitial) {
        this.interstitial = interstitial;
    }
}
