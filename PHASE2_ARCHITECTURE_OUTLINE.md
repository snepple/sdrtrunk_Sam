# Phase 2: Global UX Framework & Windows 11 Integration - Architectural Outline

This document outlines the architectural approach for Phase 2 of the sdrtrunk_Sam project modernization, focusing on transitioning from nested `JTabbedPane` components to a modern Sidebar Navigation model and deeply integrating native Windows 11 features.

## 1. Global Navigation Overhaul (Sidebar vs. TabbedPane)

The application currently relies heavily on `com.jidesoft.swing.JideTabbedPane` for top-level navigation in the `ControllerPanel` (managing views like "Now Playing", "Map", "Tuners", "Playlist Editor"), and also within individual modules like `NowPlayingPanel` for channel details.

**Proposed Implementation:**
*   **Introduce FlatLaf:** Update `build.gradle` to add FlatLaf (`com.formdev:flatlaf`) and its extras (`flatlaf-extras`) to support a modern UI baseline.
*   **Remove JideTabbedPane:** In `io.github.dsheirer.controller.ControllerPanel`, strip out `mTabbedPane`. The main view area will be refactored to use a `CardLayout` or a FlatLaf-styled content container.
*   **Create the Sidebar Navigation (`NavigationSidebar.java`):**
    *   Build a new, collapsible left-hand navigation panel.
    *   Categorize views into **Monitoring** (Now Playing, Map), **Configuration** (Tuners, Playlist Editor), and **System**.
    *   Use an `EventBus` or standard ActionListeners to orchestrate view switching in the main `CardLayout` when a sidebar item is clicked.
*   **Refactor `NowPlayingPanel`:** For nested tabs (Details, Events, Messages, Channel), remove the inner `JideTabbedPane`. These will be replaced with secondary vertical navigation or FlatLaf segmented control styles (pill buttons) to eliminate the "tab within a tab" anti-pattern and follow progressive disclosure principles.

## 2. Windows 11 Fluent UI & OS Sync

Currently, the app enforces `MetalLookAndFeel` on Mac/Linux and relies on the default LaF on Windows. We will modernize this specifically for Windows 11 environments using FlatLaf and JNA.

**Proposed Implementation:**
*   **OS Detection & Theme Initialization (`SDRTrunk.java`):**
    *   Implement an OS check during startup.
    *   If running on Windows 11 (or equivalent Windows 10 versions supporting features): Call `FlatWindowsNativeWindowBorder.install()` to enable native drop shadows, rounded corners, and Snap Layouts.
    *   Enable the Windows 11 "Mica" backdrop effect by configuring the root pane properties (e.g., `JRootPane.titleBarBackground`) and utilizing FlatLaf window decorations.
*   **JNA Registry Hook for Theme Sync:**
    *   Create a new class, `WindowsThemeMonitor.java`.
    *   Use JNA (`com.sun.jna.platform.win32.Advapi32` and `WinReg`) to open the registry key: `Software\Microsoft\Windows\CurrentVersion\Themes\Personalize`.
    *   Use `Advapi32.INSTANCE.RegNotifyChangeKeyValue` in a background thread to block and wait for changes to this registry key.
    *   When the event fires, read the `AppsUseLightTheme` value.
    *   Dispatch an update event to the Swing Event Dispatch Thread (EDT) via `SwingUtilities.invokeLater()` to switch the FlatLaf theme dynamically using `FlatAnimatedLafChange`.

## 3. Vector Icon Migration

*   **IconManager Migration:** Overhaul `io.github.dsheirer.gui.icon.IconManager` to support parsing and rendering SVG files. By utilizing FlatLaf Extras (`FlatSVGIcon`), we can seamlessly replace existing bitmap icons with SVGs, ensuring crisp scaling on high-DPI displays without pixelation.

## 4. Native Notifications

*   **Replacing Legacy Popups:** Replace Swing `JOptionPane` alerts with native Windows Action Center Toast Notifications for dispatch alerts.
*   **Implementation:** Utilize the AWT `SystemTray` and `TrayIcon.displayMessage()` if suitable, or write a lightweight JNA wrapper around the Windows Toast Notification COM API for richer notification support (actions, images).

## 5. Hardware Acceleration & Performance

*   **JVM Arguments:** Update `build.gradle` and/or launcher scripts to force hardware acceleration for rendering the Waterfall and Spectrum panels (`-Dsun.java2d.d3d=true` or OpenGL equivalents).
*   **Vectorization:** Where possible, utilize GPU offloading or the `jdk.incubator.vector` SIMD vectorization to support massive channel counts in the DSP pipelines.
