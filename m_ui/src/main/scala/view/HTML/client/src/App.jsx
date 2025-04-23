import { useState } from 'react'
import './App.css'
import {
    startGame,
    addPlayer,
    hitPlayer,
    standPlayer,
    doubleDown,
    bet,
    leavePlayer,
    saveGame,
    loadGame,
    getGameState,
    loadGameFromPersistence,
    saveGameToPersistence
} from './api' // Pfad zu deiner API-Datei

function App() {
    const [gameState, setGameState] = useState('')
    const [playerName, setPlayerName] = useState('')
    const [betAmount, setBetAmount] = useState('')
    const [path, setPath] = useState('')

    const updateState = async (action) => {
        try {
            await action()
            const state = await getGameState()
            setGameState(state)
        } catch (error) {
            console.error(error)
            setGameState('Fehler beim Ausführen der Aktion')
        }
    }

    return (
        <div className="app">
            <h1>Blackjack</h1>
            <div className="controls">
                <button onClick={() => updateState(startGame)}>Start Game</button>

                <input
                    type="text"
                    placeholder="Player name"
                    value={playerName}
                    onChange={(e) => setPlayerName(e.target.value)}
                />
                <button onClick={() => updateState(() => addPlayer(playerName))}>Add Player</button>

                <button onClick={() => updateState(hitPlayer)}>Hit</button>
                <button onClick={() => updateState(standPlayer)}>Stand</button>
                <button onClick={() => updateState(doubleDown)}>Double Down</button>

                <input
                    type="text"
                    placeholder="Bet amount"
                    value={betAmount}
                    onChange={(e) => setBetAmount(e.target.value)}
                />
                <button onClick={() => updateState(() => bet(betAmount))}>Bet</button>

                <button onClick={() => updateState(leavePlayer)}>Leave</button>
                <button onClick={() => updateState(saveGame)}>Save</button>
                <button onClick={() => updateState(loadGame)}>Load</button>

                <input
                    type="text"
                    placeholder="Persistence path"
                    value={path}
                    onChange={(e) => setPath(e.target.value)}
                />
                <button onClick={() => updateState(() => saveGameToPersistence(path))}>
                    Save to Persistence
                </button>
                <button onClick={() => updateState(() => loadGameFromPersistence(path))}>
                    Load from Persistence
                </button>
            </div>

            <pre className="game-state">{gameState}</pre>
        </div>
    )
}

export default App
