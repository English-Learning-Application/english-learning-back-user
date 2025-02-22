docker build -t user-microservice .
minikube image load user-microservice:latest
kubectl delete secret user-service-secret
kubectl create secret generic user-service-secret --from-env-file=local.env
kubectl delete deployment user-service-deployment
kubectl apply -f local-deployment.yaml