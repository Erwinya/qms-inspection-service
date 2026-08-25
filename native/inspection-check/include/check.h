#ifndef INSPECTION_CHECK_H
#define INSPECTION_CHECK_H

#include <stddef.h>
#include <stdio.h>

typedef struct {
    double value;
    int line_number;
    int in_spec;
} Measurement;

typedef struct {
    Measurement *items;
    size_t count;
    size_t capacity;
} MeasurementList;

typedef struct {
    double min;
    double max;
    int has_min;
    int has_max;
    const char *path; /* NULL means stdin */
} CheckOptions;

void measurement_list_init(MeasurementList *list);
void measurement_list_free(MeasurementList *list);
int measurement_list_push(MeasurementList *list, double value, int line_number);

int parse_options(int argc, char **argv, CheckOptions *out);
int load_measurements(FILE *in, MeasurementList *list);
void evaluate_measurements(MeasurementList *list, double min, double max);
void print_report(const MeasurementList *list, double min, double max);
int summary_exit_code(const MeasurementList *list);

void print_usage(const char *program);

#endif /* INSPECTION_CHECK_H */
