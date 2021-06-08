package ib.project;

import java.io.File;
import java.io.IOException;
import java.util.ResourceBundle;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import ib.project.rest.DemoController;

@SpringBootApplication
public class DemoApplication {

	private static String DATA_DIR_PATH;
	private static Logger logger = LoggerFactory.getLogger(DemoApplication.class);
	
	static {
		ResourceBundle rb = ResourceBundle.getBundle("application");
		DATA_DIR_PATH = rb.getString("dataDir");
	}
	
	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
		
		//create files folder in target/classes
		try {
			String path = DemoController.class.getProtectionDomain().getCodeSource().getLocation().getPath() + DATA_DIR_PATH;
			path = path.replaceAll("%20", " ");
			FileUtils.forceMkdir(new File(path));
			
			logger.info("Folder with user files: " + path);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
 