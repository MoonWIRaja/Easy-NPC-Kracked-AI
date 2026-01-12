# Easy NPC Kracked AI

> 🧠 **Mod Minecraft Fabric** - NPC Bertenaga AI dengan Kekekalan SQLite

**Language / Bahasa:**
- 🇬🇧 [English](README.md)
- 🇲🇾 **Bahasa Melayu** (Anda di sini)

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
║      Jadikan NPC Anda sebagai Entiti yang Berfikir & Belajar!               ║
╚══════════════════════════════════════════════════════════════════════════════╝
```

---

## ✨ Ciri-ciri

### 🤖 Sokongan Penyedia AI Universal
- **Gunakan SEBARANG AI** - OpenAI, Anthropic, DeepSeek, Grok, Ollama, atau API serasi OpenAI
- **Tambah Penyedia Tanpa Had** - Hanya masukkan: Nama, API Key, Endpoint, Model
- **Setiap NPC Boleh Guna AI Berbeza** - Setiap NPC boleh menggunakan penyedia AI yang berbeza
- **Preset Pantas** - Satu klik untuk persediaan perkhidmatan AI popular

### 🧠 Perlakuan NPC Pintar
- **Fikir Automatik** - NPC berfikir dan bertindak balas autonomi
- **Sistem Ingatan** - NPC mengingati perbualan lalu dengan SQLite
- **Sistem Personaliti** - Sesuaikan keceriaan, ingin tahu, humor, agresif
- **Pembelajaran Dinamik** - Personaliti berkembang berdasarkan interaksi

### 🌐 Web Dashboard
- **Pengurusan Masa Nyata** - Konfigurasi NPC dari pelayar
- **Antara Muka Visual** - UI bertema isometrik yang cantik
- **Status Masa Nyata** - Lihat semua NPC aktif dan keadaan mereka
- **Authentikasi Selamat** - Dilindungi kata laluan dengan akses berasaskan peranan

### 🗄️ Pangkalan Data SQLite
- **Penyimpanan Kekal** - Semua data kekal walaupun server restart
- **NPC Tanpa Had** - Skala ke ratusan NPC AI
- **Sejarah Perbualan** - Sejarah chat penuh disimpan setiap NPC
- **Pengurusan Pengguna** - Banyak admin pengguna dengan login selamat

### 🎙️ Integrasi Suara
- **Teks-ke-Suara** - NPC boleh bercakap (perlukan Simple Voice Chat)
- **TTS Pelayar** - Guna sintesis suara pelayar built-in
- **TTS Luaran** - Sambung ke perkhidmatan TTS luaran

---

## 📋 Jadual Kandungan

1. [Prasyarat](#-prasyarat)
2. [Pemasangan](#-pemasangan)
3. [Persediaan Pertama](#-persediaan-pertama)
4. [Konfigurasi](#-konfigurasi)
5. [Menambah Penyedia AI](#-menambah-penyedia-ai)
6. [Membuat & Mengkonfigurasi NPC](#-membuat--mengkonfigurasi-npc)
7. [Web Dashboard](#-web-dashboard)
8. [Pengurusan Pangkalan Data](#-pengurusan-pangkalan-data)
9. [Masalah & Penyelesaian](#-masalah--penyelesaian)
10. [Struktur Projek](#-struktur-projek)

---

## 📦 Prasyarat

### Perisian Diperlukan

| Perisian | Versi | Muat Turun |
|----------|---------|----------|
| **Minecraft** | 1.21.11 | [minecraft.net](https://www.minecraft.net/) |
| **Fabric Loader** | Terkini | [fabricmc.net](https://fabricmc.net/use/) |
| **Java** | 21 | [oracle.com](https://www.oracle.com/java/technologies/downloads/) |
| **Easy NPC Mod** | Terkini | [modrinth.com/mod/easy+npcs](https://modrinth.com/mod/easy+npcs) |
| **Simple Voice Chat** (Pilihan) | Terkini | [modrinth.com/mod/voice-chat](https://modrinth.com/mod/voice-chat) |

### Keperluan Server

- **RAM**: Minimum 4GB, Disyorkan 8GB+ (untuk banyak NPC AI)
- **CPU**: Processor multi-core moden
- **Rangkaian**: Sambungan internet stabil (untuk panggilan API AI)

---

## 🚀 Pemasangan

### Bahagian 1: Pasang Server Minecraft Fabric

**1.1 Muat Turun Fabric Installer**

1. Pergi ke [fabricmc.net/use](https://fabricmc.net/use/)
2. Muat turun **Fabric Installer**
3. Jalankan installer

**1.2 Buat Server**

1. Pilih tab **"Server"**
2. Pilih lokasi pemasangan (cth: `C:\MinecraftServer`)
3. Klik **"Download Server"**
4. Klik **"Install Server"**

**1.3 Pasang Mod Easy NPC**

1. Muat turun **Easy NPC** dari [Modrinth](https://modrinth.com/mod/easy+npcs)
2. Letakkan fail `.jar` dalam: `server_folder/mods/`

**1.4 Pasang Easy NPC Kracked AI**

1. Muat turun **Easy NPC Kracked AI** dari [Modrinth](https://modrinth.com/mod/easy-npc-kracked-ai)
2. Letakkan fail `.jar` dalam: `server_folder/mods/`

**1.5 Pasang Simple Voice Chat (Pilihan)**

1. Muat turun **Simple Voice Chat** dari [Modrinth](https://modrinth.com/mod/voice-chat)
2. Letakkan fail `.jar` dalam: `server_folder/mods/`
3. Pasang mod di bahagian client Minecraft juga

**1.6 Mulakan Server**

```bash
# Windows
java -Xmx4G -jar fabric-server-launcher.jar nogui

