# Object-Oriented Programming — PRR Telecommunications Network Manager

A **telecommunications network management application** written in Java, built for the **Programação com Objetos** (Object-Oriented Programming) course at **Instituto Superior Técnico (IST)**, University of Lisbon, 2022/23.

The application (**PRR**) models a phone network: clients own terminals, terminals place and receive voice/text/video communications, tariff plans determine what each communication costs, clients move between loyalty levels based on their behaviour, and clients can be notified about terminals that become reachable again. The whole network can be saved to and reloaded from disk.

The project is a showcase of **object-oriented design**: the domain is decomposed into small, focused classes and coordinated through several of the classic *Gang of Four* design patterns rather than large conditional blocks. It compiles cleanly and passes all **118 provided public test cases**.

## Architecture

The codebase is split into three layers, each an independent, separately compiled module:

| Module | Role |
|--------|------|
| **`po-uilib`** | A small text-UI library (menus, forms, prompts) provided by the course. |
| **`prr-core`** | The domain model — all business logic, with no knowledge of the UI. |
| **`prr-app`** | The application layer — menus and commands that drive the core via the UI library. |

The strict `core` ↔ `app` separation is deliberate: the core is forbidden from importing anything from `pt.tecnico.uilib` or `prr.app`, so the entire domain could be reused behind a different interface (a GUI, a web service) without change. This is the *separation of concerns* the course is really assessing.

### Domain model (`prr-core`)

- **`Network` / `NetworkManager`** — the network holds all clients, terminals, and communications; the manager is a thin façade over it that also handles import and serialization.
- **`Client`** with loyalty levels **Normal / Gold / Platinum** — a client's level changes automatically as it uses the network.
- **`Terminal`** (abstract) → **`BasicTerminal`**, **`FancyTerminal`** — a fancy terminal supports video communications; a basic one does not.
- **`Communication`** (abstract) → **`Text`**, **`Voice`**, **`Video`** — each computes its own cost.
- **Tariff plans** — `NormalPlan`, `GoldPlan`, `PlatPlan`, all extending `BasicTariff`.
- **Notifications** — clients with notifications enabled are told when a terminal they tried to reach becomes free again.
- A family of custom **exceptions** for every domain error (unknown client/terminal, duplicate keys, unrecognized import entries, and so on).

## Design patterns used

This is the heart of what the project demonstrates:

- **State** — a `Terminal` is `Idle`, `Busy`, `Silent`, or `Off`, and a `Client` is `Normal`, `Gold`, or `Platinum`. Both are implemented as inner-class state hierarchies: an operation like "start a call" is delegated to the current state object, which decides what happens and transitions to the next state. This replaces sprawling `if (state == ...)` logic with polymorphism.
- **Strategy** — tariff plans (`BasicTariff` and subclasses) and notification delivery (`NotificationStrategy`) are pluggable algorithms, so cost calculation and notification behaviour can vary independently of the objects that use them.
- **Visitor** — `ClientVisitor`, `TerminalVisitor`, and `CommVisitor` (with a generic `Selector` predicate) let the app run queries and rendering over the domain without adding presentation code to the domain classes.
- **Template Method / inheritance & polymorphism** — abstract `Terminal` and `Communication` define the shared skeleton; concrete subclasses fill in the type-specific behaviour (e.g. `units()` and cost per communication type).
- **Command** — each menu action in `prr-app` is its own command object built on the UI library, keeping the menu structure declarative.
- **Façade** — `NetworkManager` gives the application a single, simple entry point into the domain.

### Persistence

Both the domain objects (via `Serializable`) and a plain-text import format are supported: networks can be saved to a `.dat` file and reopened, and initial data can be bulk-loaded from an import file.

## Requirements

- JDK (Java 11+; developed against the course toolchain, verified building on JDK 21)
- `make` (the project ships with Makefiles)

## Building

From the `project/` directory:

```bash
make          # builds prr-core then prr-app
make clean    # removes generated .class files and jars
```

Each module can also be built on its own; `prr-core` and `prr-app` each produce a jar (`prr-core.jar`, `prr-app.jar`) and depend on `po-uilib`.

## Running

The application entry point is `prr.app.App`. It reads menu input from standard input and can optionally bulk-load a network from an import file via the `import` system property:

```bash
# start empty
java -cp "prr-app/prr-app.jar:prr-core/prr-core.jar:../po-uilib/po-uilib.jar" prr.app.App

# start from an import file
java -Dimport=network.import -cp "<same classpath>" prr.app.App
```

Once running, the text menus let you manage clients and terminals, register communications, run queries (clients with/without debt, idle terminals, positive-balance terminals, all communications, …), view balances, and save/open the network state.

### Import file format

A plain-text file, one entity per line, pipe-separated. For example:

```
CLIENT|cli001|Manuel Pinheiro|103443
BASIC|969001|cli001|ON
FANCY|900001|cli001|OFF
```

(a client, then a basic and a fancy terminal belonging to it). The parser raises `UnrecognizedEntryException` on malformed lines.

## Testing

The `prr-tests-ef-daily-.../tests/` folder contains **118 matched test cases** as `.in` (menu input), `.out` (expected output), and optional `.import` (starting data) triples. Its `README` lists what each case exercises. To run them all:

```bash
CP="project/prr-app/src:project/prr-core/src:po-uilib/src"
cd project/../prr-tests-*/tests
for inf in *.in; do
  name="${inf%.in}"
  imp=""; [ -f "$name.import" ] && imp="-Dimport=$name.import"
  diff <(java -Dfile.encoding=UTF-8 $imp -cp "../../$CP" prr.app.App < "$inf") "$name.out" \
    >/dev/null && echo "PASS $name" || echo "FAIL $name"
done
```

All 118 pass.

## Repository layout

```
.
├── po-uilib/            # course-provided text-UI library
├── project/
│   ├── Makefile         # builds core + app
│   ├── prr-core/        # domain model (clients, terminals, communications,
│   │   └── src/prr/     #   tariffplan, notifications, visits, exceptions)
│   ├── prr-app/         # menus + command objects (the application layer)
│   │   └── src/prr/app/
│   └── uml/             # UML class diagrams (PDF) for each module
└── prr-tests-.../tests/ # 118 public test cases (.in / .out / .import)
```

The `uml/` folder contains UML class diagrams for `prr-core`, `prr-app`, and `po-uilib`, documenting the class relationships and the design patterns above.

## Notes

The assignment, menus, and identifiers are in Portuguese (it was a Portuguese-taught course); `search.py`-style provided code here is `po-uilib`, which was supplied by the course — the graded work is everything under `project/prr-core` and `project/prr-app`.

## Authors

Developed as a two-person group project for the course.
