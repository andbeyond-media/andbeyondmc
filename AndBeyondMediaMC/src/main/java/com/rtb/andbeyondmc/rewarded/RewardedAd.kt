package com.rtb.andbeyondmc.rewarded

import android.app.Activity
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.rtb.andbeyondmc.common.AdRequest
import com.rtb.andbeyondmc.common.ServerSideVerificationOptions
import com.rtb.andbeyondmc.rewardedinterstitial.Reward
import com.rtb.andbeyondmc.sdk.FullScreenContentCallback
import com.rtb.andbeyondmc.sdk.Logger
import com.rtb.andbeyondmc.sdk.log

class RewardedAd(private val context: Activity, private val adUnit: String) {
    private var rewardedAdManager = RewardedAdManager(context, adUnit)
    private var mRewardedAd: RewardedAd? = null

    fun load(adRequest: AdRequest, callBack: (loaded: Boolean) -> Unit) {
        rewardedAdManager.load(adRequest) {
            mRewardedAd = it
            callBack(mRewardedAd != null)
        }
    }

    fun setServerSideVerificationOptions(options: ServerSideVerificationOptions) {
        options.getOptions()?.let {
            mRewardedAd?.setServerSideVerificationOptions(it)
        }
    }

    fun show(callBack: (reward: Reward?) -> Unit) {
        if (mRewardedAd != null) {
            mRewardedAd?.show(context) { callBack(Reward(it.amount, it.type)) }
        } else {
            Logger.ERROR.log(msg = "The rewarded interstitial ad wasn't ready yet.")
            callBack(null)
        }
    }

    fun setContentCallback(callback: FullScreenContentCallback) {
        mRewardedAd?.fullScreenContentCallback = object : com.google.android.gms.ads.FullScreenContentCallback() {
            override fun onAdClicked() {
                super.onAdClicked()
                callback.onAdClicked()
            }

            override fun onAdDismissedFullScreenContent() {
                super.onAdDismissedFullScreenContent()
                mRewardedAd = null
                callback.onAdDismissedFullScreenContent()
            }

            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                super.onAdFailedToShowFullScreenContent(p0)
                mRewardedAd = null
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