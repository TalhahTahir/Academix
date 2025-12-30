package com.talha.academix;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AcademixApplicationTests {

	@Test
	void contextLoads() {
	}

	@Autowired javax.sql.DataSource dataSource;

@Test
void printActualDatabase() throws Exception {
    try (var c = dataSource.getConnection();
         var s = c.createStatement();
         var rs = s.executeQuery("select database()")) {
        rs.next();
        System.out.println("JDBC URL     = " + c.getMetaData().getURL());
        System.out.println("JDBC USER    = " + c.getMetaData().getUserName());
        System.out.println("SQL DATABASE = " + rs.getString(1));
    }
}

}
