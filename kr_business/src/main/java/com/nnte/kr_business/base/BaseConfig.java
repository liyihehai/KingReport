package com.nnte.kr_business.base;

import com.nnte.framework.base.DataLibrary;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BaseConfig {
    @Bean(initMethod = "initDataLibrary")
    public DataLibrary dataLibrary(){
        return new DataLibrary();
    }
/*    @Bean(initMethod = "initJedisCom")
    public JedisCom jedisCom(){
        return new JedisCom();
    }*/
}
