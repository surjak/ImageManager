#!/bin/sh
docker login
docker build --tag=aghimagemanager/image_manager:load-balancer .
docker push aghimagemanager/image_manager:load-balancer
docker logout
echo