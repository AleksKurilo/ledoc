package dk.ledocsystem.api;

import org.pmw.tinylog.Configurator;
import org.pmw.tinylog.Level;
import org.pmw.tinylog.writers.ConsoleWriter;
import org.pmw.tinylog.writers.FileWriter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.text.SimpleDateFormat;
import java.util.Calendar;

@SpringBootApplication(scanBasePackages = "dk.ledocsystem")
@EnableJpaRepositories("dk.ledocsystem.data.repository")
@EntityScan("dk.ledocsystem.data")
public class LedocApplication {

	private static final String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());

	public static void main(String[] args) {
		initLogger();
		SpringApplication.run(LedocApplication.class, args);
	}

	private static void initLogger() {
		Configurator.defaultConfig()
				.addWriter(new FileWriter("logs/ledoc-" + timeStamp + ".log"))
				.addWriter(new ConsoleWriter())
				.level(Level.WARNING)
				.activate();
	}
}
