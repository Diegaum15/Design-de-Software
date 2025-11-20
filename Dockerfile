# Usa uma imagem base com Java 17 (que é compatível com o seu pom.xml)
FROM eclipse-temurin:17-jdk

# Define o diretório de trabalho dentro do contêiner
WORKDIR /app

# Copia o JAR compilado do host (que está em backend/target) para dentro do contêiner
# O nome do JAR é definido no seu pom.xml
COPY target/seu-cantinho.jar app.jar

# Expõe a porta que o Spring Boot usa
EXPOSE 8080

# Define o comando que será executado quando o contêiner iniciar
ENTRYPOINT ["java", "-jar", "app.jar"]
