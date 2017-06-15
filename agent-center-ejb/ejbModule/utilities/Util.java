package utilities;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class Util {

	public static String getMasterNodeIP() {
		Properties prop = new Properties();
		InputStream input = null;
		try {
			input = new FileInputStream("master_node.properties");
			prop.load(input);

			input.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return prop.getProperty("ip");
	}

	public static String getMasterNodePort() {
		Properties prop = new Properties();
		InputStream input = null;
		try {
			input = new FileInputStream("master_node.properties");
			prop.load(input);

			input.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return prop.getProperty("port");
	}

	public static String getThisNodeIP() {
		String ip = null;
		try {
			BufferedReader in = new BufferedReader(new FileReader(System.getProperty("jboss.server.config.dir") + "/ip_port.txt"));
			
			ip = in.readLine().split("\\s+")[0];
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ip;
	}

	public static String getThisNodePort() {
		String port = null;
		try {
			BufferedReader in = new BufferedReader(new FileReader(System.getProperty("jboss.server.config.dir") + "/ip_port.txt"));
			
			port = in.readLine().split("\\s+")[1];
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return port;
	}

}
