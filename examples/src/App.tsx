import React, { useEffect, useState } from 'react'
import { RNDualScreenManager } from 'react-native-dual-screen'
import MasterDetail from './MasterDetail'
import TwoPage from './TwoPage'
import CompanionPane from './CompanionPane'
import DualScreenContext from './DualScreenContext'

export default function App() {
    const [isDualMode, setDualMode] = useState(false)
    useEffect(() => {
        RNDualScreenManager.isDualMode().then((isDual: boolean) => {
            setDualMode(isDual)
        })

        RNDualScreenManager.addOnChangeListener((event) => {
            const isDual = event.isDualMode === 'true'
            if (isDualMode !== isDual) {
                setDualMode(isDual)
            }
        })
    })

    return (
        <DualScreenContext.Provider
            value={{ isDualMode }}
        >
            <MasterDetail />
        </DualScreenContext.Provider>
    )
}
