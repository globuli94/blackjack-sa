# Use an official sbt + Scala + JDK image
FROM sbtscala/scala-sbt:eclipse-temurin-17.0.4_1.7.1_3.2.0

# Set the working directory
WORKDIR /gameTUIService

# Copy only build files first (to leverage Docker cache)
COPY build.sbt ./
COPY project ./project

# Trigger dependency resolution early
RUN sbt update

# Now copy the rest of the source code
COPY . .

# Optional: pre-compile
RUN sbt compile

# Default command
CMD ["sbt", "run"]
