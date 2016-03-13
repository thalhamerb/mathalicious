package com.thalhamer.numbersgame.services;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.thalhamer.numbersgame.Factory.App;
import com.thalhamer.numbersgame.R;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by Brian on 11/14/2015.
 */
@Singleton
public class AdvertisementService {

    @Inject //to make injectable in dagger
    public AdvertisementService() {
    }

    public void initAdBanner(AdView mAdView) {
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

//        AdRequest adRequest = new AdRequest.Builder()
//                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)  // All emulators
//                .addTestDevice("B14D1799161A1F2CD52AD9301DD68B37DE")  // My test phone
//                .addTestDevice("D6431951621DB594FC023DCEFCEA6E44")
//                .addTestDevice("AF2BE04100A2E6AA655D6095FBC108E5")
//                .build();

        mAdView.loadAd(adRequest);
    }

    public void initInterstitialAd() {
        if (App.getmInterstitialAd() == null) {
            App.setmInterstitialAd(new InterstitialAd(App.getContext()));
            App.getmInterstitialAd().setAdUnitId(App.getContext().getResources().getString(R.string.interstitial_ad_unit_id));
        }
    }

    public void requestNewInterstitialAd() {
        initInterstitialAd();
        if (!App.getmInterstitialAd().isLoading() && !App.getmInterstitialAd().isLoaded()) {
            AdRequest adRequest = new AdRequest.Builder().build();

            App.getmInterstitialAd().loadAd(adRequest);
        }
    }
}
