package com.example.frauddetection.config;

import java.io.IOException;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.KieRepository;
import org.kie.api.builder.ReleaseId;
import org.kie.api.event.rule.ObjectDeletedEvent;
import org.kie.api.event.rule.ObjectInsertedEvent;
import org.kie.api.event.rule.ObjectUpdatedEvent;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.internal.io.ResourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for setting up Drools in the application.
 * 
 * This class initializes and configures the Drools KieSession and KieContainer, 
 * which are used to execute business rules defined in Drools `.drl` files.
 * 
 * Features:
 * - Loads Drools rules from the classpath (`rules.drl`).
 * - Configures a `KieSession` for rule execution.
 * - Adds a global variable (`showResults`) to the session for output display.
 * - Optionally enables debug logging for rule events (insert, update, delete) 
 *   based on the `rule.debug` property.
 * 
 * Beans:
 * - `KieSession`: The main session for executing Drools rules.
 * - `KieContainer`: The container that holds the compiled rules.
 * 
 * Properties:
 * - `rule.debug`: A boolean property to enable or disable debug logging for rule events.
 * 
 * Dependencies:
 * - Drools libraries for rule execution.
 * - Spring Framework for dependency injection and configuration.
 * 
 * Logging:
 * - Logs the creation of the `KieSession` and `KieContainer`.
 * - Logs rule events (insert, update, delete) when debug mode is enabled.
 * 
 * Example Usage:
 * - Inject the `KieSession` bean into your service or component to execute rules.
 * - Place your `.drl` file in the classpath (e.g., `src/main/resources/rules.drl`).
 * 
 * Author: linjiarong
 * Date: 2025-3-28
 */
@Configuration
public class DroolsConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(DroolsConfig.class);

    private KieServices kieServices = KieServices.Factory.get();
    @Value("${rule.debug:false}")
    private boolean isRuleDebug;

    @Bean
    public KieSession getKieSession() throws IOException {
        LOGGER.info("Session created...");
        KieSession kieSession = getKieContainer().newKieSession();
        kieSession.setGlobal("showResults", new OutputDisplay());
        if(isRuleDebug){
            kieSession.addEventListener(new RuleRuntimeEventListener() {
                @Override
                public void objectInserted(ObjectInsertedEvent event) {
                    LOGGER.info("Object inserted \n "
                            + event.getObject().toString());
                }

                @Override
                public void objectUpdated(ObjectUpdatedEvent event) {
                    LOGGER.info("Object was updated \n"
                            + "New Content \n"
                            + event.getObject().toString());
                }

                @Override
                public void objectDeleted(ObjectDeletedEvent event) {
                    LOGGER.info("Object retracted \n"
                            + event.getOldObject().toString());
                }
            });
        }
        
        return kieSession;
    }

    @Bean
    public KieContainer getKieContainer() throws IOException {
        LOGGER.info("Container created...");
        getKieRepository();
        KieBuilder kb = kieServices.newKieBuilder(getKieFileSystem());
        kb.buildAll();
        KieModule kieModule = kb.getKieModule();
        return kieServices.newKieContainer(kieModule.getReleaseId());
    }

    private void getKieRepository() {
        final KieRepository kieRepository = kieServices.getRepository();
        kieRepository.addKieModule(new KieModule() {
            @Override
            public ReleaseId getReleaseId() {
                return kieRepository.getDefaultReleaseId();
            }
        });
    }

    private KieFileSystem getKieFileSystem() throws IOException {
        KieFileSystem kieFileSystem = kieServices.newKieFileSystem();
        kieFileSystem.write(ResourceFactory.newClassPathResource("rules.drl"));
        return kieFileSystem;
    }
}
