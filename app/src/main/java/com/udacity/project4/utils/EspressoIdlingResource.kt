package com.udacity.project4.utils

import android.view.View
import androidx.test.espresso.idling.CountingIdlingResource

object EspressoIdlingResource {

    private const val RESOURCE = "GLOBAL"

    @JvmField
    val countingIdlingResource = CountingIdlingResource(RESOURCE)

    fun increment() {
        countingIdlingResource.increment()
    }

    fun decrement() {
        if (!countingIdlingResource.isIdleNow) {
            countingIdlingResource.decrement()
        }
    }
}

//object EspressoIdlingResourceToast {
//    val idlingResource = CountingIdlingResource("toast")
//
//    val listener: View.OnAttachStateChangeListener =
//        object : View.OnAttachStateChangeListener {
//            override fun onViewAttachedToWindow(v: View) {
//                EspressoIdlingResourceToast.idlingResource.increment()
//            }
//
//            override fun onViewDetachedFromWindow(v: View) {
//                EspressoIdlingResourceToast.idlingResource.decrement()
//            }
//        }
//}

inline fun <T> wrapEspressoIdlingResource(function: () -> T): T {
    // Espresso does not work well with coroutines yet. See
    // https://github.com/Kotlin/kotlinx.coroutines/issues/982
    EspressoIdlingResource.increment() // Set app as busy.
    return try {
        function()
    } finally {
        EspressoIdlingResource.decrement() // Set app as idle.
    }
}


