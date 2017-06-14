NSFS
====

Neural Scale FaaS


(Currently only supports Java)

Post a K,V of (Endpoint, FunctionBody) to the <server>/addRoute endpoint, 
and your function will be served for you.

Your function needs to have the signature Function<HttpRequest, HttpResponse> 
in order to correctly work. 

to test:

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
    
    } ' -vvvv
 $> OK
```

and this calls it:
```bash
$> curl -k https://localhost:8080/hello
$> ok

```
