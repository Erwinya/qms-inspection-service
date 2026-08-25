#include "check.h"

#include <stdio.h>
#include <stdlib.h>

int main(int argc, char **argv)
{
    CheckOptions options;
    int parse_result = parse_options(argc, argv, &options);
    if (parse_result == 1) {
        return 0; /* --help */
    }
    if (parse_result != 0) {
        print_usage(argv[0]);
        return 2;
    }

    FILE *in = stdin;
    if (options.path != NULL) {
        in = fopen(options.path, "r");
        if (in == NULL) {
            fprintf(stderr, "error: cannot open file '%s'\n", options.path);
            return 2;
        }
    }

    MeasurementList list;
    measurement_list_init(&list);

    if (load_measurements(in, &list) != 0) {
        if (in != stdin) {
            fclose(in);
        }
        measurement_list_free(&list);
        return 2;
    }

    if (in != stdin) {
        fclose(in);
    }

    if (list.count == 0) {
        fprintf(stderr, "error: no measurements found\n");
        measurement_list_free(&list);
        return 1;
    }

    evaluate_measurements(&list, options.min, options.max);
    print_report(&list, options.min, options.max);

    int code = summary_exit_code(&list);
    measurement_list_free(&list);
    return code;
}
