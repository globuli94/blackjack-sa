# ♠️♥️ Blackjack (Scala) ♣️♦️

A **text-based (TUI)** and **graphical (GUI)** Blackjack game implemented in Scala using **MVC architecture**, with Docker support for seamless deployment.

---

## 🚀 Features
- **MVC Architecture** – Clean separation of Model, View, and Controller.
- **Dual Interfaces** – Play via terminal (`TUI`) or GUI (`JavaFX/Swing`).
- **Game Persistence** – Save/load game states using file I/O.
- **Dockerized** – Run consistently across environments.
- **Scala Best Practices** – Immutability, pattern matching, and FP.

## Setup

---

Before building the docker images you need to build the client

1. Switch to **s_gameService/m_client** and execute `npm install` and after that `npm run build`
2. Now you can build the docker images with `docker-compose build`
3. After booting with `docker-compose up` you can access the client at http://localhost:8080/

