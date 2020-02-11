import Foundation

@objc (RNDSDualScreenManager)
class DualScreenManager: RCTViewManager {

  override func view() -> UIView! {
    return DualScreen()
  }
}
