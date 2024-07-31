## Build and Run the Application

**Prerequisites**

* Git
* Java 17
* Apache Maven
* Docker
* Redis Insight

### Java tips

```
in powershell, to set JAVA_HOME specific $env:JAVA_HOME="C:\dev\Java\jdk17"
```
### Prepare Azure resources
Create a new step in https://vusion-vsts.visualstudio.com/vtransmit-hf/_release?definitionId=19&view=mine&_a=releases with a new prefix (e.g: 078), it will create azure resources required for next steps. 



### Build & Run the Project

1- Get the project

```
> git clone https://<project>.git

> cd vtransmit-v2

```

2- Package the Java Service with Maven  *(Need to put that in the Docker phase...)*

```
> mvn clean package -DskipTests

```

3- Build and Run the Docker Compose image project

```
>  docker-compose up -d --build

```
| **This can take some time**

### Scale the number of consumer

When you will be scaling your application sending millions of messages you want the consuming services to be able to scale and also to be available all the time to be able to process the messages in real time.

With Redis Streams you need to add new "consumers" to the consumer group you want to scale.

Then when you have multiple process Redis will distribute the read by consumers and if one fail another one will continue to read the messages... and this is where it is also interesting to have a way to Claim and Ack the messages!

For example put 3 "vtransmit-listener-status" consumer and 5 "vtransmit-transmission".

**Add Consumers**

Open a terminal and run the following command to scale the container with Docker Compose:

```
> docker-compose up -d --scale vtransmit-listener-status=3 --scale vtransmit-transmission=5  --no-recreate
```

This will start new containers.


# lombok Eclipe Configuration #
https://www.baeldung.com/lombok-ide
Project >> properties >> Compiler >> Annotation processing : enable
