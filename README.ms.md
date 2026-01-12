# Easy NPC Kracked AI (Edisi Pembangun)

> 🧠 **Mod Fabric Minecraft** - NPC AI Autonomi dengan Storan Tempatan + Papan Pemuka Web

**Bahasa:**
- 🇬🇧 [English](README.md)
- 🇲🇾 **Bahasa Melayu** (Anda di sini)

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
║      Jadikan NPC Anda Entiti yang Berfikir & Belajar!        ║
╚══════════════════════════════════════════════════════════════╝
```

---

## ✨ Ciri-ciri

### 🎛️ Papan Pemuka Web (Web Dashboard)
- **Pengurusan NPC Masa Nyata** - Pantau status NPC dari pelayar web anda
- **Konsol Langsung** - Lihat proses pemikiran AI dan log dialog
- **Konfigurasi** - Ubah tetapan AI tanpa perlu mulakan semula game
- **Kotak Pasir API** - Uji respons AI terus melalui antara muka web

### 🤖 Integrasi AI
- **Sokongan Penyedia Universal** - Berfungsi dengan mana-mana API yang serasi dengan OpenAI
- **Penyedia Disokong**:
  - OpenAI (GPT-4, GPT-3.5)
  - Anthropic (Claude)
  - LLM Tempatan (Ollama/LM Studio melalui HTTP)
- **Enjin Personaliti** - NPC mengembangkan personaliti mereka dari semasa ke semasa
- **Sistem Memori** - Memori kekal berasaskan SQLite untuk interaksi jangka panjang

### 🗣️ Suara & Interaksi
- **Teks-ke-Ucapan (TTS)** - Sokongan TTS berasaskan pelayar atau luaran
- **API Sembang Suara** - Integrasi dengan mod Simple Voice Chat
- **Dialog Dinamik** - Perbualan yang peka konteks berdasarkan peristiwa dalam permainan

---

## 📋 Isi Kandungan

1. [Prasyarat](#-prasyarat)
2. [Mula Cepat (Web Sahaja)](#-mula-cepat-antara-muka-web-sahaja)
3. [Pemasangan (Mod Penuh)](#-pemasangan-pembangunan-mod)
4. [Konfigurasi](#-konfigurasi)
5. [Menjalankan & Menyahpepijat](#-menjalankan--menyahpepijat)
6. [Struktur Projek](#-struktur-projek)
7. [Penyelesaian Masalah](#-penyelesaian-masalah)

---

## 📦 Prasyarat

### Perisian Diperlukan

| Perisian | Versi Minimum | Disyorkan | Muat Turun |
|----------|---------------|-----------|------------|
| **Java JDK** | v21 | v21 (LTS) | [adoptium.net](https://adoptium.net/temurin/releases/?version=21) |
| **Git** | Mana-mana | Terkini | [git-scm.com](https://git-scm.com/) |
| **Minecraft** | 1.21.x | 1.21.1 | [minecraft.net](https://www.minecraft.net/) |

### Keperluan Sistem

- **RAM**: Minimum 4GB (diperuntukkan untuk Minecraft), Disyorkan 8GB+
- **OS**: Windows 10/11, macOS, Linux

---

## 🎯 Mula Cepat: Antara Muka Web Sahaja

**"Saya nak buka web dia sahaja"**

Jika anda hanya mahu menguji Papan Pemuka/Antara Muka Web tanpa menjalankan permainan Minecraft sepenuhnya:

### 1. Sahkan Pemasangan Java
Pastikan anda mempunyai Java 21:
```powershell
java -version
```

### 2. Jalankan Pelayan Ujian
**Windows:**
```powershell
.\gradlew.bat runTestServer
```

**Mac/Linux:**
```bash
./gradlew runTestServer
```

### 3. Buka Papan Pemuka
Buka pelayar web anda dan pergi ke:
[http://localhost:8081](http://localhost:8081)

*(Tekan `Ctrl+C` di terminal untuk berhenti)*

---

## 🚀 Pemasangan (Pembangunan Mod)

Bahagian ini adalah untuk pembangun yang ingin menjalankan mod di dalam Minecraft.

### Bahagian 1: Muat Turun & Tetapan

**1.1 Clone Repository**
```bash
git clone https://github.com/MoonWIRaja/Easy-NPC-Kracked-AI.git
cd Easy-NPC-Kracked-AI
```

**1.2 Jana Sumber (Generate Sources)**
Sebelum membuka dalam IDE anda, jana kod sumber Minecraft:

**Windows:**
```powershell
.\gradlew.bat genSources
```

**Mac/Linux:**
```bash
./gradlew genSources
```

### Bahagian 2: Jalankan Klien

Untuk melancarkan Minecraft dengan mod dipasang:

**Windows:**
```powershell
.\gradlew.bat runClient
```

**Mac/Linux:**
```bash
./gradlew runClient
```

Permainan akan bermula. Cipta dunia baru untuk mula menguji NPC.

---

## ⚙️ Konfigurasi

Mod ini menggunakan sistem `ModConfig` yang boleh dikonfigurasi melalui Papan Pemuka Web atau fail konfigurasi.

### Tetapan Pelayan Web
- **Port:** Lalai `8080` (Boleh ubah dalam `ModConfig`)
- **Alamat:** `0.0.0.0` atau `localhost`

### Konfigurasi Penyedia AI
Struktur `ModConfig.java` sedia ada membolehkan penambahan pelbagai penyedia:
- **ID:** Pengecam unik
- **Name:** Nama paparan (cth., "OpenAI GPT-4")
- **Endpoint:** URL API (cth., `https://api.openai.com/v1`)
- **API Key:** Kunci rahsia anda
- **Model:** Nama model (cth., `gpt-4-turbo`)

