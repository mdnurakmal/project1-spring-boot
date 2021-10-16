#!/bin/bash

clusterip=$(kubectl get services demo -o=jsonpath='{.spec.clusterIP}')

sed -i "s/clusterip/$clusterip/" configmap.yaml
kubectl apply -f configmap.yaml