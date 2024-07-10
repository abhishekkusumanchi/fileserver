# Use the official Tomcat base image with JDK 17
FROM tomcat:10.1-jdk17

# Copy the generated WAR file to the Tomcat webapps directory
COPY target/fileserver.war /usr/local/tomcat/webapps/fileserver.war

# Expose the default Tomcat port
EXPOSE 8080

# Use a shell to start Tomcat and create the static folder
CMD ["sh", "-c", "catalina.sh run & while [ ! -d /usr/local/tomcat/webapps/fileserver ]; do sleep 1; done; mkdir -p /usr/local/tomcat/webapps/fileserver/static; tail -f /dev/null"]
