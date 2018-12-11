package pkg;
import java.io.*;
import java.net.*;
import java.nio.*;
import java.util.*;

public class ClientData {
	
	private String nick;
	private InetAddress IPAddress;
	private int port;

	
	public ClientData(String nick, InetAddress iPAddress, int port) {
		
		this.nick = nick;
		IPAddress = iPAddress;
		this.port = port;
	}
	public String getNick() {
		return nick;
	}
	public void setNick(String nick) {
		this.nick = nick;
	}
	public InetAddress getIPAddress() {
		return IPAddress;
	}
	public void setIPAddress(InetAddress iPAddress) {
		IPAddress = iPAddress;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	
	public String toString() {
		return "ClientData [nick=" + nick + ", IPAddress=" + IPAddress + ", port=" + port + "]";
	}
}
