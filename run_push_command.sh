git add .
git commit -m "Update"
git push
aws ecr get-login-password --region ap-southeast-2 | docker login --username AWS --password-stdin 761018889743.dkr.ecr.ap-southeast-2.amazonaws.com
docker build -t user-microservice .
docker tag user-microservice:latest 761018889743.dkr.ecr.ap-southeast-2.amazonaws.com/user-microservice:latest
docker push 761018889743.dkr.ecr.ap-southeast-2.amazonaws.com/user-microservice:latest
kubectl delete deployment user-service-deployment
kubectl apply -f deployment.yaml