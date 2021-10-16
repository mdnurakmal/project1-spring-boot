#!/bin/bash

echo "hello"
#temp=$(kubectl get services demo -o=jsonpath='{.spec.clusterIP}')
#echo "running" $temp
#sed -i "s/clusterip/$temp/" configmap.yaml
#kubectl apply -f configmap.yaml