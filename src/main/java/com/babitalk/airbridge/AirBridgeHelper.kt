package com.babitalk.airbridge

import android.app.Application
import android.content.Intent
import android.net.Uri
import android.util.Log
import co.ab180.airbridge.Airbridge
import co.ab180.airbridge.AirbridgeLogLevel
import co.ab180.airbridge.AirbridgeOptionBuilder
import co.ab180.airbridge.common.AirbridgeAttribute
import co.ab180.airbridge.common.AirbridgeCategory

object AirBridgeHelper {
    fun initializeAirbridge(
        application: Application,
        appName: String,
        sdkToken: String,
        signatureSecretId: String,
        signatureSecret: String
    ) {
        val config = AirbridgeOptionBuilder(appName, sdkToken)
            .setSDKSignature(signatureSecretId, signatureSecret)
            .setLogLevel(AirbridgeLogLevel.DEBUG)
            .build()
        Airbridge.initializeSDK(application, config)
    }

    /**
     * Airbridge.handleDeeplink 함수는 에어브릿지 딥링크가 입력되면 true와 함께 변환된 스킴 딥링크를 onSuccess에 전달합니다.
     * 에어브릿지 딥링크가 아니면 false 와 함께 콜백을 제공하지 않습니다.
     */
    fun isHandleDeepLink(intent: Intent, handle: (Uri) -> Unit) = Airbridge.handleDeeplink(intent) {
        // when app is opened with airbridge deeplink
        // show proper content using url (YOUR_SCHEME://...)
        handle(it)
    }

    /**
     * 앱이 설치되고 처음으로 호출되었으면 true를 반환하고, 에어브릿지 딥링크 획득을 기다려 스킴 딥링크로 변환해 onSuccess로 전달합니다.
     * 해당 스킴 딥링크를 활용해 유저를 설정한 목적지로 보낼 수 있습니다.
     * 또는 저장된 에어브릿지 딥링크가 없으면 null을 onSuccess 전달합니다.
     * SDK가 초기화되지 않았거나 Airbridge.handleDeferredDeeplink 함수를 처음으로 호출하지 않았다면 false를 전달합니다.
     */
    fun isHandleDeferredDeeplink(onSuccess: (Uri?) -> Unit, onFailure: () -> Unit) =
        Airbridge.handleDeferredDeeplink({
            Log.e("isHandleDeferredDeeplink", "uri ----> ${it}")
            onSuccess(it)
        }, {
            onFailure()
        })

    /**
     * 예: kakao, naver, apple, facebook, email"
     */
    fun sendLoginEvent(loginType: String?) {
        Airbridge.trackEvent(
            AirbridgeCategory.SIGN_IN,
            semanticAttributes = mapOf(
                AirbridgeAttribute.ACTION to loginType
            )
        )
    }

    fun leaveEvent() {
        Airbridge.clearUser()
    }

    /**
     * 예: kakao, naver, apple, facebook, email"
     */
    fun sendSignUpEvent(loginType: String) {
        Airbridge.trackEvent(
            AirbridgeCategory.SIGN_UP,
            semanticAttributes = mapOf(
                AirbridgeAttribute.ACTION to loginType
            )
        )
    }

    /**
     * - 성형
     * - 쁘띠/피부
     * - 의사
     * - 병원
     */
    fun sendDetailPageEvent(
        type: String?,
        category: String? = null,
        eventId: String? = null,
        doctorId: String? = null,
        hospitalId: String? = null
    ) {
        Airbridge.trackEvent(
            AirbridgeCategory.PRODUCT_VIEWED,
            semanticAttributes = mapOf(
                AirbridgeAttribute.ACTION to type,
                AirbridgeAttribute.LABEL to category,
            ).filter { it.value != null },
            customAttributes = mapOf(
                "event_id" to eventId,
                "doctor_id" to doctorId,
                "hospital_id" to hospitalId
            ).filter { it.value != null }
        )
    }

