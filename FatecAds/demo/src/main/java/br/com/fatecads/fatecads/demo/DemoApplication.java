package br.com.fatecads.fatecads.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "br.com.fatecads.fatecads")
@EnableJpaRepositories(basePackages = "br.com.fatecads.fatecads.repository")
@EntityScan(basePackages = "br.com.fatecads.fatecads.entity")
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}
