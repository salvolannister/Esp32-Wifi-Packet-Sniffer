# MUDS – Multi-Unit Detection System

A distributed WiFi packet-sniffing system built around multiple **ESP32** microcontrollers. The boards capture 802.11 probe-request frames broadcast by nearby WiFi-enabled devices, forward the data to a central **Java server**, and display device positions and statistics in a **JavaFX** desktop GUI backed by **SQLite**.

---

## How it works

```
┌─────────────┐        TCP (port 8080)       ┌──────────────────────────┐
│  ESP32 #1   │ ──────────────────────────► │                          │
│  ESP32 #2   │ ──────────────────────────► │   Java Server            │──► SQLite DB
│  ESP32 #N   │ ──────────────────────────► │   (Trilateration engine) │
└─────────────┘                             └──────────┬───────────────┘
                                                       │
                                                       ▼
                                             ┌─────────────────┐
                                             │  JavaFX GUI     │
                                             │  (Room map,     │
                                             │   time charts,  │
                                             │   MAC stats)    │
                                             └─────────────────┘
```

1. **Sniffing** – Each ESP32 is set to promiscuous (monitor) mode and rotates through WiFi channels 1–13, recording every probe-request packet it hears. For each packet it stores the hashed MAC address (MD5), RSSI, channel, SSID, timestamp, and 802.11 sequence number.
2. **Synchronisation** – On startup each board contacts the server over TCP and receives a Unix timestamp that tells it exactly when to start sniffing. This keeps all boards time-aligned so their RSSI readings are comparable.
3. **Transmission** – After every sniffing window (default 40 s) the board sends the captured packet list to the server and asks for the next start time.
4. **Trilateration** – The server combines RSSI measurements from all boards and solves a non-linear least-squares problem (using Apache Commons Math) to estimate the 2-D position of each detected device inside the monitored room.
5. **Hidden / randomised MACs** – A dedicated `HiddenMacFinder` module correlates 802.11 sequence numbers across readings to link randomised MAC addresses back to the same physical device.
6. **Persistence** – All packets and computed positions are stored in a local SQLite database.
7. **Visualisation** – The JavaFX GUI provides:
   - **Room diagram** – real-time plot of estimated device positions.
   - **Time diagram** – number of devices detected over time.
   - **MAC frequency view** – history of a specific MAC address.
   - **Configuration panel** – define room dimensions and set up ESP32 reference positions.

---

## Technologies

| Layer | Technology |
|---|---|
| ESP32 firmware | C, [ESP-IDF](https://github.com/espressif/esp-idf) (v3.x), FreeRTOS |
| Networking (ESP32) | lwIP TCP/IP stack |
| Time sync (ESP32) | SNTP |
| MAC anonymisation | mbedTLS MD5 |
| Server | Java 11 |
| Math / Trilateration | [Apache Commons Math 3](https://commons.apache.org/proper/commons-math/) – NonLinear Least-Squares |
| GUI | JavaFX |
| Database | SQLite via [sqlite-jdbc](https://github.com/xerial/sqlite-jdbc) |
| Build (ESP32) | GNU Make (`idf.py` / legacy `make` system) |
| Build (Java) | IntelliJ IDEA / manual classpath |

---

## Project structure

```
main.c                  ESP32 firmware (promiscuous sniffer + TCP client)
FinalEspProject/        Java server
  src/
    Server/             TCP server, packet aggregation, trilateration orchestration
    DB/                 SQLite helpers and queries
    DTO/                Data-transfer objects (Packet, Payload, …)
JavaFxGui/              JavaFX desktop application
  src/
    application/        Controllers and FXML views
    DB/                 GUI-side database access
    DTO/                Shared data objects
Trilateration/          Standalone trilateration library
  src/trilateration/    NonLinearLeastSquaresSolver, TrilaterationFunction
```

---

## Getting started

### ESP32 firmware

1. Copy the project into your ESP-IDF workspace.
2. Edit `main.c` and set `EXAMPLE_WIFI_SSID`, `EXAMPLE_WIFI_PASS`, and `HOST_IP_ADDR` to match your local network and server machine.
3. Flash and monitor:
   ```bash
   cd ./esp/Pds_project/Sniffer
   make flash monitor
   ```

### Java server & GUI

1. Open `FinalEspProject` and `JavaFxGui` in IntelliJ IDEA (or add the jars on the classpath manually).
2. Make sure `sqlite-jdbc-3.7.2.jar` is on the classpath.
3. Run `Main.java` inside `JavaFxGui` to start the GUI; the GUI launches the server internally.
4. Use the **Configuration** panel to name the room, place the ESP32 reference points (x, y coordinates), and start sniffing.

