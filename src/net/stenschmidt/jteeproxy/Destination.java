package net.stenschmidt.jteeproxy;

/**
 * A Destination describes the hostname and the portnumber of an TCP endpoint.
 */
public class Destination {
	private String _host;
	private int _port;
	
	public Destination (String host, int port) {
		_host = host;
		_port = port;
	}
	
	/**
	 * The Destination-Configuration is enabled when a hostname and a valid port is provided.
	 * @return boolean true if valid configuration is provided, else false. 
	 */
	public boolean isEnabled() {
		return _host.length() > 0 && _port > 0;
	}
	
	public int getPort() {
		return _port;
	}	
	
	public String getHost() {
		return _host;
	}
		
}