# Linux/macOS
java -Xmx4G -jar fabric-server-launcher.jar nogui
```

---

## 🎯 Persediaan Pertama

### Langkah 1: Akses Web Dashboard

1. Mulakan server Minecraft anda
2. Buka pelayar: **http://localhost:8080**
   - (Ganti `localhost` dengan IP server jika remote)
3. Anda akan lihat skrin login

### Langkah 2: Buat Akaun Pertama

**Login Lalai:**
- Pengguna pertama akan menjadi **ADMIN**
- Masukkan sebarang username dan password
- Akaun ini akan mempunyai akses penuh

**Contoh:**
- Username: `admin`
- Password: `kata-laluan-selamat-anda`

### Langkah 3: Konfigurasi Awal

1. Selepas login, pergi ke tab **Core Logic**
2. Konfigurasi tetapan asas:
   - **Cognitive Cycle**: Berapa kerap NPC berfikir (dalam ticks, 20 ticks = 1 saat)
   - **Evolutionary Coefficient**: Betapa cepat personaliti berubah (0.0 - 1.0)
   - **External Interface**: Enable/disable web dashboard
   - **Broadcast Port**: Tukar port web (lalai: 8080)

---

## ⚙️ Konfigurasi

### Penyedia AI

Mod ini menggunakan **sistem penyedia AI generik** - anda boleh tambah SEBARANG perkhidmatan AI!

**Untuk menambah penyedia AI:**

1. Pergi ke tab **Core Logic** dalam web dashboard
2. Klik butang **"Add Provider"**
3. Isikan:
   - **Provider Name**: cth, "OpenAI GPT-4", "DeepSeek", "Ollama"
   - **API Key**: API key anda (boleh kosong untuk model lokal seperti Ollama)
   - **API Endpoint**: URL API
   - **Model**: Nama model untuk digunakan

**Contoh Perkhidmatan AI Popular:**

| Perkhidmatan | Endpoint | Model |
|--------------|----------|-------|
| **OpenAI** | `https://api.openai.com/v1` | `gpt-4o` |
| **DeepSeek** | `https://api.deepseek.com/v1` | `deepseek-chat` |
| **Grok (xAI)** | `https://api.x.ai/v1` | `grok-beta` |
| **Ollama** | `http://localhost:11434/v1` | `llama3.2` |
| **Anthropic** | `https://api.anthropic.com/v1` | `claude-sonnet-4-20250514` |

**Tambah Pantas dengan Chips:**
- Klik preset chips (OpenAI, DeepSeek, Grok, Ollama, Anthropic)
- Borang akan auto-isikan dengan endpoint dan model yang betul
- Hanya tambah API key anda dan simpan!

### Tetapan Server

Edit `config/ainpcconnector.json`:

