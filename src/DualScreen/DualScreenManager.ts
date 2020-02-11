import { NativeModules, NativeEventEmitter, EmitterSubscription } from 'react-native'

declare module 'react-native' {
    namespace NativeModules {
        export interface DualScreenManager {
            isDualMode(): Promise<boolean>
            isDualScreenDevice(): Promise<boolean>
            HINGE_WIDTH: number
        }
    }
}

class RNDualScreenManager {
    private eventEmitter: NativeEventEmitter

    constructor() {
        this.eventEmitter = new NativeEventEmitter(NativeModules.DualScreenManager)
    }

    isDualMode() {
        return NativeModules.DualScreenManager.isDualMode()
    }

    isDualScreenDevice() {
        return NativeModules.DualScreenManager.isDualScreenDevice()
    }

    get hingeWidth() {
        return NativeModules.DualScreenManager.HINGE_WIDTH
    }

    addOnChangeListener(listener: (...args: any[]) => any, context?: any): EmitterSubscription {
        return this.eventEmitter.addListener('DualModeChanged', listener)
    }
}

export const DualScreenManager = new RNDualScreenManager()
