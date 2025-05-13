FROM sbtscala/scala-sbt:eclipse-temurin-17.0.4_1.7.1_3.2.0

# 1. Create credentials directory
RUN mkdir -p /root/.sbt/1.0

# 2. Add GitHub Packages resolver (public info)
COPY resolvers.sbt /root/.sbt/1.0/resolvers.sbt

# 3. Build args for credentials
ARG GITHUB_USER
ARG GITHUB_TOKEN

# 4. Create credentials file (only during build)
RUN echo "credentials += Credentials(\"GitHub Package Registry\", \"maven.pkg.github.com\", \"$GITHUB_USER\", \"$GITHUB_TOKEN\")" > /root/.sbt/1.0/credentials.sbt

WORKDIR /app
COPY . .
RUN sbt update && sbt compile

# 5. Remove credentials after build (optional but recommended)
RUN rm /root/.sbt/1.0/credentials.sbt

CMD ["sbt", "run"]