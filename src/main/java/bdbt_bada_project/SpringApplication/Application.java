package bdbt_bada_project.SpringApplication;

import bdbt_bada_project.SpringApplication.Helpers.ServerGUI;
import bdbt_bada_project.SpringApplication.Persistence.GlobalDataManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.swing.*;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
