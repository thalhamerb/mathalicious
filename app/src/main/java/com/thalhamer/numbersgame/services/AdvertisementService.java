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
//        AdView mAdView = (AdView) findViewById(R.id.adView);
//        AdRequest adRequest = new AdRequest.Builder().build();
//        mAdView.loadAd(adRequest);

        //TODO switch to code above when ready to deploy (no longer use test ones)

        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)  // All emulators
                .addTestDevice("B14D1799161A1F2CD52AD9301DD68B37DE")  // My test phone
                .build();
        mAdView.loadAd(adRequest);
    }

    public void initInterstitialAd() {
        if (App.getmInterstitialAd() == null) {
            App.setmInterstitialAd(new InterstitialAd(App.getContext()));
            App.getmInterstitialAd().setAdUnitId(App.getContext().getResources().getString(R.string.banner_ad_unit_id));
        }
    }

    public void requestNewInterstitialAd() {
        //TODO switch out test ones when ready to deploy
        if (!App.getmInterstitialAd().isLoaded()) {
            AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)  // All emulators
                    .addTestDevice("D6431951621DB594FC023DCEFCEA6E44")  // My Galaxy Nexus test phone
                    .build();

            App.getmInterstitialAd().loadAd(adRequest);
        }
    }
}
