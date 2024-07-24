# RSocket Usage Guide to AWARDS MICRO SERVICE

## Introduction to OUR MICROSERVICE
Our microservice is designed to handle AWARDS for movies.
It provides a RESTful API that allows other applications to interact with it, such as an Actors Microservice.
The microservice is responsible for storing, retrieving, and managing awards. It also handles tasks such as moderation and spam filtering.




## Introduction to RSocket

RSocket is a binary protocol for use on byte stream transports such as TCP, WebSockets, and Aeron. It enables the following symmetric interaction models via async message passing over a single connection:
- Request/Response (streaming and non-streaming-not at this service)
- Fire-and-Forget
- Request/Stream
- Channel (bi-directional streams)

RSocket supports session resumption, multiplexing, and application-layer flow control.

## Code Snippets

Here OUR  some example RSocket commands. You can copy these commands from the clipboard and use them as needed.

### GET AWARDS CHANNEL &&CREATE Review Request STREAM &DELETE FNF

```bash
    java -jar rsc-0.9.1.jar --stream --route=post-award --data="{\"id\":\"string\",\"type\":\"typecast\",\"winners\":[\"maddona\",\"jlo\"],\"movie\":\"mov\",\"organization\":\"org\",\"country\":\"IL\",\"date\":\"03-03-2020\",\"deatils\":{\"additionalProp1\":{},\"additionalProp2\":{},\"additionalProp3\":{}}}" --debug tcp://localhost:7004

    java -jar rsc-0.9.1.jar --fnf --route=delete-award-by-id --data="\"4d2c9bda-712e-4faf-b4f2-91341c5f7333\"" --debug tcp://localhost:7004


java -jar rsc-0.9.1.jar --channel --route=get-channel-award --data=- --debug tcp://localhost:7004

```
##  At CHANNEL METHOD 
data is Flux<IdWrapperBoundary>  This boundary has 2 fields-
id- which Criteria at Get Method
criteria-which Value at Get Method


### ELK

# Pull the Docker images
docker pull elasticsearch:8.12.0
docker pull logstash:8.12.0
docker pull kibana:8.12.0

# Run containers

```bash
docker run -d --name elasticsearch -p 9200:9200 -e "discovery.type=single-node" elasticsearch:8.12.0
docker run -d --name logstash -p 5044:5044 logstash:8.12.0
docker run -d --name kibana -p 5601:5601 kibana:8.12.0
```

# Configuration for Spring Project
Add the following to your application.properties or application.yml:

spring.elasticsearch.uris=http://localhost:9200
spring.elasticsearch.username=elastic
spring.elasticsearch.password=yourpassword



If you need any further adjustments, let me know!


```
##  Description



