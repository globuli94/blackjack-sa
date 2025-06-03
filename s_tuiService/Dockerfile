# Use an official sbt + Scala + JDK image
FROM sbtscala/scala-sbt:eclipse-temurin-17.0.4_1.7.1_3.2.0

# Set the working directory
WORKDIR /tuiService

# Copy build files to leverage caching
COPY build.sbt ./
COPY project ./project

# Download dependencies early
RUN sbt update

# Copy all source code
COPY . .

# Build fat JAR using sbt assembly (make sure sbt-assembly plugin is enabled in your project)
RUN sbt assembly

# Run the JAR - replace the jar name with your actual output jar path
CMD ["java", "-jar", "target/scala-2.13/gameService-assembly-0.1.0.jar"]
