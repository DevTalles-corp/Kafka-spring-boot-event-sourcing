package com.bistro;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

public class ModularityTests {

    ApplicationModules modules = ApplicationModules.of(BistroApplication.class);

    @Test
    void printsModuleStructure(){
        modules.forEach(System.out::println);
    }

    @Test
    void verifiesModuleBoundaries(){
        modules.verify();
    }

}
