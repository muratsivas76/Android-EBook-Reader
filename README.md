# Old-School Zero-Allocation Text Layout & Pagination Engine

Developed initially over 11 years ago by **Murat (Born in 1976, Sivas, Türkiye)**, this project stands as a testament to low-level software craftsmanship. Originally forged in the constraints of Nokia Java J2ME using bare `xterm` terminals and raw `javac` compilation (without the luxury of modern IDEs), this engine has been painstakingly migrated and optimized for Android (API Level 29 and above, fully Java 6-8 backwards compatible).

This repository contains hundreds of source files forming a highly cohesive, robust pagination and formatting stack. **Fair warning: This is not a modern "plug-and-play" library for the new generation of developers who rely on bloated external dependencies.** It is a steep, raw, and heavy architecture meant explicitly for old-school students of computer science and developers who value micro-optimization, deterministic memory footprints, and byte-level precision.

---

## 🏛️ The Engineering Manifesto

Modern software has grown obese. A simple text or note application today routinely consumes 50MB+ of disk space and hundreds of megabytes of RAM due to lazy "WebView/Electron" abstractions or layered, unused libraries. 

This engine operates on a radically different philosophy:
* **The 100KB Crown:** The entire rich text layout framework is packed into a microscopic footprint, outperforming commercial engines in speed, rendering fluidity, and battery efficiency.
* **Strict Zero-Garbage Policy:** Dynamic string allocations (like `substring()`, `split()`, or `replaceAll()`) are completely banned inside core rendering loop streams. By leveraging low-level routines such as `regionMatches()` and direct primitive character arrays, the Garbage Collector is never awoken. The RAM footprint remains a flat, unyielding line even during rapid pagination on a 1000-page manuscript.
* **Deterministic Integer Caching:** Every constant marker—down to the numerical literal zero (`0`)—is structured to bypass structural heap inflation, reflecting historical J2ME performance practices where every single byte fought for its life in kilobytes of memory.

---

## 🛠️ Feature Matrix

This framework includes an automated, server-side / desktop compiler framework (`ToMDK`) paired with a runtime Android rendering matrix wrapper (`renderAlign`):

1. **Multi-State Stream Layout Parsing:** On-the-fly execution of `<b>`, `<i>`, `<u>`, and `<font color="...">` styling blocks.
2. **Curshungeçirmez (Bulletproof) Severed-Tag Merging:** Safely intercepts and heals split HTML attributes broken midway at precise page boundaries or line endings. Leftover terminal syntax structures are swallowed invisibly without polluting the view canvas.
3. **Dual-Safety Justification (`align` Engine):** Pixel-perfect text centering and two-yana-yaslama (justification) that mathematically processes word spaces without ever corrupting whitespace alignments bounded inside markup structures.
4. **Sonsuz Hex Color Compiler:** An allocation-free, bit-shifting hexadecimal parser (`<< 4 | digit`) capable of compiling millions of dynamic web colors safely into ARGB primitives.
5. **Zero-Overhead Exporters:** Streaming extraction loops (`copyContentTXT` and `copyContentHTML`) that process structural markup into raw text outputs without memory pipeline choking.

---

## 👨‍💻 Target Audience & Legacy

This software is released under the **GPL v3 License** to ensure its architectural freedom remains absolute. 

If you are looking for an intuitive, drag-and-drop, automated UI component, **look away.** This project requires deep integration, a clear understanding of graphic coordinate baselines, canvas translations, and state-machine compilation mechanics. It is built by an old-school zanaatkar (artisan) for old-school engineers who still love the smell of raw source files and pure terminal execution.

*Crafted with pride, sweat, and absolute bit-level precision in Sivas.*
