package org.jgayoso.ncomplo;

import org.jasypt.util.password.ConfigurablePasswordEncryptor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@ComponentScan
@EnableAutoConfiguration
@EnableJpaRepositories
public class Application {

	
	@Bean
	public ConfigurablePasswordEncryptor configurablePasswordEncryptor() {
		return new ConfigurablePasswordEncryptor();
	}
	
	
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
    
}
