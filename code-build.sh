#!/bin/bash

aws codebuild create-project \
    --name user-service-build \
    --source type=GITHUB,location=https://github.com/English-Learning-Application/english-learning-back-user.git \
    --artifacts type=NO_ARTIFACTS \
    --environment type=LINUX_CONTAINER,computeType=BUILD_GENERAL1_SMALL,image=aws/codebuild/standard:5.0 \
    --service-role arn:aws:iam::761018889743:role/CodeBuildServiceRole
