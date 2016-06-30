package com.mohoo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * baseMain
 * 类描述 <p>
 * 创建日期：2016年6月30日<br>
 * 修改历史：<br>
 * 修改日期：<br>
 * 修改作者：<br>
 * 修改内容：<br>
 * @author Administrator
 * @version 1.0
 */
@Configuration
@ComponentScan
@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class,HibernateJpaAutoConfiguration.class})
@EnableScheduling
public class App 
{
    public static void main( String[] args )
    {
    	SpringApplication.run(App.class, args);
    }
}
