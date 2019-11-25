package cn.lovezsm.locationsystem;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("cn.lovezsm.locationsystem.base.mapper")
public class LocationsystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(LocationsystemApplication.class, args);
    }

}
