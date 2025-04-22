const BASE_URL = 'http://localhost:8080/game';
const PERSISTENCE_URL = 'http://localhost:8081/persistence';

interface ResponseData {
    message: string;
}

async function startGame(): Promise<ResponseData> {
    const response = await fetch(`${BASE_URL}/start`, { method: 'POST' });
    return await response.json();
}

async function addPlayer(name: string): Promise<ResponseData> {
    const response = await fetch(`${BASE_URL}/addPlayer/${name}`, { method: 'POST' });
    return await response.json();
}

async function hitPlayer(): Promise<ResponseData> {
    const response = await fetch(`${BASE_URL}/hit`, { method: 'POST' });
    return await response.json();
}

async function standPlayer(): Promise<ResponseData> {
    const response = await fetch(`${BASE_URL}/stand`, { method: 'POST' });
    return await response.json();
}

async function doubleDown(): Promise<ResponseData> {
    const response = await fetch(`${BASE_URL}/doubleDown`, { method: 'POST' });
    return await response.json();
}

async function bet(amount: string): Promise<ResponseData> {
    const response = await fetch(`${BASE_URL}/bet/${amount}`, { method: 'POST' });
    return await response.json();
}

async function leavePlayer(): Promise<ResponseData> {
    const response = await fetch(`${BASE_URL}/leave`, { method: 'POST' });
    return await response.json();
}

async function saveGame(): Promise<ResponseData> {
    const response = await fetch(`${BASE_URL}/save`, { method: 'POST' });
    return await response.json();
}

async function loadGame(): Promise<ResponseData> {
    const response = await fetch(`${BASE_URL}/load`, { method: 'POST' });
    return await response.json();
}

async function getGameState(): Promise<string> {
    const response = await fetch(`${BASE_URL}/state`);
    return await response.text();
}

// Persistence server routes
async function loadGameFromPersistence(path: string): Promise<void> {
    const response = await fetch(`${PERSISTENCE_URL}/load/${path}`, { method: 'POST' });
    if (!response.ok) throw new Error('Failed to load game');
}

async function saveGameToPersistence(path: string): Promise<void> {
    const response = await fetch(`${PERSISTENCE_URL}/save/${path}`, { method: 'POST' });
    if (!response.ok) throw new Error('Failed to save game');
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
    loadGameFromPersistence,
    saveGameToPersistence
};
