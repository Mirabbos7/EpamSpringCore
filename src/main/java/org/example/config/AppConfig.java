package org.example.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.Resource;

import java.util.ArrayList;
import java.util.List;

@Configuration
@ComponentScan(basePackages = "org.example")
@PropertySource("classpath:application.properties")
public class AppConfig {

    @Value("${storage.file.trainingTypes}")
    private Resource trainingTypeResource;
    @Value("${storage.file.users}")
    private Resource userResource;
    @Value("${storage.file.trainees}")
    private Resource traineeResource;
    @Value("${storage.file.trainers}")
    private Resource trainerResource;
    @Value("${storage.file.trainings}")
    private Resource trainingResource;

    @Bean
    public List<Resource> initFiles() {
        final var resources = new ArrayList<Resource>();
        resources.add(trainingTypeResource);
        resources.add(userResource);
        resources.add(traineeResource);
        resources.add(trainerResource);
        resources.add(trainingResource);
        return resources;
    }
}
