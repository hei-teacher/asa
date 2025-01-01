# Asa - Work Management Tool

### Features
- Explore all missions across your organizations by products
- Identify care missions like days off, sick days and team building events
- Daily track your time by mission
- Track throughout the year who worked on same missions with you
- Let your coworkers know when you will not be available

### Running

First, set all following environment variables.
```
ASA_LOGOUT_URL=
AWS_SES_SOURCE=
COGNITO_LOGOUT_URL=
SERVER_ERROR_INCLUDEMESSAGE=
SPRING_DATASOURCE_PASSWORD=
SPRING_DATASOURCE_URL=
SPRING_DATASOURCE_USERNAME=
SPRING_FLYWAY_OUTOFORDER=
SPRING_SECURITY_OAUTH2_CLIENT_PROVIDER_COGNITO_ISSUERURI=
SPRING_SECURITY_OAUTH2_CLIENT_PROVIDER_COGNITO_USERNAMEATTRIBUTE=
SPRING_SECURITY_OAUTH2_CLIENT_REDIRECTURI=
SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_COGNITO_CLIENTID=
SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_COGNITO_CLIENTSECRET=
SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_COGNITO_SCOPE_0_=
SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_COGNITO_SCOPE_1_=
SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_COGNITO_SCOPE_2_=
ASA_CARE_PRODUCT_CODE=
```

Then, run Spring Boot as usual,
for example by building an uber jar through `gradle bootJar`,
then by launching `java -jar asa.jar`.
As there are a lot of environment variables to set,
you probably want to load them through an `.env` file:
`export $(cat .env | xargs) && java -jar asa.jar`.

Last, visit `http://localhost:8080`

### Releases uber jar

* [v1](https://drive.google.com/file/d/1JFlfpSun6Cl26-URsR5dtgYU8-xEoF28/view?usp=drive_link)
