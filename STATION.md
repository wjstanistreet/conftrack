# ConfTrack — every station fixed

This branch is the finished application: every station's bug found and fixed. Each station's
write-up is in `docs/stations/`. It covers what was wrong, why it mattered, the tool that found
it, and the fix.

> The free tier tells you what your code says. The subscription tells you what your application
> is actually doing.

## The stations

| # | Block | Station | Tool | Branch |
|---|---|---|---|---|
| 00 | — | [Open the project](docs/stations/STATION-00.md) | Search Everywhere, Spring tool window, Endpoints | `main` |
| 01 | A | [It won't start](docs/stations/STATION-01.md) | Spring bean inspections, gutter icons, beans diagram | `solution/station-01` |
| 02 | A | [Nobody knows the key names](docs/stations/STATION-02.md) | Spring configuration completion | `solution/station-02`, `solution/block-a` |
| 03 | B | [The schema doesn't match](docs/stations/STATION-03.md) | Database tools, schema-aware inspections | `solution/station-03`, `solution/block-b` |
| 04 | C | [The endpoint 500s, and writes need a login](docs/stations/STATION-04.md) | Endpoints window, HTTP Client | `solution/station-04` |
| 05 | C | [The right code, the wrong bean](docs/stations/STATION-05.md) | Spring Debugger | `solution/station-05`, `solution/block-c` |
| 06 | D | [The dashboard takes seconds](docs/stations/STATION-06.md) | IntelliJ Profiler | `solution/station-06` |
| 07 | D | [The dependency is vulnerable](docs/stations/STATION-07.md) | Package Checker | `solution/station-07`, `solution/block-d` |
| 08 | E | [The page says "undefined"](docs/stations/STATION-08.md) | JavaScript/TypeScript support, Code With Me | `solution/station-08`, `solution/block-e` |
| 09 | F | [The new API isn't live yet](docs/stations/STATION-09.md) | OpenAPI, WireMock, HTTP Client | `solution/station-09`, `solution/block-f`, `solution/complete` |

## Shortcuts worth keeping

Windows and Linux first, macOS in brackets.

| Shortcut | What it does | Station |
|---|---|---|
| Double Shift | Search Everywhere | 00 |
| Click a bean gutter icon | Jump to what is injected | 01 |
| Ctrl+Space (⌃Space) | Completion, including in YAML | 02 |
| Ctrl+click (⌘-click) | Go to a declaration, e.g. from a config key to its field | 02 |
| Ctrl+Enter (⌘⏎) | Run a statement in the SQL console | 03 |
| Alt+Enter (⌥⏎) | Fix or generate: a qualifier, a request from an endpoint, a missing env variable, a request from a stub | 01, 04, 09 |
| Alt+F8 (⌥F8) | Evaluate an expression against the running application | 05 |
| Run ▸ Profile, or attach | Profile the running application | 06 |
| Shift+F6 (⇧F6) | Rename something and every usage of it | 08 |

## Try it on your own projects

- Open a Spring project and look at the **Endpoints** window and the **Spring** tool window
  before reading any code.
- Keep your API checks in `.http` files next to the code, with secrets in the private environment
  file.
- Profile before you optimise. The slow part is rarely where you would guess.
- When an API you depend on is not ready, stub it from its spec with WireMock instead of waiting.
