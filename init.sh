#!/bin/bash

temp=$(kubectl get services demo -o=jsonpath='{.spec.clusterIP}')

sed -i "s/clusterip/$temp/" configmap.yaml
kubectl apply -f configmap.yaml