```json
{
  "webServer": {
    "enabled": true,
    "ip": null,
    "port": 8080
  },
  "ai": {
    "defaultProviderId": "id-penyedia-anda"
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

## 🤖 Menambah Penyedia AI

### Pilihan 1: Menggunakan Web Dashboard (Disyorkan)

1. Login ke web dashboard
2. Pergi ke tab **Core Logic**
3. Klik **"Add Provider"**
4. Isikan borang:
   ```
   Provider Name: Perkhidmatan AI Saya
   API Key: sk-... (API key anda)
   API Endpoint: https://api.example.com/v1
   Model: nama-model
   ```
5. Klik **"Save Provider"**
6. Pilih sebagai **Default Provider** dari dropdown

### Pilihan 2: Masukan Terus ke Database

Untuk pengguna mahir, anda boleh tambah penyedia terus ke SQLite:

```sql
INSERT INTO ai_providers (id, name, api_key, endpoint, model)
VALUES (
  'provider-uuid',
  'Penyedia AI Saya',
  'api-key-anda',
  'https://api.example.com/v1',
  'nama-model'
);
```

### Mendapatkan API Keys

| Perkhidmatan | Pautan Dapatkan Key |
|---------------|---------------------|
| OpenAI | [platform.openai.com](https://platform.openai.com/api-keys) |
| DeepSeek | [platform.deepseek.com](https://platform.deepseek.com/) |
| Grok | [x.ai](https://x.ai/) |
| Anthropic | [console.anthropic.com](https://console.anthropic.com/) |
| OpenRouter | [openrouter.ai/keys](https://openrouter.ai/keys) |

---

## 👾 Membuat & Mengkonfigurasi NPC

### Spawn Easy NPC

1. Dalam permainan, dapatkan **Easy NPC Spawn Egg**
2. Klik kanan untuk spawn NPC
3. NPC akan muncul dalam web dashboard

### Konfigurasi NPC melalui Web Dashboard

1. Pergi ke tab **Entity Registry**
2. Klik pada kad NPC
3. Konfigurasi:

**Tetapan Asas:**
- **AI Consciousness**: Enable/disable AI untuk NPC ini
- **Vocal Output**: Enable/disable teks-ke-suara

**Antara Muka Neural:**
- **Provider**: Pilih penyedia AI untuk NPC ini
- **Persona Core**: Perihalkan personaliti NPC
- **Operational Directives**: System prompt untuk AI

**Pekali Personaliti:**
- **Social** (Keceriaan): Betapa mesra NPC (0.0 - 1.0)
- **Curie** (Ingin Tahu): Betapa ingin tahu NPC (0.0 - 1.0)
- **Wit** (Humor): Betapa humor NPC (0.0 - 1.0)
- **Chaos** (Agresif): Betapa agresif NPC (0.0 - 1.0)

### Contoh Konfigurasi NPC

**Penjual Kedai Mesra:**
```
Persona: Penjual kedai yang suka berdagang dan buat perjanjian.
System Prompt: Anda adalah penjual kedai yang mesra di kampung Minecraft.
Anda sambut pemain dengan mesra, tawar dagangan adil, dan ingati pelanggan ulang.

Personaliti: Keceriaan Tinggi (0.9), Ingin Tahu Tinggi (0.7)
```

**Ahli Sihir Misteri:**
```
Persona: Ahli sihir kuno dengan kebijaksanaan kriptik dan teka-teki.
System Prompt: Anda adalah ahli sihir yang bijak yang bercakap dalam teka-teki dan metafora.
Anda tawar nasihat ajaib tapi tidak pernah beri jawapan terus.

Personaliti: Humor Rendah (0.2), Ingin Tahu Tinggi (0.8)
```

**Pengawal Gembira:**
```
Persona: Pengawal yang penat dan hanya mahukan ketenangan.
System Prompt: Anda adalah pengawal kota yang gembira. Anda curiga dengan orang asing
tetapi setia kepada raja anda. Anda mengeluh tentang kerja anda.

Personaliti: Keceriaan Rendah (0.3), Ingin Tahu Rendah (0.2)
```

---

## 🌐 Web Dashboard

### Login

- **URL**: `http://ip-server-anda:8080`
- **Login Pertama**: Cipta akaun admin
- **Login Selepasnya**: Guna akaun yang dicipta

### Gambaran Tab

| Tab | Keterangan |
|-----|-------------|
| **Entity Registry** | Lihat semua NPC, edit tetapan mereka |
| **Core Logic** | Konfigurasi penyedia AI, tetapan global |
| **Access Control** | Urus akaun pengguna (admin sahaja) |

### Penunjuk Status NPC

- 🟢 **Online** - NPC aktif dan AI dienable
- 🔴 **Offline** - NPC wujud tapi AI dimatikan
- 🟡 **Thinking** - NPC sedang memproses

---

## 🗄️ Pengurusan Pangkalan Data

### Lokasi Database SQLite

```
config/ainpc_data.db
```

### Jadual Database

