<<<<<<< HEAD
package com.novel;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.novel.mapper")
public class NovelApplication {
    public static void main(String[] args) {
        SpringApplication.run(NovelApplication.class, args);
    }
=======
package com.novel;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.novel.mapper")
public class NovelApplication {
    public static void main(String[] args) {
        SpringApplication.run(NovelApplication.class, args);
    }
>>>>>>> f761e4fcf7d418a7792e50eeba7078e6fc32c340
}