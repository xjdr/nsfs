NSFS
====

Neural Scale FaaS


(Currently only supports Java)

Post a K,V of (Endpoint, FunctionBody) to the <server>/addRoute endpoint, 
and your function will be served for you.

Your function needs to have the signature Function<HttpRequest, HttpResponse> 
in order to correctly work. 
