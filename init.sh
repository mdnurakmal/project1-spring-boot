#!/bin/bash

sed -i "" "/^\([[:space:]]*spring.kafka.consumer.bootstrap-servers = \).*/s//\1$1/" $2