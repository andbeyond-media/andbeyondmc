package com.rtb.andbeyondmc.rewardedinterstitial

import android.app.Activity
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd
import com.rtb.andbeyondmc.common.AdRequest
import com.rtb.andbeyondmc.common.ServerSideVerificationOptions
import com.rtb.andbeyondmc.sdk.FullScreenContentCallback
import com.rtb.andbeyondmc.sdk.Logger
import com.rtb.andbeyondmc.sdk.log

class RewardedInterstitialAd(private val context: Activity, private val adUnit: String) {
    private var rewardedInterstitialAdManager = RewardedInterstitialAdManager(context, adUnit)
    private var mInterstitialRewardedAd: RewardedInterstitialAd? = null

    fun load(adRequest: AdRequest, callBack: (loaded: Boolean) -> Unit) {
        rewardedInterstitialAdManager.load(adRequest) {
            mInterstitialRewardedAd = it
            callBack(mInterstitialRewardedAd != null)
        }
    }

    fun setServerSideVerificationOptions(options: ServerSideVerificationOptions) {
        options.getOptions()?.let {
            mInterstitialRewardedAd?.setServerSideVerificationOptions(it)
        }
    }

    fun show(callBack: (reward: Reward?) -> Unit) {
        if (mInterstitialRewardedAd != null) {
            mInterstitialRewardedAd?.show(context) { callBack(Reward(it.amount, it.type)) }
        } else {
            Logger.ERROR.log(msg = "The rewarded interstitial ad wasn't ready yet.")
            callBack(null)
        }
    }

    fun setContentCallback(callback: FullScreenContentCallback) {
        mInterstitialRewardedAd?.fullScreenContentCallback = object : com.google.android.gms.ads.FullScreenContentCallback() {
            override fun onAdClicked() {
                super.onAdClicked()
                callback.onAdClicked()
            }

            override fun onAdDismissedFullScreenContent() {
                super.onAdDismissedFullScreenContent()
                mInterstitialRewardedAd = null
                callback.onAdDismissedFullScreenContent()
            }

            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                super.onAdFailedToShowFullScreenContent(p0)
                mInterstitialRewardedAd = null
                callback.onAdFailedToShowFullScreenContent(p0.toString())
            }

            override fun onAdImpression() {
                super.onAdImpression()
                callback.onAdImpression()
            }

            override fun onAdShowedFullScreenContent() {
                super.onAdShowedFullScreenContent()
                callback.onAdShowedFullScreenContent()
            }
        }
    }
}