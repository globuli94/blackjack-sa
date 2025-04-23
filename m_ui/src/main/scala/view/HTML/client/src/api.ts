const BASE_URL = 'http://localhost:8080/game';
const PERSISTENCE_URL = 'http://localhost:8081/persistence';

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

async function saveGame(): Promise<void> {
    const response = await fetch(`${BASE_URL}/save`, { method: 'POST' });
    if (response.ok) {
        return;
    } else {
        throw new Error('Failed to save game');
    }
}

async function loadGame(): Promise<void> {
    const response = await fetch(`${BASE_URL}/load`, { method: 'POST' });
    if (response.ok) {
        return;
    } else {
        throw new Error('Failed to load game');
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
