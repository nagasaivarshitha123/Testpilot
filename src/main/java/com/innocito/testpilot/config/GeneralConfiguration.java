package com.innocito.testpilot.config;

import com.innocito.testpilot.entity.ApiRepository;
import com.innocito.testpilot.model.ApiRepositoryResponse;
import jakarta.persistence.EntityManagerFactory;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
public class GeneralConfiguration {
//    @Bean
//    public ModelMapper modelMapper() {
//        ModelMapper modelMapper = new ModelMapper();
//        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
//        return modelMapper;
//    }


    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        // ✅ Custom mapping: ApiRepository.project.id → ApiRepositoryResponse.projectId
        modelMapper.addMappings(new PropertyMap<ApiRepository, ApiRepositoryResponse>() {
            @Override
            protected void configure() {
                map(source.getProject().getId(), destination.getProjectId());
            }
        });

        return modelMapper;
    }

















    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

    @Primary
    @Bean(name = "entityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(EntityManagerFactoryBuilder builder,
                                                                       DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages("com.innocito.testpilot") // Adjust package name
                .persistenceUnit("default")
                .build();
    }

    @Bean(name = "transactionManager")
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}