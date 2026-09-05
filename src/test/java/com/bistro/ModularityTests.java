package com.bistro;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

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

    @Test
    void writesDocumentation(){
        new Documenter(modules).writeDocumentation();
    }

}
