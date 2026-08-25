#include "check.h"

#include <ctype.h>
#include <errno.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

void measurement_list_init(MeasurementList *list)
{
    list->items = NULL;
    list->count = 0;
    list->capacity = 0;
}

void measurement_list_free(MeasurementList *list)
{
    free(list->items);
    list->items = NULL;
    list->count = 0;
    list->capacity = 0;
}

int measurement_list_push(MeasurementList *list, double value, int line_number)
{
    if (list->count == list->capacity) {
        size_t next = list->capacity == 0 ? 16 : list->capacity * 2;
        Measurement *grown = realloc(list->items, next * sizeof(*grown));
        if (grown == NULL) {
            return -1;
        }
        list->items = grown;
        list->capacity = next;
    }

    list->items[list->count].value = value;
    list->items[list->count].line_number = line_number;
    list->items[list->count].in_spec = 0;
    list->count += 1;
    return 0;
}

static int starts_with(const char *text, const char *prefix)
{
    return strncmp(text, prefix, strlen(prefix)) == 0;
}

static int parse_double_arg(const char *label, const char *text, double *out)
{
    char *end = NULL;
    errno = 0;
    double value = strtod(text, &end);
    if (errno != 0 || end == text || *end != '\0') {
        fprintf(stderr, "error: invalid %s value '%s'\n", label, text);
        return -1;
    }
    *out = value;
    return 0;
}

void print_usage(const char *program)
{
    fprintf(stderr,
            "Usage: %s --min <lower> --max <upper> [--file <path>]\n"
            "\n"
            "Evaluate numeric inspection measurements against a tolerance window.\n"
            "Reads one measurement per line from --file, or from stdin if omitted.\n"
            "Blank lines and lines starting with '#' are ignored.\n"
            "\n"
            "Exit codes:\n"
            "  0  all measurements in specification\n"
            "  1  one or more out of specification (or no measurements)\n"
            "  2  usage / input error\n",
            program);
}

int parse_options(int argc, char **argv, CheckOptions *out)
{
    out->min = 0.0;
    out->max = 0.0;
    out->has_min = 0;
    out->has_max = 0;
    out->path = NULL;

    for (int i = 1; i < argc; ++i) {
        const char *arg = argv[i];

        if (strcmp(arg, "--help") == 0 || strcmp(arg, "-h") == 0) {
            print_usage(argv[0]);
            return 1;
        }

        if (strcmp(arg, "--min") == 0) {
            if (i + 1 >= argc) {
                fprintf(stderr, "error: --min requires a value\n");
                return -1;
            }
            if (parse_double_arg("min", argv[++i], &out->min) != 0) {
                return -1;
            }
            out->has_min = 1;
            continue;
        }

        if (starts_with(arg, "--min=")) {
            if (parse_double_arg("min", arg + 6, &out->min) != 0) {
                return -1;
            }
            out->has_min = 1;
            continue;
        }

        if (strcmp(arg, "--max") == 0) {
            if (i + 1 >= argc) {
                fprintf(stderr, "error: --max requires a value\n");
                return -1;
            }
            if (parse_double_arg("max", argv[++i], &out->max) != 0) {
                return -1;
            }
            out->has_max = 1;
            continue;
        }

        if (starts_with(arg, "--max=")) {
            if (parse_double_arg("max", arg + 6, &out->max) != 0) {
                return -1;
            }
            out->has_max = 1;
            continue;
        }

        if (strcmp(arg, "--file") == 0) {
            if (i + 1 >= argc) {
                fprintf(stderr, "error: --file requires a path\n");
                return -1;
            }
            out->path = argv[++i];
            continue;
        }

        if (starts_with(arg, "--file=")) {
            out->path = arg + 7;
            continue;
        }

        fprintf(stderr, "error: unknown argument '%s'\n", arg);
        return -1;
    }

    if (!out->has_min || !out->has_max) {
        fprintf(stderr, "error: both --min and --max are required\n");
        return -1;
    }

    if (out->min > out->max) {
        fprintf(stderr, "error: --min (%.6g) is greater than --max (%.6g)\n",
                out->min, out->max);
        return -1;
    }

    return 0;
}

static int is_blank_line(const char *line)
{
    for (const char *p = line; *p != '\0'; ++p) {
        if (!isspace((unsigned char)*p)) {
            return 0;
        }
    }
    return 1;
}

int load_measurements(FILE *in, MeasurementList *list)
{
    char line[512];
    int line_number = 0;

    while (fgets(line, sizeof(line), in) != NULL) {
        ++line_number;

        if (is_blank_line(line) || line[0] == '#') {
            continue;
        }

        char *end = NULL;
        errno = 0;
        double value = strtod(line, &end);
        if (errno != 0 || end == line) {
            fprintf(stderr, "error: invalid measurement on line %d\n", line_number);
            return -1;
        }

        while (*end != '\0' && isspace((unsigned char)*end)) {
            ++end;
        }
        if (*end != '\0' && *end != '#') {
            fprintf(stderr, "error: trailing junk on line %d\n", line_number);
            return -1;
        }

        if (measurement_list_push(list, value, line_number) != 0) {
            fprintf(stderr, "error: out of memory\n");
            return -1;
        }
    }

    if (ferror(in)) {
        fprintf(stderr, "error: failed while reading measurements\n");
        return -1;
    }

    return 0;
}

void evaluate_measurements(MeasurementList *list, double min, double max)
{
    for (size_t i = 0; i < list->count; ++i) {
        double value = list->items[i].value;
        list->items[i].in_spec = (value >= min && value <= max) ? 1 : 0;
    }
}

void print_report(const MeasurementList *list, double min, double max)
{
    size_t pass_count = 0;
    size_t fail_count = 0;

    printf("Inspection tolerance check\n");
    printf("Spec window : [%.6g, %.6g]\n", min, max);
    printf("----------------------------------------\n");
    printf("%-8s %-14s %s\n", "Line", "Value", "Result");
    printf("----------------------------------------\n");

    for (size_t i = 0; i < list->count; ++i) {
        const Measurement *m = &list->items[i];
        const char *result = m->in_spec ? "PASS" : "FAIL";
        if (m->in_spec) {
            ++pass_count;
        } else {
            ++fail_count;
        }
        printf("%-8d %-14.6g %s\n", m->line_number, m->value, result);
    }

    printf("----------------------------------------\n");
    printf("Total: %zu  PASS: %zu  FAIL: %zu\n",
           list->count, pass_count, fail_count);
}

int summary_exit_code(const MeasurementList *list)
{
    if (list->count == 0) {
        return 1;
    }

    for (size_t i = 0; i < list->count; ++i) {
        if (!list->items[i].in_spec) {
            return 1;
        }
    }

    return 0;
}
