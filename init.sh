#!/bin/bash
set -x #echo onnano

echo "hello"
temp=$(kubectl get services kafka -o=jsonpath='{.spec.clusterIP}')
echo "running" $temp
sed -i "s/clusterip/$temp/" ./configmap.yaml
kubectl apply -f ./configmap.yaml
kubectl apply -f ./deployment.yaml