FROM openjdk:15

COPY target/discord.jar /discord.jar

CMD ["java", "-jar", "/discord.jar"]

