# JTeeProxy
JTeeProxy is a simple TCP bridging software that allows you to transparently forwarded a TCP port to a primary- and a secondary-host and port. Both hosts can be set to localhost for internal port redirection.
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
mvn clean package

#Start Server A
java -cp .\JTeeProxy.jar test.tools.EchoServer 6789 
 
#Start Server B
java -cp .\JTeeProxy.jar test.tools.EchoServer 6790

#Start the Proxy
java -cp .\JTeeProxy.jar net.stenschmidt.jteeproxy.JTeeProxy 1234 localhost 6789 localhost 6790

#Connect the Client to the Proxy
java -cp .\JTeeProxy.jar test.tools.EchoClient localhost 1234
#Type some Text and press Enter
```

## TODO / planned features

* secondary servers should be optional
* error with secondary must not affect the connection with the primary server
* Logging
* HTTPS / SSL 
* One primary server and a list of secondary servers
