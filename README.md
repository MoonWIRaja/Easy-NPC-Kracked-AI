# Easy NPC Kracked AI (Developer Edition)

> 🧠 **Open Source Minecraft Fabric Mod** - Autonomous AI NPCs with Local Persistence

**Language / Bahasa:**
- 🇬🇧 **English** (You are here)
- 🇲🇾 [Bahasa Melayu](README.ms.md)

```
╔══════════════════════════════════════════════════════════════════════════════╗
║  ░▒▓ EASY NPC KRACKED AI ▓▒░                                                ║
║                                                                              ║
║    Turn Your NPCs Into Thinking, Learning Entities!                          ║
║    DEVELOPMENT & SOURCE CODE REPOSITORY                                      ║
╚══════════════════════════════════════════════════════════════════════════════╝
```

---

## 🛠️ Project Overview

This is the development repository for **Easy NPC Kracked AI**, a Minecraft Fabric mod that integrates LLMs (Large Language Models) into NPCs. Unlike standard mods, this project includes a built-in **Javalin Web Server** for a real-time management dashboard and uses **SQLite** for persistent NPC memory storage.

### Technology Stack
- **Mod Loader:** Fabric API
- **Language:** Java 21
- **Build System:** Gradle 8.x
- **Web Server:** Javalin (Embedded)
- **Database:** SQLite (Embedded)
- **Frontend:** Vanilla JS + CSS (in `src/main/resources/web`)

---

## 📋 Table of Contents

1. [Prerequisites](#-prerequisites)
2. [Setting Up Environment](#-setting-up-environment)
3. [Building from Source](#-building-from-source)
4. [Running & Debugging](#-running--debugging)
5. [Testing](#-testing)
6. [Project Structure](#-project-structure)

---

## 📦 Prerequisites

To contribute or build this project, you need:

| Requirement | Details |
|-------------|---------|
| **Java JDK** | Version 21 (Required for Minecraft 1.21+) |
| **Git** | For version control |
| **IDE** | IntelliJ IDEA (Recommended) or VS Code |

---

## 🚀 Setting Up Environment

### 1. Clone the Repository

```bash
git clone https://github.com/MoonWIRaja/Easy-NPC-Kracked-AI.git
cd Easy-NPC-Kracked-AI
```

### 2. Generate Fabric Sources

Before opening in your IDE, generate the Minecraft source code:

```bash
# macOS / Linux
./gradlew genSources

# Windows
gradlew genSources
```

### 3. Import Project
- **IntelliJ IDEA:** File > Open > Select `build.gradle` > Open as Project.
- **VS Code:** Open folder, install "Extension Pack for Java".

---

## 🔨 Building from Source

To compile the mod into a `.jar` file:

```bash
./gradlew build
```

**Output Location:**
The final jar will be in `easy-npc-kracked-ai/build/libs/`.

---

## 🎮 Running & Debugging

You don't need to manually install the mod to test it. Gradle handles everything.

### Run Client
Launches Minecraft with the mod installed.

```bash
./gradlew runClient
```

### Run Server
Launches a dedicated server with the mod.
*Note: You may need to accept the EULA in `run/eula.txt` after the first run.*

```bash
./gradlew runServer
```

### Accessing Web Dashboard during Dev
When the game/server is running:
1. Default Port: **8080**
2. URL: http://localhost:8080
3. Default Login: First user becomes Admin.

---

## 🧪 Testing

### Unit & Integration Tests

We use JUnit for testing backend logic (Web server, JSON parsing, etc).

```bash
./gradlew test
```

**Key Test Files:**
- `src/test/java/testserver/TestWebServer.java` - Tests the Javalin web server endpoints without launching Minecraft.

---

## 📁 Project Structure

```
easy-npc-kracked-ai/
├── src/main/java/com/ainpcconnector/
│   ├── AINpcConnectorMod.java       # Mod Entry Point
│   ├── ai/                          # AI Provider Logic (OpenAI, Anthropic)
│   ├── behavior/                    # NPC Mental State & Ticking Logic
│   ├── config/                      # SQLite & Config Managers
│   ├── npc/                         # NPC Registry & Data Models
│   └── web/                         # Embedded Web Server
│       ├── WebServer.java           # Javalin Setup
│       └── handlers/                # HTTP Route Handlers
├── src/main/resources/
│   ├── assets/                      # Textures & Lang files
│   └── web/                         # Web Dashboard Frontend
│       ├── index.html
│       ├── css/
│       └── js/
└── build.gradle                     # Dependencies & Build Config
```

---

## 🤝 Contributing

1. Fork the repository.
2. Create a feature branch (`git checkout -b feature/amazing-feature`).
3. Commit your changes.
4. Push to the branch.
5. Open a Pull Request.

---

## ❤️ Credits

**Maintained & Developed by:**

**(MoonWiRaja & 4kmal4lif) KRACKEDDEV**

With special thanks to:
- **Paulevs** (Original Easy NPC)
- **Henkelmax** (Simple Voice Chat)

---
