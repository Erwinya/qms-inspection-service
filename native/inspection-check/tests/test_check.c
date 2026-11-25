#include "check.h"

#include <math.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

static int failures = 0;

static void expect_true(int cond, const char *msg)
{
    if (!cond) {
        fprintf(stderr, "FAIL: %s\n", msg);
        failures += 1;
    } else {
        printf("ok  %s\n", msg);
    }
}

static FILE *tmp_from_text(const char *text)
{
    FILE *f = tmpfile();
    if (f == NULL) {
        perror("tmpfile");
        exit(1);
    }
    fputs(text, f);
    rewind(f);
    return f;
}

static void test_parse_options_ok(void)
{
    char *argv[] = {"inspection-check", "--min", "0.1", "--max=0.9", "--file", "x.txt"};
    CheckOptions opts;
    int rc = parse_options(6, argv, &opts);
    expect_true(rc == 0, "parse_options accepts min/max/file");
    expect_true(opts.min == 0.1 && opts.max == 0.9, "parse_options stores bounds");
    expect_true(opts.path != NULL && strcmp(opts.path, "x.txt") == 0, "parse_options stores path");
}

static void test_parse_options_rejects_nan(void)
{
    char *argv[] = {"inspection-check", "--min", "nan", "--max", "1"};
    CheckOptions opts;
    int rc = parse_options(5, argv, &opts);
    expect_true(rc == -1, "parse_options rejects nan min");
}

static void test_load_and_evaluate(void)
{
    FILE *in = tmp_from_text("# c\n\n  0.50  # ok\n0.40\n0.60\n");
    MeasurementList list;
    measurement_list_init(&list);
    expect_true(load_measurements(in, &list) == 0, "load_measurements accepts comments/whitespace");
    fclose(in);
    expect_true(list.count == 3, "load_measurements keeps three values");
    evaluate_measurements(&list, 0.48, 0.52);
    expect_true(list.items[0].in_spec == 1, "in-spec measurement passes");
    expect_true(list.items[1].in_spec == 0, "below-min measurement fails");
    expect_true(list.items[2].in_spec == 0, "above-max measurement fails");
    expect_true(summary_exit_code(&list) == 1, "summary_exit_code fails when any out of spec");
    measurement_list_free(&list);
}

static void test_reject_non_finite_and_long_line(void)
{
    FILE *bad = tmp_from_text("inf\n");
    MeasurementList list;
    measurement_list_init(&list);
    expect_true(load_measurements(bad, &list) == -1, "load_measurements rejects infinity");
    measurement_list_free(&list);
    fclose(bad);

    char long_line[600];
    memset(long_line, '1', sizeof(long_line) - 2);
    long_line[sizeof(long_line) - 2] = '\n';
    long_line[sizeof(long_line) - 1] = '\0';
    FILE *over = tmp_from_text(long_line);
    measurement_list_init(&list);
    expect_true(load_measurements(over, &list) == -1, "load_measurements rejects overlong lines");
    measurement_list_free(&list);
    fclose(over);
}

static void test_empty_input_exit(void)
{
    MeasurementList list;
    measurement_list_init(&list);
    expect_true(summary_exit_code(&list) == 1, "empty list exits 1");
    measurement_list_free(&list);
}

int main(void)
{
    test_parse_options_ok();
    test_parse_options_rejects_nan();
    test_load_and_evaluate();
    test_reject_non_finite_and_long_line();
    test_empty_input_exit();
    if (failures != 0) {
        fprintf(stderr, "%d failed\n", failures);
        return 1;
    }
    printf("all tests passed\n");
    return 0;
}
