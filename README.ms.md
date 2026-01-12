# Easy NPC Kracked AI (Edisi Pembangun)

> 🧠 **Mod Minecraft Fabric Open Source** - NPC AI Autonomi dengan Storan Lokal

**Language / Bahasa:**
- 🇬🇧 [English](README.md)
- 🇲🇾 **Bahasa Melayu** (Anda di sini)

```
╔══════════════════════════════════════════════════════════════════════════════╗
║  ░▒▓ EASY NPC KRACKED AI ▓▒░                                                ║
║                                                                              ║
║    Jadikan NPC Anda sebagai Entiti yang Berfikir & Belajar!                  ║
║    REPOSITORY PEMBANGUNAN & KOD SUMBER                                       ║
╚══════════════════════════════════════════════════════════════════════════════╝
```

---

## 🛠️ Gambaran Projek

Ini adalah repository pembangunan untuk **Easy NPC Kracked AI**, mod Minecraft Fabric yang mengintegrasikan LLM (Large Language Models) ke dalam NPC. Berbeza dengan mod biasa, projek ini mempunyai **Javalin Web Server** terbina dalam untuk papan pemuka pengurusan masa nyata dan menggunakan **SQLite** untuk penyimpanan memori NPC yang kekal.

### Teknologi Digunakan
- **Mod Loader:** Fabric API
- **Bahasa:** Java 21
- **Sistem Build:** Gradle 8.x
- **Web Server:** Javalin (Embedded)
- **Database:** SQLite (Embedded)
- **Frontend:** Vanilla JS + CSS (dalam `src/main/resources/web`)

---

## 📋 Kandungan

1. [Prasyarat](#-prasyarat)
2. [Persediaan Persekitaran](#-persediaan-persekitaran)
3. [Membina dari Sumber](#-membina-dari-sumber)
4. [Menjalankan & Debugging](#-menjalankan--debugging)
5. [Pengujian](#-pengujian)
6. [Struktur Projek](#-struktur-projek)

---

## 📦 Prasyarat

Untuk menyumbang atau membina projek ini, anda memerlukan:

| Keperluan | Perincian |
|-------------|---------|
| **Java JDK** | Versi 21 (Diperlukan untuk Minecraft 1.21+) |
| **Git** | Untuk kawalan versi |
| **IDE** | IntelliJ IDEA (Disyorkan) atau VS Code |

---

## 🚀 Persediaan Persekitaran

### 1. Clone Repository

```bash
git clone https://github.com/MoonWIRaja/Easy-NPC-Kracked-AI.git
cd Easy-NPC-Kracked-AI
```

### 2. Generate Sumber Fabric

Sebelum membuka dalam IDE anda, jana kod sumber Minecraft:

```bash
# macOS / Linux
./gradlew genSources

# Windows
gradlew genSources
```

### 3. Import Projek
- **IntelliJ IDEA:** File > Open > Pilih `build.gradle` > Open as Project.
- **VS Code:** Buka folder, pasang "Extension Pack for Java".

---

## 🔨 Membina dari Sumber

Untuk compile mod menjadi fail `.jar`:

```bash
./gradlew build
```

**Lokasi Output:**
Fail jar akhir akan berada di `easy-npc-kracked-ai/build/libs/`.

---

## 🎮 Menjalankan & Debugging

Anda tidak perlu install mod secara manual untuk test. Gradle menguruskan segalanya.

### Run Client
Melancarkan Minecraft dengan mod siap dipasang.

```bash
./gradlew runClient
```

### Run Server
Melancarkan dedicated server dengan mod.
*Nota: Anda mungkin perlu terima EULA dalam `run/eula.txt` selepas run kali pertama.*

```bash
./gradlew runServer
```

### Mengakses Web Dashboard semasa Dev
Apabila game/server sedang berjalan:
1. Port Lalai: **8080**
2. URL: http://localhost:8080
3. Login Lalai: Pengguna pertama menjadi Admin.

---

## 🧪 Pengujian

### Unit & Integration Tests

Kami menggunakan JUnit untuk menguji logik backend (Web server, parsing JSON, dll).

```bash
./gradlew test
```

**Fail Ujian Utama:**
- `src/test/java/testserver/TestWebServer.java` - Menguji endpoint server web Javalin tanpa melancarkan Minecraft.

---

## 📁 Struktur Projek

```
easy-npc-kracked-ai/
├── src/main/java/com/ainpcconnector/
│   ├── AINpcConnectorMod.java       # Titik Masuk Mod
│   ├── ai/                          # Logik Penyedia AI (OpenAI, Anthropic)
│   ├── behavior/                    # Minda NPC & Logik Ticking
│   ├── config/                      # Pengurus SQLite & Config
│   ├── npc/                         # Pendaftaran NPC & Model Data
│   └── web/                         # Web Server Terbina Dalam
│       ├── WebServer.java           # Persediaan Javalin
│       └── handlers/                # HTTP Route Handlers
├── src/main/resources/
│   ├── assets/                      # Texture & Fail Bahasa
│   └── web/                         # Frontend Web Dashboard
│       ├── index.html
│       ├── css/
│       └── js/
└── build.gradle                     # Dependencies & Konfigurasi Build
```

---

## 🤝 Menyumbang

1. Fork repository ini.
2. Buat feature branch (`git checkout -b feature/ciri-hebat`).
3. Commit perubahan anda.
4. Push ke branch.
5. Buka Pull Request.

---

## ❤️ Kredit

**Diselenggara & Dibangunkan oleh:**

**(MoonWiRaja & 4kmal4lif) KRACKEDDEV**

Dengan penghargaan khas kepada:
- **Paulevs** (Easy NPC Asal)
- **Henkelmax** (Simple Voice Chat)

---
