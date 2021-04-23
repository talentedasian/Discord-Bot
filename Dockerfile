FROM openjdk:15

COPY target/bot.discord-0.0.1-SNAPSHOT.jar /discord.jar

CMD ["java", "-jar", "/discord.jar"]

