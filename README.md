
# **OdontoClinick – Plataforma Odontológica (JSF + MySQL + GlassFish 4.1.2)**

Bienvenido a **OdontoClinick**, un sistema clínico desarrollado con **JavaServer Faces (JSF)**, usando **GlassFish 4.1.2** como servidor de aplicaciones y **MySQL** como base de datos relacional.
El proyecto está diseñado para que cualquier miembro del equipo pueda ejecutarlo de forma rápida, sin configuración manual compleja.

---

## **Características principales**

*  Backend en **JavaServer Faces (JSF 2.x)**
*  Diseño MVC con Beans, DAOs y Services
*  Conexión administrada mediante **JDBC Resources (JNDI)**
*  **GlassFish 4.1.2** totalmente preconfigurado
*  Compatible con **MySQL 8+**
*  Proyecto organizado, modular y escalable

---

# **Requisitos previos**

Antes de ejecutar este proyecto, necesitas:

### Software necesario

| Herramienta                      | Versión recomendada                  |
| -------------------------------- | ------------------------------------ |
| **JDK**                          | 8 (obligatorio para GlassFish 4.1.2) |
| **GlassFish**                    | 4.1.2                                |
| **NetBeans**                     | Cualquiera (Uso nuevo 28)            |
| **MySQL Server**                 | 5.7 o 8.0                            |
| **MySQL Workbench / PhpMyAdmin** | Cualquiera                           |

---

# **Instalación y configuración**

Este repositorio **ya incluye un dominio de GlassFish preconfigurado**, por lo que **NO necesitas** crear pools, datasources, ni instalar drivers.

Solo sigue estos pasos 

---

## Clonar el repositorio

```bash
git clone https://github.com/TU_USUARIO/odontoclinick.git
```

---

##  Configurar GlassFish (IMPORTANTE)

### Paso A — Ubicar la carpeta `glassfish-domain` del repositorio

Dentro del proyecto encontrarás:

```
/glassfish-domain
```

Este dominio ya tiene configurado:

 JDBC Connection Pool
 JDBC Resource
 Driver MySQL (mysql-connector.jar)
 Usuario y credenciales
 JNDI: `jdbc/odontoclinick`

---

### Paso B — Instalar el dominio en GlassFish

1. Ir a la instalación de GlassFish:

```
GLASSFISH_HOME/glassfish/domains/
```

2. Borrar el dominio por defecto:

```
domain1
```

3. Copiar la carpeta del repositorio:

```
domain1
```

4. ubicarla donde se encontraba:

```
C:\Users\mynde\GlassFish_Server\glassfish\domains\(ubicar la carpeta domain1)
```

---

## Importar el proyecto en NetBeans

1. Abrir NetBeans
2. Archivo → Abrir proyecto
3. Seleccionar la carpeta:

```
/proyecto-jsf
```

4. Elegir el servidor **GlassFish 4.1.2**
5. Ejecutar

---

## Importar la base de datos

El archivo SQL está ubicado en:

```
/database/odontoclinick.sql
```

Solo debes ejecutarlo en Workbench o PhpMyAdmin.

---

# **Arquitectura del proyecto**

El proyecto está organizado por capas:

```
/src
   /beans      → ManagedBeans JSF
   /dao             → Acceso a datos
   /modelos           → Entidades Java
/web
   /views           → Vistas XHTML
```

---

# **Conexión a base de datos (GlassFish)**

El acceso a MySQL se realiza mediante **JNDI**, ya configurado en el dominio:

### JNDI Resource

```
jdbc/odontoclinick
```

### Uso en DAOs

```java
@Resource(lookup = "jdbc/odontoclinick")
private DataSource ds;

public Connection getConnection() throws SQLException {
    return ds.getConnection();
}
```

---

# **Ejecución**

Una vez configurado el dominio:

 Iniciar GlassFish desde NetBeans
 Cargar el proyecto
 Ejecutar con botón Run

El sistema se iniciará en:

```
http://localhost:8080/
```

---

# **Contribución**

1. Crear una nueva rama:

```bash
git checkout -b feature/nueva-funcion
```

2. Subir cambios:

```bash
git commit -m "Agrego nueva funcionalidad"
git push origin feature/nueva-funcion
```

3. Crear Pull Request

---

# **Licencia**

Este proyecto es para fines académicos y colaborativos.
Todos los colaboradores del equipo OdontoClinick pueden modificarlo libremente.

---

# **OdontoClinick — Tu clínica digital con JSF**

Desarrollado por el equipo: Scrum Odontoclinick
💙 ¡Gracias por contribuir al proyecto!


Solo dime y lo embellezco como un framework profesional.
