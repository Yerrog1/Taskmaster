# Desarrollo de TaskMaster

## Requisitos

- Java 17
- Apache Maven
- MySQL 8.0
- Docker (o Podman)

## Variables de entorno

En la aplicación se utilizan las siguientes variables de entorno, aunque pueden ser cambiadas en el
fichero `application.properties` de la ruta donde se ejecute la aplicación.

Entre `comillas` el nombre de la variable. En **negrita** los entornos donde es obligatoria.

- `DATABASE_URL` **PROD**: La URL completa de la base de datos (`jdbc:DRIVER://HOST:PORT/DATABASE`)
- `DATABASE_USER` **PROD**: Nombre del usuario de la base de datos.
- `DATABASE_PASSWORD` **PROD**: Contraseña del usuario de la base de datos.
- `CORS_ALLOWED_ORIGINS` **DEVL**, **TEST**, **PROD**: Lista de dominios permitidos para CORS.
- `FRONTEND_BASE_URL`: URL del frontend.

- `APPLICATIONINSIGHTS_CONNECTION_STRING`: Cadena de conexión a Azure Application Insights.
- `APPLICATIONINSIGHTS_ROLE_NAME`: Nombre de la aplicación en Azure Application Insights.

- `AZURECS_SENDER_ADDRESS` **DEVL**, **PROD**: Dirección de correo electrónico del remitente.
- `AZURECS_CONNECTION_STRING` **DEVL**, **PROD**: Cadena de conexión a Azure Communication Services.
- `AZURE_STORAGE_ACCOUNT_NAME` **DEVL**, **PROD**: Nombre de la cuenta de Azure Storage.
- `AZURE_STORAGE_ACCOUNT_KEY` **DEVL**, **PROD**: Clave de la cuenta de Azure Storage.
- `AZURE_STORAGE_CONTAINER_NAME` **DEVL**, **PROD**: Nombre del contenedor de Azure Storage.

- `JWT_SECRET` **PROD**: Clave secreta para firmar los tokens JWT.

## Inicialización de la base de datos

**NOTA: BAJO NINGUNA CIRCUNSTANCIA SE DEBEN UTILIZAR ESTOS COMANDOS EN LA BASE DE DATOS EN
PRODUCCIÓN. ESTA CONTRASEÑA NO ES SEGURA, Y LA BASE DE DATOS PODRÍA SER ATACADA CON RELATIVA
FACILIDAD.**

### En local

Ejecutar esto en MySQL en localhost, para crear la base de datos en local para desarrollo.

```mysql
CREATE DATABASE IF NOT EXISTS `taskmaster` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
DROP USER IF EXISTS 'jdbc';
CREATE USER 'jdbc' identified by 'jdbc';
GRANT ALL PRIVILEGES ON taskmaster.* TO 'jdbc';
```

### En docker

```pwsh
docker run --name aadsql -e MYSQL_PASSWORD=jdbc -e MYSQL_USER=jdbc -e MYSQL_DATABASE=taskmaster -d mysql:latest
```

## GitHub Actions

En el repositorio se utilizan GitHub Actions para un par de tareas. En primer lugar, se utiliza para
compilar y ejecutar los tests de la aplicación cada vez que se hace un push a la rama `main` o a
una pull request.

Además, para cada push a `main`, se realiza despliegue contínuo a la máquina virtual, compilando
la aplicación, subiendo a Azure CR la imagen y desplegando la aplicación en el servidor de
producción.

## Despliegue

La aplicación se puede desplegar de muchas maneras: desde un clúster de Kubernetes, con una chart de
Helm, con un fichero de Docker Compose, con un fichero de Podman Compose, etc.

La forma que hemos adoptado en BugStars es con una base de datos en Azure Database for MySQL y una
máquina virtual en Azure Virtual Machines. Para ello, hemos creado una máquina virtual con Ubuntu
20.04 y le hemos instalado Podman. En la máquina virtual, hemos generado un contenedor con la
imagen (que tenemos en un registro privado de Azure Container Registry) de TaskMaster y le hemos
pasado las variables de entorno necesarias.

Para el frontend, ejecutamos Nuxt en modo producción y la hemos desplegado en la misma máquina
virtual. Para ello, hemos creado un contenedor con la imagen y le hemos pasado las variables de
entorno necesarias.
