#!/bin/sh
APP_VERSION=1.0.0
REPO_DOMAIN=xxxxxxxxxxxx.dkr.ecr.ap-southeast-1.amazonaws.com
            
cd ../ &&
mvn clean package -Dmaven.test.skip &&
mv fraud-detection-service/target/fraud-detection-service-$APP_VERSION.jar docker/fraud-detection-service/ &&
mv trans-generator/target/trans-generator-$APP_VERSION.jar docker/trans-generator/ &&
aws ecr get-login-password --region ap-southeast-1 | docker login --username AWS --password-stdin $REPO_DOMAIN &&
cd docker/fraud-detection-service &&
docker build --build-arg APP_VERSION=$APP_VERSION -t $REPO_DOMAIN/example/fraud-detection-service:$APP_VERSION . && 
docker push $REPO_DOMAIN/example/fraud-detection-service:$APP_VERSION &&
cd ../trans-generator &&
docker build --build-arg APP_VERSION=$APP_VERSION -t $REPO_DOMAIN/example/trans-generator:$APP_VERSION . &&
docker push $REPO_DOMAIN/example/trans-generator:$APP_VERSION 
