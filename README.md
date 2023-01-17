# Spark Java Web Crawler
Web Crawler on a Spark Java Micro Framework - A Docker Microservice Example

Full documentation of the framework: http://sparkjava.com/

Some basics first. Maven starts a webserver called Jetty. It will also compile your java code for execution behind Jetty. Any definitions of functions (get/post/put/delete/head etc..) in your Main() function will be mapped as callbacks and executed every time a particular path (/hello, /users/:name, etc.) are accessed in the Jetty server. For example: 

get("/hello" (req, res) -> { return "Hello World!"; });

if you have the code above defined in the body of your Main() function, every time there is a request to "/hello" path on your application (Jetty server) it will call this get funcion, provide it with access to the incoming http request through the "req" and give you access to manipulate the response message through "res". It will then execute whatever you have defined in the body of the function and will then send the http response. Refer to Spark Java for complete documentation.


Dependencies of this project are listed in the pom.xml file, and you will need the following tools installed to run the crawler:
1) Java
2) Apache Maven
3) Docker


Use the following commands to a) build, and b) to run this service.

 #### $ sudo docker build . -t alixus-crawler
 #### $ sudo docker run -it -e BASE_URL=<HTTP_SERVER_ADDRESS> -p 4567:4567 --rm alixus-crawler

The above command will a) build the server, and b) run it in a docker container on port 4567. The crawler will be executing search queries on the address specified in BASE_URL. Use IP address or FQDN, do not use "localhost".

Also add the following command line arguments to the docker run command:
1) -e MAX_THREADS=<NUM>
2) -e MAX_RESULTS=<NUM>
3) -e PRETTY_JAY=<NUM>
4) -v "$HOME/.m2":/root/.m2

MAX_THREADS specifies the number of working crawler threads. MAX_RESULTS will limit the number of pages to crawl. PRETTY_JAY will enable the pretty formatting of the json responses. The last command line argument, -v, will tell Maven to use cached libraries, instead of downloading full packages during every build. Also, you can set the environment variables in the Dockerfile such:

ENV <<variable_name>> <<variable_value>>

Once the crawler is started, it can be reached on a port 4567. The following is a POST request to the path "/crawl", json body provides keyword to search for:

-----------------------------------------
POST /crawl HTTP/1.1  
Host: localhost:4567  
Content-Type: application/json  
Content-Length: 20  
  
  {"keyword": "http"}    
  
  
HTTP/1.1 200 OK  
Date: Wed, 04 Jan 2023 17:24:18 GMT  
Content-Type: text/html;charset=utf-8  
Transfer-Encoding: chunked  
Server: Jetty(9.4.30.v20200611)  
  
  {"id":"wdmwIyMi"}    
 
 
------------------------------------------

The request/response exchange above results in a search ID "wdmwIyMi". Using this ID you can query the same Jetty server on the GET("/crawler/:id") mapping, as follows:  
  
------------------------------------------  
GET /crawl/wdmwIyMi HTTP/1.1  
HOSt: localhost  
  
HTTP/1.1 200 OK  
Date: Wed, 04 Jan 2023 18:12:13 GMT  
Content-Type: text/html;charset=utf-8  
Content-Length: 81160  
Server: Jetty(9.4.30.v20200611)  
  
 
 
  {    
  "id": "wdmwIyMi",  
  "status": "done",  
  "urls": [  
    "http://192.168.4.102//",  
    "http://192.168.4.102/index.html",  
    "http://192.168.4.102/manpageindex.html",  
    "http://192.168.4.102/index1.html",  
    "http://192.168.4.102/index2.html",  
    "http://192.168.4.102/index3.html",  
    "http://192.168.4.102/index4.html",  
    "http://192.168.4.102/index5.html",  
    "http://192.168.4.102/index7.html",  
    "http://192.168.4.102/index8.html",  
    "http://192.168.4.102/gfdl.html"  
    ...  
    ....  
    [this will be the number of pages returned depending on your MAX_RESULTS variable]  
    ......  
  ]  
  }    
 
 
------------------------------------------
