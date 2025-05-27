import { useState, useEffect } from 'react'
import './App.css'

// Base API URL
const BASE_URL = 'http://localhost:8080/game'

// Utility to complete fetch calls
async function handleResponse(res) {
    if (!res.ok) throw new Error(await res.text() || 'API error')
    return;
}

// API functions now include sessionId segment
async function startGameApi(sessionId) {
    const res = await fetch(`${BASE_URL}/${sessionId}/start`, { method: 'POST' })
    return handleResponse(res)
}

async function addPlayerApi(sessionId, name) {
    const res = await fetch(`${BASE_URL}/${sessionId}/addPlayer/${encodeURIComponent(name)}`, { method: 'POST' })
    return handleResponse(res)
}

async function hitPlayerApi(sessionId) {
    const res = await fetch(`${BASE_URL}/${sessionId}/hit`, { method: 'POST' })
    return handleResponse(res)
}

async function standPlayerApi(sessionId) {
    const res = await fetch(`${BASE_URL}/${sessionId}/stand`, { method: 'POST' })
    return handleResponse(res)
}

async function doubleDownApi(sessionId) {
    const res = await fetch(`${BASE_URL}/${sessionId}/doubleDown`, { method: 'POST' })
    return handleResponse(res)
}

async function betApi(sessionId, amount) {
    const res = await fetch(`${BASE_URL}/${sessionId}/bet/${encodeURIComponent(amount)}`, { method: 'POST' })
    return handleResponse(res)
}

async function leavePlayerApi(sessionId) {
    const res = await fetch(`${BASE_URL}/${sessionId}/leave`, { method: 'POST' })
    return handleResponse(res)
}

async function getGameStateApi(sessionId) {
    const res = await fetch(`${BASE_URL}/${sessionId}/state`)
    if (!res.ok) throw new Error('Failed to get game state')
    return res.text()
}

async function saveGameApi(sessionId) {
    const res = await fetch(`${BASE_URL}/${sessionId}/save`, { method: 'POST' })
    return handleResponse(res)
}

async function loadGameApi(sessionId) {
    const res = await fetch(`${BASE_URL}/${sessionId}/load`, { method: 'POST' })
    return handleResponse(res)
}

function App() {
    const [gameState, setGameState] = useState('')
    const [playerName, setPlayerName] = useState('')
    const [betAmount, setBetAmount] = useState('')
    const [sessionId, setSessionId] = useState(null)

    // Extract sessionId or redirect
    useEffect(() => {
        const params = new URLSearchParams(window.location.search)
        const id = params.get('sessionId')
        if (!id) {
            window.location.href = '/' // trigger server redirect
        } else {
            setSessionId(id)
        }
    }, [])

    // Fetch game state when sessionId is set
    useEffect(() => {
        if (!sessionId) return
            ;(async () => {
            try {
                const state = await getGameStateApi(sessionId)
                setGameState(state)
            } catch (e) {
                console.error(e)
                setGameState('Fehler beim Laden des Spielzustands')
            }
        })()
    }, [sessionId])

    // Generic update helper
    const updateState = async (action) => {
        if (!sessionId) return
        try {
            await action(sessionId)
            const state = await getGameStateApi(sessionId)
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
                <button onClick={() => updateState(startGameApi)}>Start Game</button>
                <input
                    type="text"
                    placeholder="Player name"
                    value={playerName}
                    onChange={(e) => setPlayerName(e.target.value)}
                />
                <button onClick={() => updateState(id => addPlayerApi(id, playerName))}>
                    Add Player
                </button>
                <button onClick={() => updateState(hitPlayerApi)}>Hit</button>
                <button onClick={() => updateState(standPlayerApi)}>Stand</button>
                <button onClick={() => updateState(doubleDownApi)}>Double Down</button>
                <input
                    type="text"
                    placeholder="Bet amount"
                    value={betAmount}
                    onChange={(e) => setBetAmount(e.target.value)}
                />
                <button onClick={() => updateState(id => betApi(id, betAmount))}>
                    Bet
                </button>
                <button onClick={() => updateState(leavePlayerApi)}>Leave</button>
                <button onClick={() => updateState(saveGameApi)}>Save Game</button>
                <button onClick={() => updateState(loadGameApi)}>Load Game</button>
            </div>
            <pre className="game-state">{gameState}</pre>
        </div>
    )
}

export default App
