package com.jackzheng.ourgame.demonadshowlib.googlePay;

import android.app.Activity;
import android.os.Debug;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BillingControl  implements PurchasesUpdatedListener{

    private static final String BASE_64_ENCODED_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAj2NtrxszfQ7GzXH8vIvOMsIKToJ8Mxjgzb3VAM8VzOmDibMsrF8BrVMeRCgL/iyWXDoSn5yk8IsEGoQmwRycjlLIxZM8TS5+g/YkpZFzwyvPxVTXw4BY2wOHX7zi3SkATtvr6DTMcZYvZqWNALj618flq/0JgVRpcWxMAbQFC+/NUHRGFb6iB+/IbX3iCARxp8PB6wXc8d2tO133wfPrj/dunaw8VxBHiNdAEGxQLgwZiGZ5mdiD82m2VizM/4vH9fDq+unI1lB7waFYBbgXLUg0XCIOHSPhwvOXkb4CUP3TBbY951QuSL6MCZfeBmUlTzHgsAgyFBAwlMnrsFNG5wIDAQAB";

    private static final String TAG = "BillingControl";
    Activity mActivity;
    BillingUpdatesListener mBillingUpdatesListener;
    BillingClient mBillingClient;
    boolean mIsServiceConnected = false;
    int mBillingClientResponseCode = -1;
    List<SkuDetails> mSkuDetails = null;
    public final List<Purchase> mPurchases = new ArrayList<>();

    public interface BillingUpdatesListener {
        void onBillingClientSetupFinished();
        void onConsumeFinished(String token,  int result);
        void onPurchasesUpdated(List<Purchase> purchases);
        void onQueryPurchases(List<SkuDetails> skus);
    }

    public BillingControl(Activity activity, final BillingUpdatesListener updatesListener) {
        Log.d(TAG, "Creating Billing client.");
        mActivity = activity;
        mBillingUpdatesListener = updatesListener;
        mBillingClient = BillingClient.newBuilder(mActivity).enablePendingPurchases().setListener( this).build();

        Log.d(TAG, "Starting setup.");


        mBillingClient.startConnection(new BillingClientStateListener() {

            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                Log.d(TAG, "Setup finished. Response code: " + billingResult.getResponseCode());
                if ( billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    mIsServiceConnected = true;
                    queryPurchases();
                    mBillingUpdatesListener.onBillingClientSetupFinished();
                }
                mBillingClientResponseCode = billingResult.getResponseCode();
            }

            @Override
            public void onBillingServiceDisconnected() {
                mIsServiceConnected = false;
                mBillingClient.startConnection(this);
            }
        });


        // Start setup. This is asynchronous and the specified listener will be called
        // once setup completes.
        // It also starts to report all the new purchases through onPurchasesUpdated() callback.

    }
    private void queryPurchases(){
        List<String> skuList = new ArrayList<>();
        skuList.add("hero_support");
        skuList.add("diamond_bag_small");
        skuList.add("diamond_bag_middle");
        skuList.add("diamond_bag_big");
        skuList.add("diamond_bag_super");
        skuList.add("gas");
        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);
        mBillingClient.querySkuDetailsAsync(params.build(),
                new SkuDetailsResponseListener() {
                    @Override
                    public void onSkuDetailsResponse(BillingResult billingResult, List<SkuDetails> skuDetailsList) {
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && skuDetailsList != null) {
                            mSkuDetails = skuDetailsList;
                            for (SkuDetails skuDetails : skuDetailsList) {
                                Log.d(TAG , "skuDetails sku="+skuDetails.getSku()+" skuDetails="+skuDetails.getPrice());
                            }
                            mBillingUpdatesListener.onQueryPurchases(mSkuDetails);
                        }else{
                            Log.d(TAG , "skuDetails billingResult.getResponseCode()="+billingResult.getResponseCode()+" skuDetailsList="+skuDetailsList);

                        }
                    }

                });
    }

    private boolean buySku(SkuDetails sku){

        BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                .setSkuDetails(sku)
                .build();
        BillingResult responseCode = mBillingClient.launchBillingFlow(mActivity,flowParams);
        return responseCode.getResponseCode() == BillingClient.BillingResponseCode.OK;
    }


    @Override
    public void onPurchasesUpdated(BillingResult billingResult, @Nullable List<Purchase> purchases) {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
                && purchases != null) {
         //   mPurchases.clear();
            for (Purchase purchase : purchases) {
                handlePurchase(purchase);
            }
            mBillingUpdatesListener.onPurchasesUpdated(mPurchases);
        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
            // Handle an error caused by a user cancelling the purchase flow.
        } else {
            // Handle any other error codes.
        }
    }
    private void handlePurchase(Purchase purchase) {
        if (!verifyValidSignature(purchase.getOriginalJson(), purchase.getSignature())) {
            Log.i(TAG, "Got a purchase: " + purchase + "; but signature is bad. Skipping...");
            return;
        }

        Log.d(TAG, "Got a verified purchase: " + purchase);
        consumeAsync(purchase.getPurchaseToken());
        mPurchases.add(purchase);
    }
    private boolean verifyValidSignature(String signedData, String signature) {
        // Some sanity checks to see if the developer (that's you!) really followed the
        // instructions to run this sample (don't put these checks on your app!)
        if (BASE_64_ENCODED_PUBLIC_KEY.contains("CONSTRUCT_YOUR")) {
            throw new RuntimeException("Please update your app's public key at: "
                    + "BASE_64_ENCODED_PUBLIC_KEY");
        }

        try {
            return Security.verifyPurchase(BASE_64_ENCODED_PUBLIC_KEY, signedData, signature);
        } catch (IOException e) {
            Log.e(TAG, "Got an exception trying to validate a purchase: " + e);
            return false;
        }
    }
    private Set<String> mTokensToBeConsumed;
    public void consumeAsync(final String purchaseToken) {
        // If we've already scheduled to consume this token - no action is needed (this could happen
        // if you received the token when querying purchases inside onReceive() and later from
        // onActivityResult()
        if (mTokensToBeConsumed == null) {
            mTokensToBeConsumed = new HashSet<>();
        } else if (mTokensToBeConsumed.contains(purchaseToken)) {
            Log.i(TAG, "Token was already scheduled to be consumed - skipping...");
            return;
        }
        mTokensToBeConsumed.add(purchaseToken);

        // Generating Consume Response listener
        final ConsumeResponseListener onConsumeListener = new ConsumeResponseListener() {
            @Override
            public void onConsumeResponse(BillingResult responseCode, String purchaseToken) {
                // If billing service was disconnected, we try to reconnect 1 time
                // (feel free to introduce your retry policy here).
                mBillingUpdatesListener.onConsumeFinished(purchaseToken, responseCode.getResponseCode());
            }
        };
        ConsumeParams consumeParams =
                ConsumeParams.newBuilder()
                        .setPurchaseToken(purchaseToken)
                        .build();
        mBillingClient.consumeAsync(consumeParams, onConsumeListener);

    }

}
