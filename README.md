# ♠️♥️ Blackjack (Scala) ♣️♦️

A **text-based (TUI)** and **graphical (GUI)** Blackjack game implemented in Scala using **MVC architecture**, with Docker support for seamless deployment.

---

## 🚀 Features
- **MVC Architecture** – Clean separation of Model, View, and Controller.
- **Dual Interfaces** – Play via terminal (`TUI`) or GUI (`JavaFX/Swing`).
- **Game Persistence** – Save/load game states using file I/O.
- **Dockerized** – Run consistently across environments.
- **Scala Best Practices** – Immutability, pattern matching, and FP.


## added game library
# this library handles model and model serialization
ThisBuild / version := "1.0.0"
ThisBuild / scalaVersion := "3.3.1"
ThisBuild / organization := "com.github.globuli94"
publishTo := Some("GitHub Packages" at s"https://maven.pkg.github.com/globuli94/blackjack-sa")
lazy val root = (project in file("."))
    .settings(
        name := "gameLibrary",
        libraryDependencies ++= Seq(
            ...
        )
    )

# to create token for package download
create ~/.sbt/1.0/credentials.sbt
credentials += Credentials(
    "GitHub Package Registry",
    "maven.pkg.github.com",
    "USERNAME",
    "TOKEN" // create -> settings -> developer settings -> personal access tokens (classic) -> "SBT Publishing Token" write: and read: packages
)

# to import the package in a project
resolvers += "GitHub Packages" at "https://maven.pkg.github.com/globuli94/blackjack-sa",
libraryDependencies ++= Seq(
      "com.github.globuli94" %% "gamelibrary" % "1.0.0"
)