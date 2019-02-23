# Liquibase database evolution with with Spring Boot 
Liquibase can not generate ChangeLog from empty database or JPA entities. It should be run against existing database(dev or test) initially before using Liquibase-Hibernate plugin. If you do not have existing database, create new one with 1 table and Liquibase takes it from there. Steps to setup Liquibase from Scratch
1. Create SpringBoot project or clone this project
2. Add `liquibase.properties` under src/main/resources folder with the following content and change DB properties accordingly
```outputChangeLogFile= src/main/resources/db/db.changelog-master.xml
driver=com.mysql.cj.jdbc.Driver
url=jdbc:mysql://localhost:3306/liquibasedemo
username=root
password=<your password>


# Reference Properties
referenceUrl=hibernate:spring:com.liquibasedemo.model?dialect=org.hibernate.dialect.MySQLDialect&hibernate.physical_naming_strategy=org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy&hibernate.implicit_naming_strategy=org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy
```
3. Add Liquibase plugin to pom.xml and change change log file locations. Replace versions based on my pom.xml file
```xml
<plugin>
                <groupId>org.liquibase</groupId>
                <artifactId>liquibase-maven-plugin</artifactId>
                <version>${liquibase-maven-plugin.version}</version>
                <configuration>
                    <propertyFile>src/main/resources/liquibase.properties</propertyFile>
                    <changeLogFile>src/main/resources/db/db.changelog-master.xml</changeLogFile>
                    <diffChangeLogFile>src/main/resources/db/changelog/${maven.build.timestamp}_changelog.xml</diffChangeLogFile>
                    <logging>info</logging>
                </configuration>
               <executions>
                    <execution>
                        <id>update-profile</id>
                        <phase>process-resources</phase>
                        <goals>
                            <goal>update</goal>
                        </goals>
                    </execution>
                    <execution>
                        <id>diff-profile</id>
                        <phase>process-test-resources</phase>
                        <goals>
                            <goal>diff</goal>
                        </goals>
                    </execution>
                </executions>
                <dependencies>
                    <dependency>
                        <groupId>org.liquibase.ext</groupId>
                        <artifactId>liquibase-hibernate5</artifactId>
                        <version>${liquibase-hibernate5.version}</version>
                    </dependency>
                    <dependency>
                        <groupId>org.springframework.boot</groupId>
                        <artifactId>spring-boot-starter-data-jpa</artifactId>
                        <version>${spring-boot.version}</version>
                    </dependency>
                    <dependency>
                        <groupId>javax.validation</groupId>
                        <artifactId>validation-api</artifactId>
                        <version>${validation-api.version}</version>
                    </dependency>
                    <dependency>
                        <groupId>org.javassist</groupId>
                        <artifactId>javassist</artifactId>
                        <version>${javassist.version}</version>
                    </dependency>

                </dependencies>
            </plugin>
```
4. Change `application-dev.yml` to point to custom change log file 
```yaml
  liquibase:
    contexts: dev
    change-log: classpath:db/db.changelog-master.xml
```
5. Add following dependency to `pom.xml`
```xml
      <dependency>
            <groupId>org.liquibase</groupId>
            <artifactId>liquibase-core</artifactId>
        </dependency>
```
6. Now let's create empty database `liquibasedemo` in MySQL server
7. In liquibase-maven-plugin we have 2 custom profiles(You can give any name you want) but make sure to include phase and goals as is
    - diff-profile (Generates Liquibase changeset based on changes JPA entities)
    - update-profile (Updates the Database based on change sets generated in above step)
8. Now go to project root directory and run the following command (Below command equivalent to `$mvn liquibase:diff`) . It will generate Change Sets based JPA entities, if you are running this for first time, it will generate change sets for all the files
```
$ mvn process-test-resources

```  
9. Now run update command to generate tables and `DATABASECHANGELOG` tables
```angular2
$ mvn process-resources
```
10. Now go to database see all the changes applied and tables created
11. Repeat steps 8 and 9 every time you make any changes to JPA entities
