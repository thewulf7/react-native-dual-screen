package thewulf7.reactnativedualscreen

import com.facebook.react.ReactPackage
import com.facebook.react.bridge.NativeModule
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.uimanager.ViewManager

import thewulf7.reactnativedualscreen.dualscreen.DualScreenManager
import thewulf7.reactnativedualscreen.dualscreen.HingeManager

class ReactNativeDualScreenPackage : ReactPackage {
	override fun createNativeModules(reactContext: ReactApplicationContext): List<NativeModule> {
		return listOf(
				DualScreenManager(reactContext)
		)
	}

	override fun createViewManagers(reactContext: ReactApplicationContext): List<ViewManager<*, *>> {
		return listOf(
				HingeManager()
		)
	}
}
