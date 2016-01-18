package com.thalhamer.numbersgame.services;import android.app.Activity;import android.app.AlertDialog;import android.util.Log;import android.view.View;import android.widget.ImageView;import android.widget.TextView;import com.google.common.collect.Lists;import com.thalhamer.numbersgame.Factory.App;import com.thalhamer.numbersgame.R;import com.thalhamer.numbersgame.domain.IapPower;import com.thalhamer.numbersgame.enums.PowerEnum;import com.thalhamer.numbersgame.enums.sounds.SoundEnum;import com.thalhamer.numbersgame.util.IabHelper;import com.thalhamer.numbersgame.util.IabResult;import com.thalhamer.numbersgame.util.Inventory;import com.thalhamer.numbersgame.util.Purchase;import com.thalhamer.numbersgame.util.SkuDetails;import java.math.BigInteger;import java.security.SecureRandom;import java.util.List;import javax.inject.Inject;import javax.inject.Singleton;/** * in app purchase service * <p/> * Created by Brian on 9/25/2015. */@Singletonpublic class InAppPurchaseService {    public static final int REQUEST_CODE = 10275;    private SecureRandom random = new SecureRandom();    private Inventory inventory;    @Inject    SoundService soundService;    @Inject    PowerService powerService;    public IabHelper getInAppPurchaseHelper(final Activity activity) {        final IabHelper mHelper = new IabHelper(activity, getAppPublicKey());        // enable debug logging ONLY if debugging.        mHelper.enableDebugLogging(false);        // Start setup. This is asynchronous and the specified listener        // will be called once setup completes.        Log.d(activity.getLocalClassName(), "Starting setup asyncronously.");        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {            public void onIabSetupFinished(IabResult result) {                Log.d(activity.getLocalClassName(), "Setup finished async.");                if (!result.isSuccess()) {                    String message = "Problem setting up in-app billing: " + result;                    Log.d(activity.getLocalClassName(), message);                    alertUser(activity, message);                    App.setConnectedToPurchasingNetwork(false);                    return;                }                // Have we been disposed of in the meantime? If so, quit.                if (mHelper == null) return;                App.setConnectedToPurchasingNetwork(true);                consumeAnyUnconsumedPurchases(activity, mHelper);            }        });        return mHelper;    }    //do this on app startup    public void consumeAnyUnconsumedPurchases(final Activity activity, IabHelper mHelper) {        mHelper.queryInventoryAsync(createConsumeUnconsumedListener(activity, mHelper));    }    private IabHelper.QueryInventoryFinishedListener createConsumeUnconsumedListener(final Activity activity, final IabHelper mHelper) {        return new IabHelper.QueryInventoryFinishedListener() {            public void onQueryInventoryFinished(IabResult result, Inventory inventory) {                if (result.isSuccess()) {                    InAppPurchaseService.this.inventory = inventory;                    //check if has unconsumed purchases that should be consumed and consumes if finds any                    for (PowerEnum powerEnum : PowerEnum.values()) {                        for (int i = 1; i <= 3; i++) {                            if (inventory.hasPurchase(powerEnum.getAppPurchaseSKU() + i)) {                                String skuName = powerEnum.getAppPurchaseSKU() + i;                                Purchase purchase = inventory.getPurchase(skuName);                                if (verifyDeveloperPayload(purchase, App.getContext().getString(R.string.purchase_payload))) {                                    mHelper.consumeAsync(purchase, createOnConsumeListener(mHelper, activity));                                    mHelper.flagEndAsync();                                }                            }                        }                    }                } else {                    Log.d(activity.getLocalClassName(), "Querying inventory to update consumable purchaces failed.");                }            }        };    }    public void setPowerBuyDetails(final Activity activity, IabHelper mHelper, List<IapPower> iapPowers) {        List<String> skulist = Lists.newArrayList();        for (IapPower iapPower : iapPowers) {            View powerView = iapPower.getPowerView();            PowerEnum powerEnum = iapPower.getPowerEnum();            setWaitScreen(true, powerView);            skulist.add(powerEnum.getAppPurchaseSKU() + 1);            skulist.add(powerEnum.getAppPurchaseSKU() + 2);            skulist.add(powerEnum.getAppPurchaseSKU() + 3);        }        if (mHelper == null) return;        mHelper.flagEndAsync();        mHelper.queryInventoryAsync(true, skulist, createPowerBuyDetailsListener(activity, mHelper, iapPowers));    }    public IabHelper.QueryInventoryFinishedListener createPowerBuyDetailsListener(final Activity activity, final IabHelper mHelper, final List<IapPower> iapPowers) {        return new IabHelper.QueryInventoryFinishedListener() {            public void onQueryInventoryFinished(IabResult result, Inventory inventory) {                if (result.isSuccess()) {                    for (IapPower iapPower : iapPowers) {                        View powerView = iapPower.getPowerView();                        final PowerEnum powerEnum = iapPower.getPowerEnum();                        //set details for each power button                        for (int i = 1; i <= 3; i++) {                            SkuDetails skuDetails = inventory.getSkuDetails(powerEnum.getAppPurchaseSKU() + i);                            if (i == 1) {  //only care about the first one since the title and description is common to all of the in app purchases                                TextView title = (TextView) powerView.findViewById(R.id.title);                                title.setText(iapPower.getPowerEnum().getBuyPowerTitle());                                TextView description = (TextView) powerView.findViewById(R.id.description);                                description.setVisibility(View.VISIBLE);                                description.setText(skuDetails.getDescription());                            }                            if (skuDetails == null) {                                setWaitScreen(true, powerView);                                ((TextView) powerView.findViewById(R.id.waitView)).setText("Error retrieving data from store.");                            } else {                                int resID = App.getContext().getResources().getIdentifier("buyImage" + i, "id", App.getContext().getPackageName());                                View buttonView = powerView.findViewById(resID);                                TextView quantityView = (TextView) buttonView.findViewById(R.id.buyQuantity);                                int quantity = getPowerQuantityFromSkuDetails(skuDetails);                                quantityView.setText(Integer.toString(quantity));                                TextView amountView = (TextView) buttonView.findViewById(R.id.buyAmount);                                amountView.setText(skuDetails.getPrice());                                ImageView powerImage = (ImageView) buttonView.findViewById(R.id.buttonPowerImage);                                powerImage.setBackgroundResource(powerEnum.getImageResourceId());                                buttonView.setTag(skuDetails.getSku());                                buttonView.setOnClickListener(new View.OnClickListener() {                                    @Override                                    public void onClick(View v) {                                        SoundEnum.CLICK1.getMediaPlayer().start();                                        buyPower(mHelper, activity, powerEnum, (String) v.getTag());                                    }                                });                                setWaitScreen(false, powerView);                            }                        }                    }                    InAppPurchaseService.this.inventory = inventory;                } else {                    for (IapPower iapPower : iapPowers) {                        View powerView = iapPower.getPowerView();                        String text = "Could not connect to store.  Make sure airplane mode is off or wifi is on.";                        ((TextView) powerView.findViewById(R.id.waitView)).setText(text);                    }                }            }        };    }    public void buyPower(IabHelper mHelper, Activity activity, PowerEnum powerEnum, String powerSku) {        SkuDetails skuDetails = inventory.getSkuDetails(powerSku);        if (powerService.willHaveMoreThanMaxPowerQuantity(powerEnum, getPowerQuantityFromSkuDetails(skuDetails))) {            alertUser(activity, "Purchase cannot be made because will exceed max quantity allowed for power.");            return;        }        String developerPayload = App.getContext().getString(R.string.purchase_payload);        IabHelper.OnIabPurchaseFinishedListener finishedListener = createPurchaseFinishedListener(mHelper, activity, developerPayload);        mHelper.flagEndAsync();        mHelper.launchPurchaseFlow(activity, powerSku, REQUEST_CODE, finishedListener, developerPayload);    }    public void setWaitScreen(boolean set, View powerView) {        powerView.findViewById(R.id.showView).setVisibility(set ? View.GONE : View.VISIBLE);        powerView.findViewById(R.id.waitView).setVisibility(set ? View.VISIBLE : View.GONE);    }    public IabHelper.OnIabPurchaseFinishedListener createPurchaseFinishedListener(final IabHelper mHelper, final Activity activity, final String randomStringId) {        return new IabHelper.OnIabPurchaseFinishedListener() {            public void onIabPurchaseFinished(IabResult result, Purchase purchase) {                Log.d(activity.getLocalClassName(), "Purchase finished: " + result + ", purchase: " + purchase);                // if we were disposed of in the meantime, quit.                if (mHelper == null) return;                if (result.isFailure()) {                    if (result.getResponse() != -1005) {                        alertUser(activity, "Error purchasing: " + result);                    }                    return;                }                if (!verifyDeveloperPayload(purchase, randomStringId)) {                    alertUser(activity, "Error purchasing. Authenticity verification failed.");                    return;                }                Log.d(activity.getLocalClassName(), "Purchase successful.  Now consuming");                mHelper.flagEndAsync();                mHelper.consumeAsync(purchase, createOnConsumeListener(mHelper, activity));            }        };    }    public IabHelper.OnConsumeFinishedListener createOnConsumeListener(final IabHelper mHelper, final Activity activity) {        return new IabHelper.OnConsumeFinishedListener() {            public void onConsumeFinished(Purchase purchase, IabResult result) {                // if we were disposed of in the meantime, quit.                if (mHelper == null) return;                if (result.isSuccess()) {                    String sku = purchase.getSku();                    SkuDetails purchaseDetails = inventory.getSkuDetails(sku);                    if (purchase.getSku().contains(PowerEnum.CLEAR_ONE_NUM.getAppPurchaseSKU())) {                        powerService.updateStoredPowerQuantity(PowerEnum.CLEAR_ONE_NUM, getPowerQuantityFromSkuDetails(purchaseDetails));                    } else if (purchase.getSku().contains(PowerEnum.CLEAR_ALL_NUM.getAppPurchaseSKU())) {                        powerService.updateStoredPowerQuantity(PowerEnum.CLEAR_ALL_NUM, getPowerQuantityFromSkuDetails(purchaseDetails));                    }                    soundService.playSound(SoundEnum.BOUGHT_POWER);                    Log.d(activity.getLocalClassName(), "Consumption successful and updating done.");                } else {                    alertUser(activity, "Error while consuming: " + result);                }            }        };    }    public Integer getPowerQuantityFromSkuDetails(SkuDetails skuDetails) {        //TODO pull this out as constants?        char value = skuDetails.getSku().charAt(skuDetails.getSku().length() - 1);        if (value == '1') {            return 3;        } else if (value == '2') {            return 6;        } else if (value == '3') {            return 12;        } else {            return null;        }    }    private boolean verifyDeveloperPayload(Purchase purchase, String randomStringId) {        return randomStringId.equals(purchase.getDeveloperPayload());    }    public void alertUser(Activity activity, String message) {        AlertDialog.Builder bld = new AlertDialog.Builder(activity);        bld.setMessage(message);        bld.setNeutralButton("OK", null);        Log.d(activity.getLocalClassName(), "Showing alertUser dialog: " + message);        bld.create().show();    }    private String getAppPublicKey() {        String password1 = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAsZLs8wLD2wqB3uxY";        String password2 = "KmF5QxWoImQg5o7D8tGhZ/Bjc+AYujvlU+UibL+IY87XUdaH5HpENP/UPejyhSNQ34yuewgwia";        String password3 = "Cv6aZKCxIB5eupkWDGjQQSbxyEgmcUsQ6g+TsWhTkiCA1FN6qO7AUgfSJ+2EVAe66YV3QDb/RwUJZD2mEr0KlR0BNCa";        String password4 = "XK2RaPgmPwruBWJd6xW1rvgDcptzRFU+OnOaZ03TACVb4hAcwrCuOx0Va5CdyHa4MsHTuYiqtU286L2KsHDNcsKAR";        String password5 = "/Ml5/o5SY06pg4bbdYJCU9MScxlBacnq8ce1OL3hb+firtvWvvSF3XaPm05hAZb3BR9Edt+wIDAQAB";        return (password1 + password2 + password3 + password4 + password5);    }    public String getRandomStringId() {        return new BigInteger(130, random).toString(32);    }}