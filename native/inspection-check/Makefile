CC      ?= gcc
CFLAGS  ?= -std=c11 -Wall -Wextra -Wpedantic -O2
CPPFLAGS = -Iinclude
LDFLAGS ?=
TARGET   = inspection-check

SRCS = src/main.c src/check.c
OBJS = $(SRCS:.c=.o)

.PHONY: all clean sample

all: $(TARGET)

$(TARGET): $(OBJS)
	$(CC) $(CFLAGS) -o $@ $(OBJS) $(LDFLAGS)

src/%.o: src/%.c include/check.h
	$(CC) $(CFLAGS) $(CPPFLAGS) -c $< -o $@

sample: $(TARGET)
	./$(TARGET) --min 0.48 --max 0.52 --file samples/measures.txt

clean:
	rm -f $(OBJS) $(TARGET) $(TARGET).exe
