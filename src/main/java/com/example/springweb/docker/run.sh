#!/bin/sh

# exectable
echo "Hello World Pls Like ME : )"
verdict_path="./files/verdict.txt"
output_path="./sandbox/output.txt"
compare_path="./files/compare.txt"
expected_output_path="./files/exp_output.txt"

truncate -s 0 $verdict_path $compare_path

mkdir sandbox
cp ./files/code.cpp ./sandbox/code.cpp
cp ./files/input.txt ./sandbox/input.txt

# create secret key
SECRET_KEY=$(tr -dc '[:alnum:]' < /dev/urandom | head -c 10)

chown root ./files
chmod 700 -R ./files

adduser -D -h ./sandbox usr1

# compilation command
g++ -o ./sandbox/code.out ./sandbox/code.cpp
stat=$?

chown -R usr1:usr1 ./sandbox
chmod 700 -R ./sandbox

if [ $stat -eq 1 ]; then
    echo "Compilation Error" >> $verdict_path
    chown -R usr1:usr1 ./files
    exit 1
fi

SECRET_KEY=$(tr -dc '[:alnum:]' < /dev/urandom | head -c 10)

(
    timeout 3s su - usr1 -c "./code.out < input.txt > output.txt"
)

exit_status=$?

chown -R usr1:usr1 ./files
cp ./sandbox/output.txt ./files/real_output.txt

if [ $exit_status -eq 124 ]; then
    echo "TLE" >> $verdict_path
elif [ $exit_status -ne 0 ]; then
    echo "RTE" >> $verdict_path
else
    if [ ! -f $output_path ]; then
      touch $output_path
    fi

    diff --brief $output_path $expected_output_path > $compare_path
    cat $compare_path

    if [ -s $compare_path ]; then
        echo "WA" >> $verdict_path
    else
        echo "AC" >> $verdict_path
    fi
fi
