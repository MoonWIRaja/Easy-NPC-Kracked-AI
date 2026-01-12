# Easy NPC Kracked AI

> 🧠 **Minecraft Fabric Mod** - AI-Powered Autonomous NPCs with SQLite Persistence

**Language / Bahasa:**
- 🇬🇧 **English** (You are here)
- 🇲🇾 [Bahasa Melayu](README.ms.md)

```
╔══════════════════════════════════════════════════════════════════════════════╗
║  ░▒▓ EASY NPC KRACKED AI ▓▒░                                                ║
║                                                                              ║
║    ██████╗ ███████╗ ██████╗ ███████╗ ██████╗██████╗  ██████╗                  ║
║    ██╔══██╗██╔════╝██╔═══██╗██╔════╝██╔════╝██╔══██╗██╔══██╗                ║
║    ██████╔╝█████╗  ██║   ██║███████╗█████╗  ██║  ██║██████╔╝                ║
║    ██╔══██╗██╔══╝  ██║   ██║╚════██║██╔══╝  ██║  ██║██╔══██╗                ║
║    ██████╔╝███████╗╚██████╔╝███████║██║     ██████╔╝██║  ██║                ║
║    ╚═════╝ ╚══════╝ ╚═════╝ ╚══════╝╚═╝     ╚═════╝ ╚═╝  ╚═╝                ║
║                                                                              ║
║         Turn Your NPCs Into Thinking, Learning Entities!                    ║
╚══════════════════════════════════════════════════════════════════════════════╝
```

---

## ✨ Features

### 🤖 Universal AI Provider Support
- **Works with ANY AI** - OpenAI, Anthropic, DeepSeek, Grok, Ollama, or any OpenAI-compatible API
- **Add Unlimited Providers** - Simply enter: Name, API Key, Endpoint, Model
- **Per-NPC Assignment** - Each NPC can use a different AI provider
- **Quick Presets** - One-click setup for popular AI services

### 🧠 Intelligent NPC Behavior
- **Auto-Thinking** - NPCs think and respond autonomously on intervals
- **Memory System** - NPCs remember past conversations with SQLite persistence
- **Personality System** - Customize friendliness, curiosity, humor, aggression
- **Dynamic Learning** - Personalities evolve based on interactions

### 🌐 Web Dashboard
- **Real-time Management** - Configure NPCs from your browser
- **Visual Interface** - Beautiful isometric-themed UI
- **Live Status** - See all active NPCs and their states
- **Secure Authentication** - Password-protected with role-based access

### 🗄️ SQLite Database
- **Persistent Storage** - All data survives server restarts
- **Unlimited NPCs** - Scale to hundreds of AI NPCs
- **Conversation History** - Full chat history stored per NPC
- **User Management** - Multiple admin users with secure login

### 🎙️ Voice Integration
- **Text-to-Speech** - NPCs can speak their responses (requires Simple Voice Chat)
- **Browser TTS** - Use built-in browser speech synthesis
- **External TTS** - Connect to external TTS services

---

## 📋 Table of Contents

