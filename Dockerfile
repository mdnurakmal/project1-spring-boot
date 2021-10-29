FROM docker.io/library/chat:0.0.1-SNAPSHOT

ARG FIRESTORE_SA

RUN echo $FIRESTORE_SA

USER root
RUN mkdir -p /firestore
#RUN chown newuser /firestore

#USER newuser
WORKDIR /firestore
RUN cat $FIRESTORE_SA > key.json
ENV GOOGLE_APPLICATION_CREDENTIALS /firestore/key.json