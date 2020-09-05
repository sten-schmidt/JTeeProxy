# JTeeProxy
JTeeProxy is a simple teeproxy implemented in Java. It allows you to transparently forward a TCP port to a primary- and a secondary host and port. Both hosts can be set to localhost for internal port redirection.
<pre>
[Client] <<----->> [JTeeProxy-Server:Port] 
                              | <<-------->> [PRIMARY-Server:Port]
                              | ---------->> [SECONDARY-Server:Port]
</pre>
The responsed data from PRIMARY-Server can be logged by the JTeePoroxy-Process an will be forwarded the Client.
The responsed data from SECONDARY-Server can be logged by the JTeeProxy-Process, but will not be forwared to the Client.

## Quickstart

```powershell
#Build
.\build.ps1

#Start Server A
java -cp .\JTeeProxy.jar net.stenschmidt.jteeproxy.testtools.EchoServer 6789 
 
#Start Server B
java -cp .\JTeeProxy.jar net.stenschmidt.jteeproxy.testtools.EchoServer 6790 

#Start the Proxy
java -cp .\JTeeProxy.jar net.stenschmidt.jteeproxy.JTeeProxy 1234 localhost 6789 localhost 6790 

#Connect the Client to the Proxy
java -cp .\JTeeProxy.jar net.stenschmidt.jteeproxy.testtools.EchoClient localhost 1234
#Type some Text and press Enter
```

## TODO / planned features

* secondary servers should be optional
* error with secondary must not affect the connection with the primary server
* Logging
* HTTPS / SSL Support
* One primary server and a list of secondary servers