1. [Prerequisites](#-prerequisites)
2. [Installation](#-installation)
3. [First Setup](#-first-setup)
4. [Configuration](#-configuration)
5. [Adding AI Providers](#-adding-ai-providers)
6. [Creating & Configuring NPCs](#--creating--configuring-npcs)
7. [Web Dashboard](#-web-dashboard)
8. [Database Management](#-database-management)
9. [Troubleshooting](#-troubleshooting)
10. [Project Structure](#-project-structure)

---

## 📦 Prerequisites

### Required Software

| Software | Version | Download |
|----------|---------|----------|
| **Minecraft** | 1.21.11 | [minecraft.net](https://www.minecraft.net/) |
| **Fabric Loader** | Latest | [fabricmc.net](https://fabricmc.net/use/) |
| **Java** | 21 | [oracle.com](https://www.oracle.com/java/technologies/downloads/) |
| **Easy NPC Mod** | Latest | [modrinth.com/mod/easy+npcs](https://modrinth.com/mod/easy+npcs) |
| **Simple Voice Chat** (Optional) | Latest | [modrinth.com/mod/voice-chat](https://modrinth.com/mod/voice-chat) |

### Server Requirements

- **RAM**: Minimum 4GB, Recommended 8GB+ (for many AI NPCs)
- **CPU**: Modern multi-core processor
- **Network**: Stable internet connection (for AI API calls)

---

## 🚀 Installation

### Part 1: Install Minecraft Fabric Server

**1.1 Download Fabric Installer**

1. Go to [fabricmc.net/use](https://fabricmc.net/use/)
2. Download **Fabric Installer**
3. Run the installer

**1.2 Create Server**

1. Select **"Server"** tab
2. Choose installation location (e.g., `C:\MinecraftServer`)
3. Click **"Download Server"**
4. Click **"Install Server"**

**1.3 Install Easy NPC Mod**

1. Download **Easy NPC** from [Modrinth](https://modrinth.com/mod/easy+npcs)
2. Put the `.jar` file in: `server_folder/mods/`

**1.4 Install Easy NPC Kracked AI**

1. Download **Easy NPC Kracked AI** from [Modrinth](https://modrinth.com/mod/easy-npc-kracked-ai)
2. Put the `.jar` file in: `server_folder/mods/`

**1.5 Install Simple Voice Chat (Optional)**

1. Download **Simple Voice Chat** from [Modrinth](https://modrinth.com/mod/voice-chat)
2. Put the `.jar` file in: `server_folder/mods/`
3. Install client-side mod on your Minecraft client too

**1.6 Start Server**

```bash
# Windows
java -Xmx4G -jar fabric-server-launcher.jar nogui

# Linux/macOS
java -Xmx4G -jar fabric-server-launcher.jar nogui
```

---

## 🎯 First Setup

### Step 1: Access Web Dashboard

1. Start your Minecraft server
2. Open browser: **http://localhost:8080**
   - (Replace `localhost` with your server IP if remote)
3. You'll see the login screen

### Step 2: Create First Account

**Default Login:**
- The first user to login becomes **ADMIN**
- Enter any username and password
- This account will have full access

**Example:**
- Username: `admin`
- Password: `your-secure-password`

### Step 3: Initial Configuration

1. After login, go to **Core Logic** tab
2. Configure basic settings:
   - **Cognitive Cycle**: How often NPCs think (in ticks, 20 ticks = 1 second)
   - **Evolutionary Coefficient**: How fast personalities change (0.0 - 1.0)
   - **External Interface**: Enable/disable web dashboard
   - **Broadcast Port**: Change web port (default: 8080)

---

## ⚙️ Configuration

### AI Providers

The mod uses a **generic AI provider system** - you can add ANY AI service!

**To add an AI provider:**

1. Go to **Core Logic** tab in web dashboard
2. Click **"Add Provider"** button
3. Fill in:
   - **Provider Name**: e.g., "OpenAI GPT-4", "DeepSeek", "Ollama"
   - **API Key**: Your API key (can be empty for local models like Ollama)
   - **API Endpoint**: The API URL
   - **Model**: Model name to use

**Popular AI Service Examples:**

| Service | Endpoint | Model |
|---------|----------|-------|
| **OpenAI** | `https://api.openai.com/v1` | `gpt-4o` |
| **DeepSeek** | `https://api.deepseek.com/v1` | `deepseek-chat` |
| **Grok (xAI)** | `https://api.x.ai/v1` | `grok-beta` |
| **Ollama** | `http://localhost:11434/v1` | `llama3.2` |
| **Anthropic** | `https://api.anthropic.com/v1` | `claude-sonnet-4-20250514` |

**Quick Add with Chips:**
- Click the preset chips (OpenAI, DeepSeek, Grok, Ollama, Anthropic)
- The form will auto-fill with correct endpoint and model
- Just add your API key and save!

### Server Settings

Edit `config/ainpcconnector.json`:

```json
{
  "webServer": {
    "enabled": true,
    "ip": null,
    "port": 8080
  },
  "ai": {
    "defaultProviderId": "your-provider-id"
  },
  "npc": {
    "aiThinkIntervalTicks": 40,
    "personalityEvolutionRate": 0.01
  },
  "voice": {
    "enabled": false,
    "ttsProvider": "browser"
  }
}
```

---

## 🤖 Adding AI Providers

### Option 1: Using Web Dashboard (Recommended)

1. Login to web dashboard
2. Go to **Core Logic** tab
3. Click **"Add Provider"**
4. Fill in the form:
   ```
   Provider Name: My AI Service
   API Key: sk-... (your API key)
   API Endpoint: https://api.example.com/v1
   Model: model-name
   ```
5. Click **"Save Provider"**
6. Select as **Default Provider** from dropdown

### Option 2: Direct Database Entry

For advanced users, you can add providers directly to SQLite:

```sql
INSERT INTO ai_providers (id, name, api_key, endpoint, model)
VALUES (
  'provider-uuid',
  'My AI Provider',
  'your-api-key',
  'https://api.example.com/v1',
  'model-name'
);
```

### Getting API Keys

| Service | Link to Get Key |
|---------|----------------|
| OpenAI | [platform.openai.com](https://platform.openai.com/api-keys) |
| DeepSeek | [platform.deepseek.com](https://platform.deepseek.com/) |
| Grok | [x.ai](https://x.ai/) |
| Anthropic | [console.anthropic.com](https://console.anthropic.com/) |
| OpenRouter | [openrouter.ai/keys](https://openrouter.ai/keys) |

---

## 👾 Creating & Configuring NPCs

### Spawn an Easy NPC

1. In-game, get an **Easy NPC Spawn Egg**
2. Right-click to spawn the NPC
3. The NPC appears in the web dashboard

### Configure NPC via Web Dashboard

1. Go to **Entity Registry** tab
2. Click on an NPC card
3. Configure:

**Basic Settings:**
- **AI Consciousness**: Enable/disable AI for this NPC
- **Vocal Output**: Enable/disable text-to-speech

**Neural Interface:**
- **Provider**: Choose which AI provider this NPC uses
- **Persona Core**: Describe the NPC's personality
- **Operational Directives**: System prompt for the AI

**Personality Coefficients:**
- **Social** (Friendliness): How friendly the NPC is (0.0 - 1.0)
- **Curie** (Curiosity): How curious the NPC is (0.0 - 1.0)
- **Wit** (Humor): How humorous the NPC is (0.0 - 1.0)
- **Chaos** (Aggression): How aggressive the NPC is (0.0 - 1.0)

### Example NPC Configurations

**Friendly Shopkeeper:**
```
Persona: A helpful shopkeeper who loves trading and making deals.
System Prompt: You are a friendly shopkeeper in a Minecraft village.
You greet players warmly, offer fair trades, and remember repeat customers.

Personality: High Friendliness (0.9), High Curiosity (0.7)
```

**Mysterious Wizard:**
```
Persona: An ancient wizard with cryptic wisdom and riddles.
System Prompt: You are a wise wizard who speaks in riddles and metaphors.
You offer magical advice but never give straight answers.

Personality: Low Humor (0.2), High Curiosity (0.8)
```

**Grumpy Guard:**
```
Persona: A tired guard who's seen too much and just wants peace.
System Prompt: You are a grumpy castle guard. You're suspicious of strangers
but loyal to your king. You complain about your job.

Personality: Low Friendliness (0.3), Low Curiosity (0.2)
```

---

## 🌐 Web Dashboard

### Login

- **URL**: `http://your-server-ip:8080`
- **First Login**: Creates admin account
- **Subsequent Logins**: Uses the created account

### Tabs Overview

| Tab | Description |
|-----|-------------|
| **Entity Registry** | View all NPCs, edit their settings |
| **Core Logic** | Configure AI providers, global settings |
| **Access Control** | Manage user accounts (admin only) |

### NPC Status Indicators

- 🟢 **Online** - NPC is active and AI is enabled
- 🔴 **Offline** - NPC exists but AI is disabled
- 🟡 **Thinking** - NPC is currently processing

---

## 🗄️ Database Management

### SQLite Database Location

```
config/ainpc_data.db
```

### Database Tables

| Table | Description |
|-------|-------------|
| `config` | Global configuration key-value pairs |
| `ai_providers` | AI provider configurations |
| `users` | Web dashboard user accounts |
| `npc_profiles` | NPC settings and personalities |
| `conversations` | Chat history per NPC |

### Viewing Database

**Using SQLite Browser:**

1. Download [DB Browser for SQLite](https://sqlitebrowser.org/)
2. Open `config/ainpc_data.db`
3. Browse tables and data

**Using Command Line:**

```bash
# Open database
sqlite3 config/ainpc_data.db

# List tables
.tables

# View NPCs
SELECT * FROM npc_profiles;

# View providers
SELECT * FROM ai_providers;

# Exit
.quit
```

### Backup Database

```bash
# Simple file copy
cp config/ainpc_data.db config/ainpc_data.db.backup

# Or using SQLite
sqlite3 config/ainpc_data.db ".backup config/ainpc_data.db.backup"
```

### Restore Database

```bash
# Stop server first
# Then restore
cp config/ainpc_data.db.backup config/ainpc_data.db

# Restart server
```

---

## 🛠️ Building from Source

### Prerequisites

- Java 21
- Gradle 8.x

### Build Steps

```bash
# Clone repository
git clone https://github.com/your-repo/easy-npc-kracked-ai.git
cd easy-npc-kracked-ai

# Build mod
./gradlew build

# Output: build/libs/*.jar
```

### Development

```bash
# Run development build
./gradlew build

# Copy to test server
cp build/libs/*.jar /path/to/minecraft/server/mods/
```

---

## 🆘 Troubleshooting

### Web Dashboard Not Accessible

**Problem:** Can't access `http://localhost:8080`

**Solutions:**
1. Check if server is running
2. Check `config/ainpcconnector.json` - ensure `webServer.enabled = true`
3. Check firewall settings
4. Try using server IP instead of localhost
5. Check port 8080 is not blocked

### NPC Not Responding

**Problem:** NPC doesn't respond when talked to

**Solutions:**
1. Check AI is enabled for the NPC in web dashboard
2. Verify AI provider is configured correctly
3. Check API key is valid
4. Test API connection in Core Logic tab
5. Check server logs for errors

### Database Errors

**Problem:** SQLite database errors

**Solutions:**
1. Stop server
2. Check database file exists: `config/ainpc_data.db`
3. Check file permissions
4. Restore from backup if corrupted
5. Delete database and restart server (will recreate)

### Out of Memory Errors

**Problem:** Server crashes with memory errors

**Solutions:**
1. Increase Java heap size:
   ```bash
   java -Xmx8G -jar fabric-server-launcher.jar nogui
   ```
2. Reduce `aiThinkIntervalTicks` in config
3. Reduce number of AI-enabled NPCs
4. Use smaller AI models

### Login Issues

**Problem:** Can't login to web dashboard

**Solutions:**
1. Clear browser cache
2. Check users table in database
3. Reset via database:
   ```sql
   DELETE FROM users;
   -- First login after this will create new admin
   ```

---

## 📁 Project Structure

```
easy-npc-kracked-ai/
├── src/main/
│   ├── java/com/ainpcconnector/
│   │   ├── AINpcConnectorMod.java       # Main mod class
│   │   ├── ai/
│   │   │   ├── AIProvider.java          # AI provider interface
│   │   │   ├── AIProviderFactory.java   # Provider factory
│   │   │   └── openai/
│   │   │       └── OpenAIProvider.java  # OpenAI-compatible implementation
│   │   ├── behavior/
│   │   │   └── AIController.java        # NPC AI logic controller
│   │   ├── config/
│   │   │   ├── ModConfig.java           # Configuration classes
│   │   │   ├── ConfigManager.java       # Config management
│   │   │   └── DatabaseManager.java     # SQLite database manager
│   │   ├── npc/
│   │   │   ├── NPCManager.java          # NPC lifecycle management
│   │   │   ├── NPCRegistry.java         # NPC profile storage
│   │   │   └── NPCProfile.java          # NPC data model
│   │   ├── voice/
│   │   │   └── VoiceIntegration.java    # TTS integration
│   │   └── web/
│   │       ├── WebServer.java           # Javalin web server
│   │       ├── handlers/
│   │       │   ├── AIHandler.java       # AI endpoints
│   │       │   ├── AuthHandler.java     # Authentication
│   │       │   ├── ConfigHandler.java   # Config endpoints
│   │       │   └── NPCHandler.java      # NPC endpoints
│   │       └── auth/
│   │           ├── AuthManager.java     # JWT/BCrypt auth
│   │           └── UserRepository.java   # User storage
│   └── resources/
│       ├── fabric.mod.json              # Mod metadata
│       └── web/
│           ├── index.html               # Web UI
│           ├── css/styles.css           # Styles
│           └── js/app.js                # Frontend logic
├── build.gradle                         # Gradle build config
└── README.md                            # This file
```

---

## 🔌 API Endpoints

### Authentication
| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/auth/login` | Login with username/password |
| `POST` | `/api/auth/logout` | Logout (invalidate token) |

### Configuration
| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/config` | Get current configuration |
| `PUT` | `/api/config` | Update configuration |

### AI Providers
| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/config/providers` | List all AI providers |
| `POST` | `/api/config/providers` | Add new provider |
| `PUT` | `/api/config/providers/:id` | Update provider |
| `DELETE` | `/api/config/providers/:id` | Delete provider |

### NPCs
| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/npcs` | List all NPCs |
| `GET` | `/api/npcs/:id` | Get NPC details |
| `PUT` | `/api/npcs/:id` | Update NPC config |
| `DELETE` | `/api/npcs/:id` | Delete NPC profile |

### AI Testing
| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/ai/test` | Test AI connection |

### Users (Admin Only)
| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/users` | List all users |
| `POST` | `/api/users` | Create new user |
| `DELETE` | `/api/users/:username` | Delete user |

---

## 🔒 Security Best Practices

1. **Strong Passwords** - Use complex passwords for dashboard accounts
2. **Firewall** - Only expose web port if needed, use VPN for remote access
3. **HTTPS** - Use reverse proxy with SSL for production
4. **API Keys** - Never share API keys, rotate periodically
5. **Backups** - Regularly backup `ainpc_data.db`
6. **Updates** - Keep mod and dependencies updated

---

## 📄 License

This mod is licensed under MIT License.

---

## 🙏 Credits & Acknowledgments

- **Easy NPC** by [Paulevs](https://modrinth.com/mod/easy+npcs) - Base NPC system
- **Simple Voice Chat** by [Henkelmax](https://modrinth.com/mod/voice-chat) - Voice chat integration
- **FabricMC** - Minecraft modding framework
- **Javalin** - Web framework
- **SQLite** - Embedded database

---

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

---

## 📞 Support

- **Issues**: [GitHub Issues](https://github.com/your-repo/easy-npc-kracked-ai/issues)
- **Discord**: [Join our Discord](https://discord.gg/your-server)

---

Made with 💜 by **KRACKEDDEV**
