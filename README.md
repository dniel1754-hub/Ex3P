# Pac-Man Game - Ex3 Algorithm Implementation
adding a video about the packmaan - 
https://drive.google.com/file/d/1BbinE0IEB2oOyR0rhLZ4ECFRO3z8gC7I/view?usp=sharing

## Overview
This project implements an autonomous agent for the Pac-Man game as part of the Ex3 assignment. The core of the project is the `Ex3Algo` class, which calculates the optimal move for Pac-Man in real-time using pathfinding algorithms and heuristic scoring. The agent is designed to handle various game scenarios, ranging from simple food collection to complex evasion and hunting strategies across multiple difficulty levels.

## Game Structure

The game operates on a **cyclic world** (wrapping borders) and supports **5 distinct levels**, where the difficulty scales with the number of ghosts:

* **Level 0:** **0 Ghosts**. A pure pathfinding challenge. The agent focuses solely on collecting all food pellets (Pink points) efficiently.
* **Level 1:** **1 Ghost**. The agent must collect food while avoiding a single pursuer.
* **Level 2:** **2 Ghosts**. Increased danger density requires smarter positioning.
* **Level 3:** **3 Ghosts**. Requires careful navigation to avoid being trapped between enemies.
* **Level 4:** **4 Ghosts**. Maximum difficulty with four active threats on the board.

---

## Algorithm Logic (`Ex3Algo`)

The decision-making process is implemented in the `move()` method of `Ex3Algo.java`. The agent evaluates the board state in every frame and prioritizes actions based on the following hierarchy:

### 1. Peaceful Mode / Level 0
* **Condition:** If there are no monsters alive (or the game is at Level 0).
* **Action:** The algorithm calculates the **shortest path** (using BFS via `allDistance`) to the **closest Pink point** (food) and moves towards it to clear the board efficiently.

### 2. Danger Analysis
* Before moving, the agent generates a **Danger Map** (`DangerMaptonearest`).
* This map calculates the shortest distance from every tile on the board to the nearest active ghost. This data is crucial for the evasion logic.

### 3. Emergency Evasion (High Priority)
* **Condition:** If the minimum distance to any active monster is **less than 7 steps**.
* **Action:** The agent immediately switches to survival mode.
    * It evaluates all valid neighboring tiles.
    * It selects the neighbor that maximizes the distance from the closest monster.
    * This ensures the agent always "flees" to the safest possible immediate location.

### 4. Strategic Power-Ups (Green Points)
* **Condition:** If a **Green point** (power-up) is located within a radius of **4 steps or less**.
* **Safety Check:** The algorithm verifies that the shortest path to the Green point is safe (i.e., the distance to the point is shorter or equal to the distance of the closest monster to that point).
* **Action:** If safe, the agent prioritizes moving to the Green point to turn the ghosts into edible targets.

### 5. Hunting Mode
* **Condition:** If a monster is in an "eatable" state (blue/edible mode).
* **Action:**
    * The agent checks if the monster is reachable within the **remaining eatable time**.
    * If reachable, the agent pursues the monster to gain extra points.
* **Constraint:** The agent is strictly programmed **never to enter the Ghost House** (the spawn area), even while hunting.

---

## Key Functions

The implementation relies on several helper methods:

* **`move(PacmanGame game)`**: The main game loop. Parses the game state, builds the map, and executes the decision logic described above.
* **`DangerMaptonearest(...)`**: Creates a 2D array representing the safety level of the entire board.
* **`evaluatedir(...)`**: Assigns a numerical score to a potential move. It heavily penalizes moves leading to danger (distance < 2 or < 3) and rewards moves leading to safe spaces or food.
* **`calculatSafeSpace(...)`**: Uses BFS to count accessible tiles from a given position, ensuring the agent doesn't run into dead ends.
* **`cheksneighbor(...)`**: A utility function to handle coordinate math on the cyclic board.

## How to Run

1.  Compile the project files.
2.  Run the main runner class (e.g., `RunMyGame` or `Ex3Main`).
3.  To change the level, update the `CASE_SCENARIO` variable in `mygameinfo.java` or `GameInfo.java` (values 0-4).
4.  Ensure `IS_MANUAL` is set to `false` to let the `Ex3Algo` control the player.

---
**Assignment:** Ex3 - Introduction to Computer Science
**Author ID:** 212750947
