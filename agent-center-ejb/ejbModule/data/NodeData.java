package data;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.servlet.ServletRequest;

import org.springframework.web.bind.ServletRequestUtils;

import entities.AgentCenter;
import utilities.Util;

@Startup
@Singleton
public class NodeData {
	
	private static List<AgentCenter> nodes = new ArrayList<AgentCenter>();
	
	@PostConstruct
	void init() {
		Properties prop = new Properties();
		InputStream input = null;
		OutputStream output = null;
		try {

			input = new FileInputStream("master_node.properties");
			
			
			prop.load(input);
			input.close();
			output = new FileOutputStream("master_node.properties");
			if(prop.getProperty("ip") == null && prop.getProperty("port") == null) {
				prop.setProperty("ip", Util.getThisNodeIP());
				prop.setProperty("port", Util.getThisNodePort());

				prop.store(output, null);
			}
			
			output.close();
			
		} catch (IOException ex) {
			ex.printStackTrace();
		}  
	}
	
	@PreDestroy
	void clearMasterNodeInfo() {
		Properties prop = new Properties();
		OutputStream output = null;
		InputStream input = null;
		try {

			input = new FileInputStream("master_node.properties");
			output = new FileOutputStream("master_node.properties");
			
			prop.load(input);
			if(prop.getProperty("ip").equals(Util.getThisNodeIP()) && prop.getProperty("port").equals(Util.getThisNodePort())) {
				prop.setProperty("ip", null);
				prop.setProperty("port", null);

				prop.store(output, null);
			}
			
			input.close();
			output.close();
			
		} catch (IOException ex) {
			ex.printStackTrace();
		}  
	}
}
