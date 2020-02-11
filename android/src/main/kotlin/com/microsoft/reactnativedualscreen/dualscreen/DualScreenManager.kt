package com.microsoft.reactnativedualscreen.dualscreen

import android.content.Context
import android.graphics.Rect
import android.view.View
import android.view.WindowManager
import com.facebook.react.bridge.*
import com.facebook.react.bridge.Arguments.createMap
import com.facebook.react.modules.core.DeviceEventManagerModule.RCTDeviceEventEmitter
import com.microsoft.device.display.DisplayMask


class DualScreenManager constructor(context: ReactApplicationContext) : ReactContextBaseJavaModule(context) {
	private val mDisplayMask: DisplayMask
		get() {
			return DisplayMask.fromResourcesRectApproximation(currentActivity)
		}
	private val rotation: Int
		get() {
			val wm = currentActivity?.getSystemService(Context.WINDOW_SERVICE) as WindowManager?
			return wm?.defaultDisplay?.rotation ?: 0
		}
	private val hinge: Rect
		get() {
			// Hinge's coordinates of its 4 edges in different mode
			// Double Landscape Rect(0, 1350 - 1800, 1434)
			// Double Portrait  Rect(1350, 0 - 1434, 1800)
			return mDisplayMask.getBoundingRectsForRotation(rotation)[0]
		}

    private val HINGE_WIDTH_KEY = "HINGE_WIDTH"

	override fun getName() = "DualScreenManager"

	override fun initialize() {
		super.initialize()
		createSubscription()
	}

	override fun getConstants(): Map<String, Any>? {
        val constants: MutableMap<String, Any> = HashMap()
    	constants[HINGE_WIDTH_KEY] = 34
    	return constants
    }

	/**
	 * Resolving a promise detecting if device is in Dual modes
	 */
	@ReactMethod
	fun isDualMode(promise: Promise) {
		val windowRect: Rect? = getWindowRect()

		// The windowRect doesn't intersect hinge
		windowRect?.let {
			if (it.width() > 0 && it.height() > 0) {
				promise.resolve(hinge.intersect(it))
			} else {
				promise.resolve(false)
			}
		}

		promise.reject(RuntimeException())
	}

	/**
	 * Resolving a promise detecting if device is Dual screen
	 */
	@ReactMethod
	fun isDualScreenDevice(promise: Promise) {
		val feature = "com.microsoft.device.display.displaymask"
		promise.resolve(reactApplicationContext.packageManager.hasSystemFeature(feature))
	}

	fun getScreenRects(screenRect1: Rect, screenRect2: Rect, rotation: Int) {
		val windowRect = getWindowRect()
		getScreenRects(windowRect, hinge, screenRect1, screenRect2)
	}

	/**
	 * Create event emitter of dual mode change
	 */
	private fun createSubscription() {
		val rootView: View? = currentActivity?.window?.decorView?.rootView
		rootView?.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
			val params = createMap()
			val windowRect: Rect? = getWindowRect()
			var isDual = false

			// The windowRect doesn't intersect hinge
			windowRect?.let {
				if (it.width() > 0 && it.height() > 0) {
					isDual = hinge.intersect(it)
				}
			}

			params.putString("isDualMode", isDual.toString())
			sendEvent(reactApplicationContext, "DualModeChanged", params)
		}
	}

	private fun sendEvent(reactContext: ReactContext,
	                      eventName: String,
	                      params: WritableMap?) {
		reactContext
				.getJSModule(RCTDeviceEventEmitter::class.java)
				.emit(eventName, params)
	}

	private fun getWindowRect(): Rect {
		val windowRect = Rect()
		val rootView: View? = currentActivity?.window?.decorView?.rootView
		rootView?.getDrawingRect(windowRect)
		return windowRect
	}

	private fun getScreenRects(windowRect: Rect, hinge: Rect, screenRect1: Rect, screenRect2: Rect) {
		// Hinge's coordinates of its 4 edges in different mode
		// Double Landscape Rect(0, 1350 - 1800, 1434)
		// Double Portrait  Rect(1350, 0 - 1434, 1800)
		with(screenRect1) {
			left = 0
			top = 0
			right = if (hinge.left > 0) hinge.left else windowRect.right
			bottom = if (hinge.left > 0) windowRect.bottom else hinge.top
		}

		with(screenRect2) {
			left = if (hinge.left > 0) hinge.right else 0
			top = if (hinge.left > 0) 0 else hinge.bottom
			right = windowRect.right
			bottom = windowRect.bottom
		}
	}
}
