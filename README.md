Java Proxy Library
==================

Implements a basic HTTP proxy which can be used to analyze HTTP requests
or responses which pass through the proxy.

Features
--------
- Listen for requests to a specific host or URL
- Extract the request and response headers
- Extract the request and response body
- Rewrite the request and response headers

Dependencies
------------
- jlibutils [https://github.com/mback2k/jlibutils]

TODO
----
- Implement advanced filter chains for listeners
- Implement HTTP CONNECT passthrough
- (Optional) Allow modification of requests and responses
