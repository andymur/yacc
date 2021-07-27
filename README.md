# Task

For every incoming request as described in [1], send out bid requests as
described in [2] to a configurable number of bidders [5]. Responses from these
bidders as described in [3] must be processed. The highest bidder wins, and
payload is sent out as described in [4].

Incoming and outgoing communication is to be done over HTTP. Message formats
are described below.

[1]: #1-incoming-requests
[2]: #2-bid-requests
[3]: #3-bid-response
[4]: #4-auction-response

### 1.1 Incoming Requests

The application must listen to incoming HTTP requests on port 8080.

An incoming request is of the following format:

    http://localhost:8080/[id]?[key=value,...]

The URL will contain an ID to identify the ad for the auction, and a number of
query-parameters.

### 1.2 Bid Requests

The application must forward incoming bid requests by sending a corresponding
HTTP POST request to each of the configured bidders with the body in the
following JSON format:

```json
{
	“id”: $id,
	“attributes” : {
		“$key”: “$value”,
		…
	}
}
```

The property `attributes` must contain all incoming query-parameters.
Multi-value parameters need not be supported.

### 1.3 Bid Response

The bidders' response will contain details of the bid(offered price), with `id` and `bid`
values in a numeric format:

```json
{
	"id" : $id,
	"bid": bid,
	"content": "the string to deliver as a response"
}
```

### 1.4 Auction Response

The response for the auction must be the `content` property of the winning bid,
with some tags that can be mentioned in the content replaced with respective values.

For now, only `$price$` must be supported, denoting the final price of the bid.


# Solution

## Bidding platform

The platform is quite simple, it is an application with only one endpoint
    
    http://baseurl:[port]/[id]?[key=value,...
    
As agreed.

You can change the port in ```application.prorperties``` or just passing ```server.port``` parameter.
With similar approach number of bidders (and their locations) can be changed (```bidders``` parameter)

Other parameters are:
* ```request.timeout``` means that _each_ bidder has some time threshold for response
* ```biddingRequesterService.type``` parameter described in the section below

### Difference from production version & caveats

Let's quickly go through the differences between current and prod versions (or what else could be done).

* Logging. Current level of logging might be a little excessive. I would change (at some parts) it to DEBUG.
* Template resolver service (see ```com.andymur.yacc.challenge.service.template.TemplateService```) has only one price resolver component, and it is hardcoded there. 
Probably it won't work well on production, if we like to extend this part frequently (and I think we do). For sake of simplicity it's done that way.
* When requesting bidders we use too much of synchronisation. ```biddingRequesterService.type``` parameter default value is 'sync', it means that bidder responses handled in particular order.
When the value equals to 'async' platform pays no attention for order of responses. With the 'async' mode application doesn't pass provided tests.
(there are tests when bidder's response ordering is important if there are several bidders with same bid amount). I would use 'async' approach, cause in my opinion it is fair not to pay attention to the order of incoming responses.
*  ```request.timeout``` property means different for 'sync' and 'async' modes. In the first case it means a timeout for each request in the latter case a timeout for all requests. It is not consistent and should be avoided. 

### How to build & test the application

To build it just type from project directory 

```mvn clean package spring-boot:repackage```

after that you can launch the application

```cd target && java -jar bidding-platform-service.jar```

If you want to start it like a docker container please:

* Build docker image with a command ```docker build --tag=bidding-platform-service:latest .```
* Run docker image with wanted properties ```docker run -e server.port=8080 -e bidders="http://localhost:8081/,http://localhost:8082/,http://localhost:8083" --network="host" bidding-platform-service:latest```

You can also test it.
For unit tests please do this.

```mvn clean test```

Integration part could be tested with a help from provided script. 

Enjoy!

SY, Andrei