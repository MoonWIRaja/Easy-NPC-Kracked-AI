# Easy NPC Kracked AI (Developer Edition)

> 🧠 **Minecraft Fabric Mod** - Autonomous AI NPCs with Local Persistence + Web Dashboard

**Language / Bahasa:**
- 🇬🇧 **English** (You are here)
- 🇲🇾 [Bahasa Melayu](README.ms.md)

```
╔══════════════════════════════════════════════════════════════╗
║  ░▒▓ EASY NPC KRACKED AI ▓▒░                                 ║
║                                                              ║
║    ███████╗ █████╗ ███████╗██╗   ██╗    ███╗   ██╗██████╗    ║
║    ██╔════╝██╔══██╗██╔════╝╚██╗ ██╔╝    ████╗  ██║██╔══██╗   ║
║    █████╗  ███████║███████╗ ╚████╔╝     ██╔██╗ ██║██████╔╝   ║
║    ██╔══╝  ██╔══██║╚════██║  ╚██╔╝      ██║╚██╗██║██╔═══╝    ║
║    ███████╗██║  ██║███████║   ██║       ██║ ╚████║██║        ║
║    ╚══════╝╚═╝  ╚═╝╚══════╝   ╚═╝       ╚═╝  ╚═══╝╚═╝        ║
║                                                              ║
║      Turn Your NPCs Into Thinking, Learning Entities!        ║
╚══════════════════════════════════════════════════════════════╝
```

---

## ✨ Features

### 🎛️ Web Dashboard
- **Real-time NPC Management** - Monitor NPC status from your browser
- **Live Console** - View AI thought processes and dialogue logs
- **Configuration** - Adjust AI settings without restarting the game
- **API Sandbox** - Test AI responses directly via the web interface

### 🤖 AI Integration
- **Universal Provider Support** - Works with any OpenAI-compatible API
- **Supported Providers**:
  - OpenAI (GPT-4, GPT-3.5)
  - Anthropic (Claude)
  - Local LLMs (Ollama/LM Studio via HTTP)
- **Personality Engine** - NPCs evolve their personalities over time
- **Memory System** - SQLite-backed persistent memory for long-term interactions

### 🗣️ Voice & Interaction
- **Text-to-Speech (TTS)** - Browser-based or external TTS support
- **Voice Chat API** - Integration with Simple Voice Chat mod
- **Dynamic Dialogue** - Context-aware conversations based on game events

---

## 📋 Table of Contents

1. [Prerequisites](#-prerequisites)
2. [Quick Start (Web Only)](#-quick-start-web-interface-only)
3. [Installation (Full Mod)](#-installation-mod-development)
4. [Configuration](#-configuration)
5. [Running & Debugging](#-running--debugging)
6. [Project Structure](#-project-structure)
7. [Troubleshooting](#-troubleshooting)

---

## 📦 Prerequisites

### Required Software

| Software | Minimum Version | Recommended | Download |
|----------|-----------------|-------------|----------|
| **Java JDK** | v21 | v21 (LTS) | [adoptium.net](https://adoptium.net/temurin/releases/?version=21) |
| **Git** | Any | Latest | [git-scm.com](https://git-scm.com/) |
| **Minecraft** | 1.21.x | 1.21.1 | [minecraft.net](https://www.minecraft.net/) |

### System Requirements

- **RAM**: Minimum 4GB (allocated to Minecraft), Recommended 8GB+
- **OS**: Windows 10/11, macOS, Linux

---

## 🎯 Quick Start: Web Interface Only

**"Saya nak buka web dia sahaja"** (I just want to open the web interface)

If you just want to test the Dashboard/Web Interface without running the full Minecraft game:

### 1. Verify Java Installation
Make sure you have Java 21 installed:
```powershell
java -version
```

### 2. Run Test Server
**Windows:**
```powershell
.\gradlew.bat runTestServer
```

**Mac/Linux:**
```bash
./gradlew runTestServer
```

### 3. Open Dashboard
Open your browser and navigate to:
[http://localhost:8081](http://localhost:8081)

*(Press `Ctrl+C` in the terminal to stop)*

---

##  Installation (Mod Development)

This section is for developers who want to run the mod inside Minecraft.

### Part 1: Download & Setup

**1.1 Clone Repository**
```bash
git clone https://github.com/MoonWIRaja/Easy-NPC-Kracked-AI.git
cd Easy-NPC-Kracked-AI
```

**1.2 Generate Sources**
Before opening in your IDE, generate the Minecraft source code:

**Windows:**
```powershell
.\gradlew.bat genSources
```

**Mac/Linux:**
```bash
./gradlew genSources
```

### Part 2: Run Client

To launch Minecraft with the mod installed:

**Windows:**
```powershell
.\gradlew.bat runClient
```

**Mac/Linux:**
```bash
./gradlew runClient
```

The game will launch. Create a new world to start testing NPCs.

---

## ⚙️ Configuration

The mod uses a `ModConfig` system that can be configured via the Web Dashboard or configuration files.

### Web Server Settings
- **Port:** Default `8080` (Configurable in `ModConfig`)
- **Address:** `0.0.0.0` or `localhost`

### AI Provider Config
Existing `ModConfig.java` structure allows adding multiple providers:
- **ID:** Unique identifier
- **Name:** Display name (e.g., "OpenAI GPT-4")
- **Endpoint:** API URL (e.g., `https://api.openai.com/v1`)
- **API Key:** Your secret key
- **Model:** Model name (e.g., `gpt-4-turbo`)

---

## 📁 Project Structure

```
easy-npc-kracked-ai/
├── src/main/java/com/ainpcconnector/
│   ├── AINpcConnectorMod.java       # Mod Entry Point
│   ├── ai/                          # AI Provider Logic
│   ├── behavior/                    # NPC Mental State & Ticking
│   ├── config/                      # Mod Configuration (ModConfig.java)
│   ├── npc/                         # NPC Registry & Data
│   └── web/                         # Javalin Web Server
│       ├── WebServer.java           # Server Setup
│       └── handlers/                # API Endpoints
├── src/main/resources/
│   ├── assets/                      # Textures & Lang files
│   └── web/                         # Web Dashboard Frontend
│       ├── index.html
│       ├── css/
│       └── js/
├── src/test/java/testserver/        # Standalone Test Server
└── build.gradle                     # Dependencies & Build Config
```

---

## 🆘 Troubleshooting

### `gradlew` command not found
If you see `'gradlew' is not recognized`:
1. Ensure you are in the root folder.
2. Use `.\gradlew.bat` on Windows (with the leading `.\`).
3. If `gradlew.bat` is missing, you can restore it (ask me if needed).

### Port 8080 already in use
If the web server fails to start:
1. Check if another application is using port 8080.
2. Edit the port in the config (or `TestWebServer.java` for testing).

### Java Version Error
The project requires **Java 21**. If you get version errors:
1. Install JDK 21.
2. Set `JAVA_HOME` to your JDK 21 installation.

---

## ❤️ Credits

**Maintained & Developed by:**
**(MoonWiRaja & 4kmal4lif) KRACKEDDEV**

With special thanks to:
- **Paulevs** (Original Easy NPC)
- **Henkelmax** (Simple Voice Chat)

---

Made with 💜 by MoonWiRaja
