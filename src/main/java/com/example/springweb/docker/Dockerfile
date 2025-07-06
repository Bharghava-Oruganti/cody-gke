FROM alpine:latest
RUN apk add --no-cache g++

COPY ./run.sh ./
RUN chmod +x run.sh
CMD ["sh","./run.sh"]
