#!/bin/bash
#
# This script pushes the Eclipse preference files in this directory 
# to all Eclipse projects it can find in the parent directory.
#
for dir in $(find ../../org.atomictagging.*/.settings/ -type d); do
	echo "Removing preference files from $dir"
	rm ${dir}/*
	echo "Pushing new preference files to $dir"
	cp *.prefs $dir
done