    fun sendReviewDetailEvent(type: String?, category: String? = null, id: String?) {
        Airbridge.trackEvent(
            "pageview_review_procedure_detail",
            semanticAttributes = mapOf(
                AirbridgeAttribute.ACTION to type,
                AirbridgeAttribute.LABEL to category,
            ),
            customAttributes = mapOf(
                "post_id" to id
            )
        )
    }

    fun sendAskMemoDetailEvent(type: String?, category: String? = null, id: String?) {
        Airbridge.trackEvent(
            "pageview_review.ask_event_detail",
            semanticAttributes = mapOf(
                AirbridgeAttribute.ACTION to type,
                AirbridgeAttribute.LABEL to category,
            ),
            customAttributes = mapOf(
                "post_id" to id
            )
        )
    }

    fun sendExhibitionDetailEvent(name: String?, id: String?) {
        Airbridge.trackEvent(
            "pageview_crm_exhibition_detail",
            semanticAttributes = mapOf(
                AirbridgeAttribute.ACTION to name,
            ),
            customAttributes = mapOf(
                "crm_exhibition_id" to id
            )
        )
    }

    fun sendEventPurchaseStart(
        type: String?, category: String? = "",
        eventId: String? = null,
        doctorId: String? = null,
        hospitalId: String? = null
    ) {
        Airbridge.trackEvent(
            AirbridgeCategory.INITIATE_CHECKOUT,
            semanticAttributes = mapOf(
                AirbridgeAttribute.ACTION to type,
                AirbridgeAttribute.LABEL to category,
            ).filter { it.value != null },
            customAttributes = mapOf(
                "event_id" to eventId,
                "doctor_id" to doctorId,
                "hospital_id" to hospitalId
            ).filter { it.value != null }
        )
    }

    fun sendEventPurchaseDone(
        type: String?,
        category: String? = null,
        price: Double?,
        id: String?,
        eventPrice: String? = null
    ) {
        Airbridge.trackEvent(
            AirbridgeCategory.ORDER_COMPLETED,
            semanticAttributes = mapOf(
                AirbridgeAttribute.ACTION to type,
                AirbridgeAttribute.LABEL to category,
                AirbridgeAttribute.VALUE to price
            ).filter { it.value != null },
            customAttributes = mapOf(
                "ask_id" to id,
                "discount_price" to eventPrice
            ).filter { it.value != null }
        )
    }

    fun sendSearchResultEvent(keyword: String?) {
        Airbridge.trackEvent(
            "pageview_search_result",
            semanticAttributes = mapOf(
                AirbridgeAttribute.LABEL to keyword,
            )
        )
    }

    fun sendScrapEvent(
        type: String?,
        category: String? = null,
        eventId: String? = null,
        doctorId: String? = null,
        hospitalId: String? = null
    ) {
        val customAttributes = mapOf(
            "event_id" to eventId,
            "doctor_id" to doctorId,
            "hospital_id" to hospitalId
        ).filterValues { it != null }

        Airbridge.trackEvent(
            "scrap_event",
            semanticAttributes = mapOf(
                AirbridgeAttribute.ACTION to type,
                AirbridgeAttribute.LABEL to category,
            ),
            customAttributes = customAttributes
        )
    }

    fun sendTalkDetailEvent(type: String?, category: String? = "", id: String?) {
        Airbridge.trackEvent(
            "pageview_talk_detail",
            semanticAttributes = mapOf(
                AirbridgeAttribute.ACTION to type,
                AirbridgeAttribute.LABEL to category,
            ),
            customAttributes = mapOf(
                "post_id" to id
            )
        )
    }

    fun sendWriteTalkEvent(type: String?, category: String? = "", id: String?) {
        Airbridge.trackEvent(
            "pageview_talk_write",
            semanticAttributes = mapOf(
                AirbridgeAttribute.ACTION to type,
                AirbridgeAttribute.LABEL to category,
            ),
            customAttributes = mapOf(
                "post_id" to id
            )
        )
    }

    fun testFunction() {
        Log.e("airBridge", "SubModule Test Code v11.0")
    }
}