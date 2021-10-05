package com.example.MasterProjekt.config.initialisation;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class InitialDataLoader implements ApplicationListener<ContextRefreshedEvent> {

    private boolean isSetup = false;

    @Autowired
    private BaseDataInitialiser baseDataInitialiser;

    @Transactional
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (isSetup) {
            return;
        }

        baseDataInitialiser.initAdminUser();

        isSetup = true;
    }

}
