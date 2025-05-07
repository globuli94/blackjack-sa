const BASE_URL = 'http://localhost:8080/game';

interface ResponseData {
    message: string;
}

async function startGame(): Promise<void> {
    const response = await fetch(`${BASE_URL}/start`, { method: 'POST' });
    if (response.ok) {
        return;
    } else {
        throw new Error('Failed to start game');
    }
}

async function addPlayer(name: string): Promise<void> {
    const response = await fetch(`${BASE_URL}/addPlayer/${name}`, { method: 'POST' });
    if (response.ok) {
        return;
    } else {
        throw new Error('Failed to add player');
    }
}

async function hitPlayer(): Promise<void> {
    const response = await fetch(`${BASE_URL}/hit`, { method: 'POST' });
    if (response.ok) {
        return;
    } else {
        throw new Error('Failed to hit player');
    }
}

async function standPlayer(): Promise<void> {
    const response = await fetch(`${BASE_URL}/stand`, { method: 'POST' });
    if (response.ok) {
        return;
    } else {
        throw new Error('Failed to stand player');
    }
}

async function doubleDown(): Promise<void> {
    const response = await fetch(`${BASE_URL}/doubleDown`, { method: 'POST' });
    if (response.ok) {
        return;
    } else {
        throw new Error('Failed to double down');
    }
}

async function bet(amount: string): Promise<void> {
    const response = await fetch(`${BASE_URL}/bet/${amount}`, { method: 'POST' });
    if (response.ok) {
        return;
    } else {
        throw new Error('Failed to place bet');
    }
}

async function leavePlayer(): Promise<void> {
    const response = await fetch(`${BASE_URL}/leave`, { method: 'POST' });
    if (response.ok) {
        return;
    } else {
        throw new Error('Failed to leave player');
    }
}

async function getGameState(): Promise<string> {
    const response = await fetch(`${BASE_URL}/state`);
    if (response.ok) {
        return await response.text();
    } else {
        throw new Error('Failed to get game state');
    }
}

// Persistence server routes

// Save a game to the persistence server with a specified gameId
async function saveGameToPersistence(gameId: string): Promise<void> {
    const response = await fetch(`${BASE_URL}/save?gameId=${gameId}`, { method: 'POST' });
    if (!response.ok) throw new Error('Failed to save game');
}

// Load a game from the persistence server using the specified gameId
async function loadGameFromPersistence(gameId: string): Promise<void> {
    const response = await fetch(`${BASE_URL}/load?gameId=${gameId}`, { method: 'POST' });
    if (!response.ok) throw new Error('Failed to load game');
}

export {
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
    saveGameToPersistence,
    loadGameFromPersistence
};
