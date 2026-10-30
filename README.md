# Chest Transporter CleanRoom

![Fabric](https://img.shields.io/badge/Loader-Fabric-blue?style=for-the-badge) ![NeoForge](https://img.shields.io/badge/Loader-NeoForge-blue?style=for-the-badge)
![Minecraft](https://img.shields.io/badge/Minecraft-1.20%20-%201.21.3+-green?style=for-the-badge)
![License](https://img.shields.io/badge/License-MIT-yellow?style=for-the-badge)
![Clean-Room](https://img.shields.io/badge/Clean--Room-100%25%20Verified-brightgreen?style=for-the-badge)

<p align="center">
  <img src="icon.png" width="128" height="128" alt="Chest Transporter CleanRoom Icon"/>
</p>

> **Tiered container carrier tools with NBT snapshotting and tactical movement debuffs.**

A genuine, from-scratch clean-room implementation compatible across **Fabric & NeoForge** for Minecraft **1.20 - 1.21.3+**.

---

## 🌟 Key Features

- **5 Progressive Transporter Tiers**: Wood (1 use), Iron (10 uses), Gold (20 uses), Diamond (50 uses), and Netherite (200 uses, fireproof).
- **Full State Snapshotting**: Seamlessly captures inventory contents, custom anvil names, lock codes, and double-chest relative partner offsets.
- **Mob Spawner Extraction**: Netherite-tier transporters can carefully pick up and relocate monster spawners in-world without losing spawned entity types.
- **Tactical Carrying Debuffs**: Moving heavy filled containers applies immersive debuffs (Wood: Slowness II & Mining Fatigue II; Iron: Slowness I; Diamond/Netherite: zero debuffs).
- **Sneak Activation Requirement**: Shift-right-click to capture prevents accidental picking up during normal chest interactions.

---

## 🛡️ Clean-Room Provenance & Architecture

This mod is an independent, clean-room implementation engineered entirely from first principles:
- **Zero Third-Party Code Copying**: All algorithms, event handlers, and data structures were implemented from scratch.
- **Modern Standards**: Developed in modern Java (LTS 17 & 21) adhering strictly to standard loader lifecycles.
- **Mathematical Invariants**: Fully verified using independent 4-tier End-to-End (E2E) test suites with 100% passing assertions.

---

## 📦 Installation Guide

### Via Mod Launcher (Recommended)
1. In the **Modrinth App**, **CurseForge App**, or **Prism Launcher**, search for `Chest Transporter CleanRoom`.
2. Select your loader (Fabric & NeoForge) and game version (1.20 - 1.21.3+).
3. Click **Install** to automatically download the mod and dependencies.

### Manual Installation
1. Download the latest `.jar` release matching your loader:
   - Fabric: `cleanroom_chesttransporter-1.0.0-fabric-*.jar`
   - NeoForge: `cleanroom_chesttransporter-1.0.0-neoforge-*.jar`
   
2. Place the downloaded `.jar` file into your `.minecraft/mods` folder.
3. Launch Minecraft and enjoy!

---

## ⚙️ Configuration

Configuration is automatically generated on first launch:
- **File**: `config/cleanroom_chesttransporter.json`: configure durability values, debuff toggles, and spawner pickup permissions.
- Edit with any text editor or via in-game configuration screens.

---

## 📄 License

This project is licensed under the terms of the **MIT License**.
See the [LICENSE](LICENSE) file for complete details.
