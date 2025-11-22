# Usa uma imagem base Java com Alpine Linux, que é pequena e segura
FROM eclipse-temurin:17-jdk-alpine

# Define o diretório de trabalho dentro do contêiner
WORKDIR /app

# Argumento para especificar o nome do arquivo JAR que será copiado
# O nome do seu projeto é 'seu-cantinho', então o JAR será 'seu-cantinho-1.0.0.jar'
ARG JAR_FILE=target/*.jar

# Copia o arquivo JAR compilado (que deve ser gerado pelo 'mvn package')
# Certifique-se de executar 'mvn clean package' antes de construir a imagem Docker!
COPY ${JAR_FILE} app.jar

# Expõe a porta que o Spring Boot usa (8080)
EXPOSE 8080

# Comando para rodar a aplicação quando o contêiner for iniciado
# O perfil 'docker' será usado para carregar as configurações do banco definidas
# para o ambiente Docker.
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=docker"]
