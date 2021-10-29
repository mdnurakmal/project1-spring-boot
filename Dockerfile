FROM docker.io/library/chat:0.0.1-SNAPSHOT

ARG FIRESTORE_SA

RUN echo $FIRESTORE_SA

RUN mkdir -p /usr/local/firestore
RUN chown newuser /usr/local/firestore
USER newuser
WORKDIR /usr/local/firestore
RUN cat $FIRESTORE_SA >> /usr/local/firestore/key.json
ENV GOOGLE_APPLICATION_CREDENTIALS /usr/local/firestore/key.json