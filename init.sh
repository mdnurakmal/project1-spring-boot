#!/bin/bash

sed -ri 's/^(\s*)(image\s*:\s*nginx\s*$)/\1image: $1/' $2