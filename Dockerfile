FROM dany712/java17-gdal3.6.4
# Run backend
WORKDIR /home/app
ADD /target/BiologicalProductivity-0.0.1-SNAPSHOT.jar backend.jar
ENTRYPOINT ["java", "-jar", "backend.jar" ]
