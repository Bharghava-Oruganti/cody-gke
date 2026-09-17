#!/bin/sh

echo "Hello World Pls Like ME : )"

FILES="/files"
SANDBOX="/sandbox"

VERDICT="$FILES/verdict.txt"
EXPECTED="$FILES/exp_output.txt"
COMPARE="$FILES/compare.txt"
REAL_OUTPUT="$FILES/real_output.txt"

# Clean previous files
mkdir -p "$FILES"
rm -f "$VERDICT" "$COMPARE" "$REAL_OUTPUT"

# Create sandbox
rm -rf "$SANDBOX"
mkdir -p "$SANDBOX"

# Copy source and input
cp "$FILES/code.cpp" "$SANDBOX/code.cpp"
cp "$FILES/input.txt" "$SANDBOX/input.txt"

# Protect files
chmod 700 "$FILES"

# Create restricted user with an absolute home directory
adduser -D -h "$SANDBOX" usr1

# Compile
g++ -o "$SANDBOX/code.out" "$SANDBOX/code.cpp"
stat=$?

# Compilation error
if [ $stat -ne 0 ]; then
    echo "Compilation Error" > "$VERDICT"
    echo "VERDICT: $(cat "$VERDICT")"
    exit 1
fi

# Give execution user access to sandbox
chown -R usr1:usr1 "$SANDBOX"
chmod -R 700 "$SANDBOX"

# Execute user program
cd "$SANDBOX"

timeout 3s su -s /bin/sh - usr1 -c \
    "cd $SANDBOX && ./code.out < input.txt > output.txt"

exit_status=$?

# Copy actual output
if [ -f "$SANDBOX/output.txt" ]; then
    cp "$SANDBOX/output.txt" "$REAL_OUTPUT"
else
    touch "$REAL_OUTPUT"
fi

# Determine verdict
if [ $exit_status -eq 124 ]; then

    echo "TLE" > "$VERDICT"

elif [ $exit_status -ne 0 ]; then

    echo "RTE" > "$VERDICT"

else

    diff --brief "$REAL_OUTPUT" "$EXPECTED" > "$COMPARE"

    if [ -s "$COMPARE" ]; then
        echo "WA" > "$VERDICT"
    else
        echo "AC" > "$VERDICT"
    fi

fi

echo "VERDICT: $(cat "$VERDICT")"