---

## 📁 Struktur Projek

```
easy-npc-kracked-ai/
├── src/main/java/com/ainpcconnector/
│   ├── AINpcConnectorMod.java       # Titik Masuk Mod
│   ├── ai/                          # Logik Penyedia AI
│   ├── behavior/                    # Minda NPC & Logik Ticking
│   ├── config/                      # Konfigurasi Mod (ModConfig.java)
│   ├── npc/                         # Pendaftaran NPC & Data Models
│   └── web/                         # Pelayan Web Javalin
│       ├── WebServer.java           # Tetapan Server
│       └── handlers/                # Titik Akhir API (Endpoints)
├── src/main/resources/
│   ├── assets/                      # Tekstur & Fail Bahasa
│   └── web/                         # Frontend Papan Pemuka Web
│       ├── index.html
│       ├── css/
│       └── js/
├── src/test/java/testserver/        # Pelayan Ujian Kendiri
└── build.gradle                     # Dependencies & Konfigurasi Binaan
```

---

## 🆘 Penyelesaian Masalah

### Arahan `gradlew` tidak dijumpai
Jika anda melihat `'gradlew' is not recognized`:
1. Pastikan anda berada di folder root.
2. Gunakan `.\gradlew.bat` pada Windows (dengan `.\` di depan).
3. Jika `gradlew.bat` hilang, anda boleh pulihkannya (tanya saya jika perlu).

### Port 8080 sedang digunakan
Jika pelayan web gagal bermula:
1. Semak jika aplikasi lain menggunakan port 8080.
2. Edit port dalam konfigurasi (atau `TestWebServer.java` untuk ujian).

### Ralat Versi Java
Projek ini memerlukan **Java 21**. Jika anda mendapat ralat versi:
1. Pasang JDK 21.
2. Tetapkan `JAVA_HOME` ke pemasangan JDK 21 anda.

---

## ❤️ Kredit

**Diselenggara & Dibangunkan oleh:**
**(MoonWiRaja & 4kmal4lif) KRACKEDDEV**

Dengan penghargaan khas kepada:
- **Paulevs** (Easy NPC Asal)
- **Henkelmax** (Simple Voice Chat)

---

Dibuat dengan 💜 oleh MoonWiRaja
