# API versión 1

## Terminología

DEBE, NO DEBE, DEBERÍA, NO DEBERÍA y PUEDE se usan como
en [RFC 2119](https://www.rfc-es.org/rfc/rfc2119-es.txt).

## URL base

    https://taskmaster-api.costas.dev

## Autenticación

    POR DEFINIR

Si la autenticación es necesaria en el endpoint y no se proporciona, se devuelve un 401.

## Información de la API

    GET /

#### Respuesta

- 200: Información de la API. Ver clase `ApiInfoOutput`.

## Gestión de miebros

### Registrar un nuevo miembro

    POST /api/v1/signup

Autenticación: NO

#### Parámetros

Clase `SignUpCommandInput`:

- **id**: identificador de miembro (string, `^[a-zA-Z0-9]+$`, mínimo 3, máximo
  20, [único](#disponibilidad-de-id-de-miembro))
- **realName**: string (string, mínimo 3, máximo 20)
- **email**: email (email, único *VER ISSUE 25*)
- **plainPassword**: contraseña a usar, en texto plano (string, mínimo 8, máximo 80)

El cliente DEBERÍA comprobar que el ID no está en uso antes de enviar una petición; y DEBERÍA pedir
la contraseña dos
veces para
evitar errores de escritura.

#### Respuesta

- 201: Miembro creado. Sin cuerpo.
- 400: Parámetros inválidos. Ver clase `ValidationExceptionAdvice`.
- 409: ID ya en uso. Sin cuerpo.

### Disponibilidad de id de miembro

    GET /api/v1/memberidAvailable

Autenticación: NO

#### Parámetros

- **id**: identificador de miembro a verificar (string, `^[a-zA-Z0-9]+$`, mínimo 3, máximo 20).

#### Respuesta

- 200: Indica disponibilidad. Ver clase `MemberIdAvailableOutput`.

### Información del usuario actual

    GET /api/v1/me

Autenticación: SÍ

#### Respuesta

- 200: Información del usuario actual. Ver clase `MeQueryOutput`.

### Actualizar foto de perfil

	PUT /api/v1/profile/photo

Autenticación: SÍ

#### Parámetros

- **photo**: foto de perfil (imagen PNG o JPEG, máximo 5 MB)

#### Respuesta

- 204: Foto de perfil actualizada. Sin cuerpo.
- 413: Foto demasiado grande. Sin cuerpo.
- 415: Formato de imagen no soportado. Sin cuerpo.

## Gestión de proyectos

### Crear un nuevo proyecto

	POST /api/v1/projects

Autenticación: SÍ

#### Parámetros

Clase `CreateProjectCommandInput`:

- **id**: identificador de proyecto (string, `^[a-zA-Z0-9]+$`, mínimo 3, máximo
  50, único)
- **name**: nombre del proyecto (string no vacía, máximo 50)
- **description**: descripción del proyecto (string, máximo 250)

El cliente DEBERÍA comprobar que el ID no está en uso antes de enviar una petición.
El cliente DEBERÍA generar un nombre a partir del ID si el usuario no lo proporciona.

#### Respuesta

- 201: Proyecto creado. Sin cuerpo.
- 400: Parámetros inválidos. Ver clase `ValidationExceptionAdvice`.
- 409: ID ya en uso. Sin cuerpo.