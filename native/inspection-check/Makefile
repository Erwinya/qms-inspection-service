CC      ?= gcc
CFLAGS  ?= -std=c11 -Wall -Wextra -Wpedantic -O2
CPPFLAGS = -Iinclude
LDFLAGS ?= -lm
TARGET   = inspection-check
TESTBIN = tests/test_check

SRCS = src/main.c src/check.c
OBJS = $(SRCS:.c=.o)
LIB_SRCS = src/check.c

.PHONY: all clean sample test

all: $(TARGET)

$(TARGET): $(OBJS)
	$(CC) $(CFLAGS) -o $@ $(OBJS) $(LDFLAGS)

src/%.o: src/%.c include/check.h
	$(CC) $(CFLAGS) $(CPPFLAGS) -c $< -o $@

$(TESTBIN): tests/test_check.c $(LIB_SRCS) include/check.h
	$(CC) $(CFLAGS) $(CPPFLAGS) -o $@ tests/test_check.c $(LIB_SRCS) $(LDFLAGS)

test: $(TESTBIN) $(TARGET)
	./$(TESTBIN)
	./$(TARGET) --min 0.48 --max 0.52 --file samples/measures.txt

sample: $(TARGET)
	./$(TARGET) --min 0.48 --max 0.52 --file samples/measures.txt

clean:
	rm -f $(OBJS) $(TARGET) $(TARGET).exe $(TESTBIN) $(TESTBIN).exe
