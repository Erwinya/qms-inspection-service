# inspection-check

Small **C11** CLI that evaluates numeric inspection measurements against a min/max tolerance window. Useful as a shop-floor or scripting companion to the Java `qms-inspection-service` API.

## Build

Requires `gcc` or `clang` (C11).

```bash
make
```

On Windows with MinGW/MSYS2:

```bat
build.bat
```

## Usage

```bash
./inspection-check --min 0.48 --max 0.52 --file samples/measures.txt
```

Or pipe values:

```bash
printf "0.50\n0.51\n0.47\n" | ./inspection-check --min 0.48 --max 0.52
```

## Exit codes

| Code | Meaning |
|------|---------|
| 0 | All measurements in specification |
| 1 | One or more out of specification (or empty input) |
| 2 | Usage / parse / I/O error |

## Layout

```text
include/check.h   public API
src/main.c        CLI entry
src/check.c       parsing, evaluation, report
samples/          example measurement files
```
