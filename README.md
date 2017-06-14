NSFS
====

Neural Scale FaaS


(Currently only supports Java)

POST a JSON body structured as follows'route: "{route}", "functionBody": "request -> { <function body>; return response; }; ";
to the https://<ipAddress>:<port>/addFunction and your function will be served.

Your function will be executed in a sandbox that will have several libraries and helper functionsavailable to you.

The functions leverage the Netty framework and you will have access to the full http stack in netty. https://netty.io
We have provided Moshi for working with JSON https://github.com/square/moshi
We have provided the full Amazon SDK for working with AWS
We have provided Jedis for working with redis
We have provided a kafka client and a cassandra client

Your function needs to have the signature Function<HttpRequest, HttpResponse> 
in order to correctly work.

## An Example 

this loads a function
```bash
 $> curl -k https://localhost:8080/addFunction \
    -X POST -d '{"route": "/hello", 
    "functionBody": "request -> { 
      String respMsg = \"OK\"; 
    
      FullHttpResponse response = new DefaultFullHttpResponse(
      HttpVersion.HTTP_1_1, 
      HttpResponseStatus.OK, 
      Unpooled.copiedBuffer(respMsg.getBytes())); 
    
      response.headers().set(CONTENT_TYPE, \"text\/plain\"); 
      response.headers().setInt(CONTENT_LENGTH, respMsg.length());  
    
      return response; } " 
    
    } ' -vvv
 $> OK
```

## and to call the function:
```bash
$> cur l -k https://localhost:8080/hello
$> ok

```

## Auth
<Add Section on Auth>

## Triggering Events
 - Http req
 - Hashed Wheel Timer
 - Cloudtrail
< Give examples of each >

## To Start the server 
./bin/serverStart.sh <port> <log_level>

## Docker
A Docker container is available for both local development and running at scale

## Scale out 
 - DHT hashed on route leveraging Trailhead
 - < N > servers that access routes and functions via a K,V store. Route is K | class ByteCode is V. 
