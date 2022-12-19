##avra-rest-api-test-automation-framework
Template project with REST API and queue tests for backend services

### Project uses:
* TestNG as a framework to manage tests https://testng.org/doc/
* REST Assured library to testing and validating requests and responses http://rest-assured.io/
* Spring library as dependency injection technique https://spring.io/projects/spring-framework
* Gradle as a dependency and additionally test runner manager https://gradle.org/
* Spring AMQP with two modules: spring-amqp and spring-rabbit to send events to provide data for tests and for testing purpose https://spring.io/projects/spring-amqp#overview
* Project Lombok - java library used in model classes to simplify code https://projectlombok.org/
* Gson - Java library that can be used to convert Java Objects into their JSON representation or convert a JSON string to an equivalent Java object https://github.com/google/gson
* Allure Framework for reports https://docs.qameta.io/allure/
* Apache PDFBox library for working with .pdf documents https://pdfbox.apache.org/
* Apache POI for working with Microsoft Documents
* NamedParameterJdbcTemplate in DB queries to avoid potentially SQL injection (as good practice here)

### Prerequisites
* Java8+
* optional: gradle

### Initial
* clone repository
* open build.gradle file in IDE (IntelliJ)
* import dependencies with gradle
* add to IDE (IntelliJ) Lombok plugin: (Settings -> Plugins -> 'Browse repositories..." -> find and install Lombok -> restart IDE)
* select IDE "Enable annotation processing": (Settings -> Build, Execution, Deployment -> Compiler -> Annotation Processors)

### Installing the CheckStyle-IDEA Plugin
* open [IntelliJ IDEA → Configure → Settings] (or [File → Settings…] if you are in a project)
* go to [Plugins tab → Marketplace → search CheckStyle-IDEA]
* install the one that matches the name completely and restart
* go to [File → Settings → Tools → Checkstyle → Configuration file] and add "config/checkstyle/checkstyle.xml" and set as active


### Environment to run tests locally
Necessary services to run your tests locally should be pointed in 'docker-compose.yml' file in the root of project. As part of 
scalability testing docker-compose.yml should be prepared to run backend services as two instances with different ports.
In 'docker-compose up' command creating two instances of application is pointed as '--scale backend-service-name=2'.

Prepare local environment for test: in IDE's terminal run command:
```
 docker-compose up -d --scale backend-service-name=2
```
After work with tests, the best way is to kill containers:
```
docker-compose down
```

### GitLab CI/CD environment to run tests
Necessary services to run your tests with GitLab CI/CD should be pointed in 'run_tests.sh' file in the 'gitlab-ci' folder.
Also necessary .env data for services should be placed in .env files in the 'gitlab-ci' folder.

### Running tests
#####GitLab schedules
Your tests might be run with GitLab scheduled jobs:
```
https://git.avra.pl/avra-rest-api-test-automation-framework/pipeline_schedules
```
To run tests add "New schedule" with
```
Description: name of schedule

Interval Pattern: for example select "Every day (at 4:00am)

Cron Timezone: Warsaw

Target Branch: branch name with tests (develop or created feature branch)

Variables:
- BACKEND_TAG_NAME 0.0.2 (tag name of backend application, latest is as default)
- POSTGRES_IMAGE_NAME 0.0.2 (tag/version name of database application)
- RABBITMQ_IMAGE_NAME 0.0.2 (tag/version name of rabbitmq application)
- WIREMOCK_TAG_NAME 0.0.2 (tag/version name of wiremock application)
- RUN_TEST_COMMAND runTestCommandName (run tests command name, default is: testNGThreads)
- TEST_GROUP testGroupName (test group name, no name is as default)
```
and run it manually or wait for scheduled job.

#####Run local
Locally your tests might be run from IntelliJ- with context menu or with gradle command:
```
gradle testNG -Pprofile=local
or
gradlew testNG -Pprofile=local
or
./gradlew testNG -Pprofile=local
```
Above command runs all your tests one by one.

All tests also might be run with number of threads (in parallel):
```
gradle testNGThreads -Pprofile=local
or
gradlew testNGThreads -Pprofile=local
or
./gradlew testNGThreads -Pprofile=local
```
As default, number of threads is set to '4'. useTestNG() configuration in this gradle task includes line:
```
excludeGroups 'rabbit'
```
It means, that one group is not running al all (it is fake group name- you can configure it for your test project)

Tests also might be run according to their group name:
```
@Test(alwaysRun = true, groups = {"rabbit"})
```
The command, which runs tests from group 'wiremock' looks like below:
```
gradle testNGThreadsGroups -Pprofile=local -Dgroups=wiremock
or
gradlew testNGThreadsGroups -Pprofile=local -Dgroups=wiremock
or
./gradlew testNGThreadsGroups -Pprofile=local -Dgroups=wiremock
```
As default, number of threads is set to '3'.

### Profiles
For every environment a profile should be configured. For now the profiles are
- local
- gitlab-ci

To run a local test with a profile using IntelliJ you have to add -Dspring.profiles.active flag to VM Options.
To set it up repeatedly for every test configure it in Run/Debug configuration -> Templates -> TestNQ
Example:
```
    -Dspring.profiles.active=local -Dtestng.dtd.http=true
```
To run test with a profile for a gradle task, you have to pass the -Pprofile param for the build. Example:
```
gradle testNG -Pprofile=local -Dgroups=someGroupName
or
gradlew testNG -Pprofile=dev -Dgroups=someGroupName
or
./gradlew testNG -Pprofile=gitlab -Dgroups=someGroupName
```

### Adding new service to gitlab-ci's tests
To add a new service to tests please go to "gitlab-ci" directory and edit there run_tests.sh file.
You can copy one of the declaration of the existing services and then paste it with changed container name etc. or copy the
following template for service in particular section:

```
# section: export variable
export SERVICE_IMAGE_NAME="${SERVICE_IMAGE_NAME:-nexus-version}"

# section: nexus image with version
SERVICE_NAME="nexus-service-image-path:${SERVICE_IMAGE_NAME}"

# section: service container name
SERVICE_CONTAINER_NAME="service-name-${CI_BUILD_ID}"

# section: path to env file
SERVICE_ENV_FILE=" ${CI_PROJECT_DIR}/gitlab-ci/your-service-env-file-name.env"

# section: docker pull and run services with containers
docker pull ${SERVICE_NAME} &>/dev/null
docker run --network=${NETWORK} --name ${SERVICE_CONTAINER_NAME} -d -p <port:port> --env-file ${SERVICE_ENV_FILE} ${SERVICE_NAME} &>/dev/null

```

where:
  - ${SERVICE_NAME} you shall put name of your new service
  - use '--env-file' in docker run command if your service needs an .env file

### Logs from services on GitLab
Logs from services are shared as job artifacts inside pipeline overview. You can download full package or browse it to get
particular log, including gradle report from tests. 

### Tests reports on GitLab
Allure report from tests running from schedules is placed in "Settings -> Pages"

### Tests reports locally
Allure report from tests is placed in directory 'allure-report'.
Open 'index.html' file to read tests report.
Gradle report from tests is placed in directory 'build\reports\testng'
