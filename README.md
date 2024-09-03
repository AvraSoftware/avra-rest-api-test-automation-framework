## avra-rest-api-test-automation-framework

When developing or testing a microservices-based system, communication between microservices is crucial for the overall reliability and performance of the application. The REST API interface is commonly used for this purpose.

Testing a REST API is an essential stage in the application development process as it ensures that the API operates according to requirements and meets user expectations. Ensuring the quality and proper functioning of the application is vital.

Automating REST API tests offers several benefits, including:

* **Automation of the testing process**: Automated testing speeds up the testing process and increases efficiency by automating repetitive tests, allowing you to focus on more complex test cases.
* **Faster error identification**: Automated testing enables quicker detection of errors, leading to faster responses and problem resolution.
* **Increased code quality**: Automated tests encourage programmers to write more robust and reliable code. They also improve code documentation as programmers need to describe what the tests do, which helps other developers understand the code.
* **Easier deployment**: Automated tests facilitate easier deployment of code changes by providing assurance that new functionalities will not introduce errors in existing parts of the system.
* **Time and cost savings**: Automating REST API tests saves time and money by reducing the need for manual testing. It also decreases the risk of human errors, which can lead to costly issues.

To assist with this, Avra provides a backend testing framework that is available to everyone and has been used commercially multiple times. It includes up-to-date, dedicated tools and usage examples.

### Project Uses:
* [TestNG](https://testng.org/doc/) - Framework for managing tests.
* [REST Assured](http://rest-assured.io/) - Library for testing and validating requests and responses.
* [Spring Framework](https://spring.io/projects/spring-framework) - Dependency injection technique.
* [Gradle](https://gradle.org/) - Dependency and test runner manager.
* [Spring AMQP](https://spring.io/projects/spring-amqp#overview) - Includes `spring-amqp` and `spring-rabbit` modules for sending events to provide data for tests.
* [Project Lombok](https://projectlombok.org/) - Java library used in model classes to simplify code.
* [Gson](https://github.com/google/gson) - Java library for converting Java Objects to JSON representation and vice versa.
* [Allure Framework](https://docs.qameta.io/allure/) - Framework for generating test reports.
* [Apache PDFBox](https://pdfbox.apache.org/) - Library for working with PDF documents.
* [Apache POI](https://poi.apache.org/) - Library for working with Microsoft documents.
* `NamedParameterJdbcTemplate` - Used in DB queries to avoid potential SQL injection (best practice).

### Prerequisites:
* Java 17
* Optional: Gradle

### Setup
1. Clone the repository.
2. Open the `build.gradle` file in your IDE (e.g., IntelliJ).
3. Import dependencies using Gradle.
4. Install the Lombok plugin in IntelliJ: Go to `Settings -> Plugins -> Browse repositories...`, find and install Lombok, and restart the IDE.
5. Enable annotation processing in IntelliJ: Go to `Settings -> Build, Execution, Deployment -> Compiler -> Annotation Processors` and enable it.

### Installing the CheckStyle-IDEA Plugin
1. Open IntelliJ IDEA and go to `File → Settings` (or `Configure → Settings` if not in a project).
2. Go to the `Plugins` tab, search for `CheckStyle-IDEA`, and install it.
3. Restart IntelliJ IDEA.
4. Go to `File → Settings → Tools → Checkstyle → Configuration file`, add `config/checkstyle/checkstyle.xml`, and set it as active.

### Environment for Running Tests Locally
Necessary services for running tests locally should be defined in the `docker-compose.yml` file located in the root of the project. For scalability testing, the `docker-compose.yml` should be configured to run backend services as two instances on different ports. Use the following command to create two instances of the application:


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
##### GitLab schedules
Your tests might be run with GitLab scheduled jobs:
```
https://git.avra.pl/avra-rest-api-test-automation-framework/pipeline_schedules
```
To create a new schedule:
1. Click "New schedule".
2. Fill in the details:
    * **Description**: Name of the schedule.
    * **Interval Pattern**: For example, select "Every day (at 4:00 am)".
    * **Cron Timezone**: Warsaw.
    * **Target Branch**: The branch with tests (e.g., `develop` or a feature branch).
    * **Variables**:
        * `BACKEND_TAG_NAME`: Tag name of the backend application (default: `0.0.2`).
        * `POSTGRES_IMAGE_NAME`: Tag/version name of the database application (default: `0.0.2`).
        * `RABBITMQ_IMAGE_NAME`: Tag/version name of the RabbitMQ application (default: `0.0.2`).
        * `WIREMOCK_TAG_NAME`: Tag/version name of the WireMock application (default: `0.0.2`).
        * `RUN_TEST_COMMAND`: Command to run tests (default: `testNGThreads`).
        * `TEST_GROUP`: Test group name (default: no name).

You can run it manually or wait for the scheduled job.

##### Run local
Locally your tests might be run from IntelliJ - with context menu or with gradle command:
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
It means, that one group is not running al all (it is fake group name - you can configure it for your test project)

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