| Jadual | Keterangan |
|-------|-------------|
| `config` | Konfigurasi global key-value |
| `ai_providers` | Konfigurasi penyedia AI |
| `users` | Akaun pengguna web dashboard |
| `npc_profiles` | Tetapan dan personaliti NPC |
| `conversations` | Sejarah chat setiap NPC |

### Melihat Database

**Menggunakan SQLite Browser:**

1. Muat turun [DB Browser for SQLite](https://sqlitebrowser.org/)
2. Buka `config/ainpc_data.db`
3. Lihat jadual dan data

**Menggunakan Command Line:**

```bash
# Buka database
sqlite3 config/ainpc_data.db

# Senarai jadual
.tables

# Lihat NPC
SELECT * FROM npc_profiles;

# Lihat penyedia
SELECT * FROM ai_providers;

# Keluar
.quit
```

### Backup Database

```bash
# Salin fail mudah
cp config/ainpc_data.db config/ainpc_data.db.backup

# Atau guna SQLite
sqlite3 config/ainpc_data.db ".backup config/ainpc_data.db.backup"
```

### Pulihkan Database

```bash
# Hentikan server dulu
# Kemudian pulihkan
cp config/ainpc_data.db.backup config/ainpc_data.db

# Mulakan semula server
```

---

## 🛠️ Membina dari Sumber

### Prasyarat

- Java 21
- Gradle 8.x

### Langkah Membina

```bash
# Clone repository
git clone https://github.com/your-repo/easy-npc-kracked-ai.git
cd easy-npc-kracked-ai

# Bina mod
./gradlew build

# Output: build/libs/*.jar
```

### Pembangunan

```bash
# Bina versi development
./gradlew build

# Salin ke server ujian
cp build/libs/*.jar /path/to/minecraft/server/mods/
```

---

## 🆘 Masalah & Penyelesaian

### Web Dashboard Tidak Boleh Diakses

**Masalah:** Tidak boleh akses `http://localhost:8080`

**Penyelesaian:**
1. Periksa jika server berjalan
2. Periksa `config/ainpcconnector.json` - pastikan `webServer.enabled = true`
3. Periksa tetapan firewall
4. Cuba guna IP server instead of localhost
5. Periksa port 8080 tidak dihalang

### NPC Tidak Bertindak Balas

**Masalah:** NPC tidak respon bila bercakap

**Penyelesaian:**
1. Periksa AI dienable untuk NPC dalam web dashboard
2. Sahkan penyedia AI dikonfigurasi dengan betul
3. Periksa API key sah
4. Test sambungan API dalam tab Core Logic
5. Periksa log server untuk ralat

### Ralat Database

**Masalah:** Ralat database SQLite

**Penyelesaian:**
1. Hentikan server
2. Periksa fail database wujud: `config/ainpc_data.db`
3. Periksa keizinan fail
4. Pulih dari backup jika rosak
5. Padam database dan mulakan semula server (akan cipta baru)

### Ralat Kekurangan Memori

**Masalah:** Server crash dengan ralat memori

**Penyelesaian:**
1. Tingkatkan saiz heap Java:
   ```bash
   java -Xmx8G -jar fabric-server-launcher.jar nogui
   ```
2. Kurangkan `aiThinkIntervalTicks` dalam config
3. Kurangkan bilangan NPC ber-AI
4. Guna model AI yang lebih kecil

### Masalah Login

**Masalah:** Tidak boleh login ke web dashboard

**Penyelesaian:**
1. Kosongkan cache pelayar
2. Periksa jadual users dalam database
3. Reset melalui database:
   ```sql
   DELETE FROM users;
   -- Login pertama selepas ini akan cipta admin baru
   ```

---

## 📁 Struktur Projek

```
easy-npc-kracked-ai/
├── src/main/
│   ├── java/com/ainpcconnector/
│   │   ├── AINpcConnectorMod.java       # Kelas mod utama
│   │   ├── ai/
│   │   │   ├── AIProvider.java          # Antara muka penyedia AI
│   │   │   ├── AIProviderFactory.java   # Factory penyedia
│   │   │   └── openai/
│   │   │       └── OpenAIProvider.java  # Implementasi serasi OpenAI
│   │   ├── behavior/
│   │   │   └── AIController.java        # Kawalan logik AI NPC
│   │   ├── config/
│   │   │   ├── ModConfig.java           # Kelas konfigurasi
│   │   │   ├── ConfigManager.java       # Pengurusan config
│   │   │   └── DatabaseManager.java     # Pengurus database SQLite
│   │   ├── npc/
│   │   │   ├── NPCManager.java          # Pengurusan lifecycle NPC
│   │   │   ├── NPCRegistry.java         # Penyimpanan profil NPC
│   │   │   └── NPCProfile.java          # Model data NPC
│   │   ├── voice/
│   │   │   └── VoiceIntegration.java    # Integrasi TTS
│   │   └── web/
│   │       ├── WebServer.java           # Server web Javalin
│   │       ├── handlers/
│   │       │   ├── AIHandler.java       # Endpoint AI
│   │       │   ├── AuthHandler.java     # Authentikasi
│   │       │   ├── ConfigHandler.java   # Endpoint config
│   │       │   └── NPCHandler.java      # Endpoint NPC
│   │       └── auth/
│   │           ├── AuthManager.java     # Auth JWT/BCrypt
│   │           └── UserRepository.java   # Penyimpanan user
│   └── resources/
│       ├── fabric.mod.json              # Metadata mod
│       └── web/
│           ├── index.html               # UI Web
│           ├── css/styles.css           # Gaya
│           └── js/app.js                # Logik frontend
├── build.gradle                         # Konfigurasi build Gradle
└── README.md                            # Fail ini
```

---

## 🔌 API Endpoints

### Authentikasi
| Kaedah | Endpoint | Keterangan |
|--------|----------|-------------|
| `POST` | `/api/auth/login` | Login dengan username/kata laluan |
| `POST` | `/api/auth/logout` | Logout (nurkan token) |

### Konfigurasi
| Kaedah | Endpoint | Keterangan |
|--------|----------|-------------|
| `GET` | `/api/config` | Dapat konfigurasi semasa |
| `PUT` | `/api/config` | Kemaskini konfigurasi |

### Penyedia AI
| Kaedah | Endpoint | Keterangan |
|--------|----------|-------------|
| `GET` | `/api/config/providers` | Senarai semua penyedia AI |
| `POST` | `/api/config/providers` | Tambah penyedia baru |
| `PUT` | `/api/config/providers/:id` | Kemaskini penyedia |
| `DELETE` | `/api/config/providers/:id` | Padam penyedia |

### NPC
| Kaedah | Endpoint | Keterangan |
|--------|----------|-------------|
| `GET` | `/api/npcs` | Senarai semua NPC |
| `GET` | `/api/npcs/:id` | Dapat butiran NPC |
| `PUT` | `/api/npcs/:id` | Kemaskini konfig NPC |
| `DELETE` | `/api/npcs/:id` | Padam profil NPC |

### Ujian AI
| Kaedah | Endpoint | Keterangan |
|--------|----------|-------------|
| `POST` | `/api/ai/test` | Uji sambungan AI |

### Pengguna (Admin Sahaja)
| Kaedah | Endpoint | Keterangan |
|--------|----------|-------------|
| `GET` | `/api/users` | Senarai semua pengguna |
| `POST` | `/api/users` | Cipta pengguna baru |
| `DELETE` | `/api/users/:username` | Padam pengguna |

---

## 🔒 Amalan Keselamatan

1. **Kata Laluan Kuat** - Guna kata laluan kompleks untuk akaun dashboard
2. **Firewall** - Hanya dedahkan port web jika perlu, guna VPN untuk akses remote
3. **HTTPS** - Guna reverse proxy dengan SSL untuk produksi
4. **API Keys** - Jangan kongsi API key, tukar secara berkala
5. **Backup** - Backup `ainpc_data.db` secara kerap
6. **Kemaskini** - Kekalkan mod dan dependencies dikemaskini

---

## 📄 Lesen

Mod ini lesenkan di bawah MIT License.

---

## 🙏 Kredit & Penghargaan

- **Easy NPC** oleh [Paulevs](https://modrinth.com/mod/easy+npcs) - Sistem NPC asas
- **Simple Voice Chat** oleh [Henkelmax](https://modrinth.com/mod/voice-chat) - Integrasi suara
- **FabricMC** - Kerangka kerja modding Minecraft
- **Javalin** - Kerangka kerja web
- **SQLite** - Database embedded

---

## 🤝 Menyumbang

Sumbangan dialu-alukan! Sila hantar Pull Request.

---

## 📞 Sokongan

- **Isu**: [GitHub Issues](https://github.com/your-repo/easy-npc-kracked-ai/issues)
- **Discord**: [Join Discord Kami](https://discord.gg/your-server)

---

Dibuat dengan 💜 oleh **KRACKEDDEV**
