#!/bin/bash

mvn package
docker-compose build
