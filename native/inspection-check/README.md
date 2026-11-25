# inspection-check

Small **C11** CLI that evaluates numeric inspection measurements against a min/max tolerance window. Useful as a shop-floor or scripting companion to the Java `qms-inspection-service` API.

## Build

Requires `gcc` or `clang` (C11).

```bash
make
make test
```

On Windows with MinGW/MSYS2:

```bat
build.bat
build.bat test
```

## Usage

```bash
./inspection-check --min 0.48 --max 0.52 --file samples/measures.txt
```

Or pipe values:

```bash
printf "0.50\n0.51\n0.47\n" | ./inspection-check --min 0.48 --max 0.52
```

Blank lines and `#` comments are ignored. Leading whitespace before a value or comment is allowed.

## Parser rules

- `--min` / `--max` must be finite numbers and `min <= max`
- Measurement lines must parse as a single finite number (optional trailing `# comment`)
- Lines longer than the internal buffer are rejected (exit `2`)
- Non-finite tokens such as `nan` / `inf` are rejected

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
tests/test_check.c  unit coverage
samples/          example measurement files
```
