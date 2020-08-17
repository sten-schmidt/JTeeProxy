# JTeeProxy
JTeeProxy is a simple TCP bridging software that allows you to transparently forwarded a TCP port to an primary- and an secondary-host and port. Both hosts can be set to localhost for internal port redirection.
<pre>
[Client] <<----->> [JTeeProxy-Server:Port] 
                              | <<-------->> [PRIMARY-Server:Port]
                              | ---------->> [SECONDARY-Server:Port]
</pre>
The responsed data from PRIMARY-Server can be logged by the JTeePoroxy-Process an will be forwarded the Client.
The responsed data from SECONDARY-Server can be logged by the JTeeProxy-Process, but will not be forwared to the Client.
