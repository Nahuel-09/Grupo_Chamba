
---

# Sistema de Gestión de Mercaderías entre Sucursales

> **Curso/Semestre:** 3º / 5º Semestre | **Materia:** Lenguaje de Programación III
> **Fecha de Entrega:** 05/03/2026

Este repositorio contiene el código fuente, la infraestructura de despliegue y el registro de control de trabajo del **Sistema de Gestión de Mercaderías entre Sucursales**, un proyecto de grado enfocado en la trazabilidad, control de stock y distribución logística inter-sucursales.

---

## 📊 1. Distribución de Roles y Responsabilidades

El desarrollo y la documentación del sistema se estructuraron bajo una estricta división de roles y un calendario de hitos internos:

| Nro | Integrante | Rol Asignado | Temas Principales a Desarrollar | Tareas Específicas | Fecha Límite Interna |
| --- | --- | --- | --- | --- | --- |
| **1** | **Nahuel López** | Desarrollador, Diseñador | Manejo del Front y Back, despliegue en Docker, siguiendo lo establecido en la documentación de sus compañeros. | Coordinar las etapas dentro de la elaboración del documento. | 24/06/2026 |
| **2** | **Soledad Caballero** | Documentadora de Despliegue y Arquitectura | Redacción de la arquitectura utilizada, la secuencia de despliegue y sus recursos Docker. | Elaborar un informe de despliegue y arquitectura. | 25/06/2026 |
| **3** | **Angela Martinez** | Documentadora de Flujos y Estados | Redacción del flujo del sistema, tanto así como sus maneras de actuar ante anomalías. | Elaborar un informe de flujos y estados de activación. | 26/06/2026 |
| **4** | **Marcelo Britos** | Documentadora de Modelos y Casos de Uso | Redacción del esquema actual del sistema, como también el manejo de quién puede ver ciertos recursos. | Elaborar un informe de estructura y manejo de recursos. | 27/06/2026 |
| **5** | **Jazmin Fernandez** | Documentadora de Datos y Organizadora Técnica | Redacción del esquema de base de datos y la estructura técnica del directorio del sistema. | Elaborar un informe de la estructura de datos y la estructura técnica de los directorios del sistema. | 27/06/2026 |

---

## 📝 Estructura de la Documentación Final

El proyecto cuenta con dos secciones fundamentales de control y defensa técnica integradas en los entregables:

### 2. Informe Individual Paso a Paso

Cada integrante del equipo dispone de un apartado detallado en la documentación que explica de forma exhaustiva los siguientes puntos de su intervención en el proyecto:

* **Desarrollo Técnico:** Qué componentes o módulos específicos se desarrollaron.
* **Arquitectura de Software:** Cómo quedó organizada la aplicación final.
* **Interfaz de Usuario (UI):** Cómo se rediseñó el menú principal del sistema.
* **Frontend Reutilizable:** Cómo se aplicó la plantilla base haciendo uso de fragmentos reutilizables de *Thymeleaf*.
* **Seguridad y Acceso:** Qué roles y permisos de usuario se implementaron para resguardar los recursos del sistema.
* **Módulo de Negocio:** Cómo se completó el módulo crítico de *Venta*.
* **Reportes y Documentos:** Cómo se genera la factura o comprobante en formato PDF, así como el proceso para el cálculo y generación del resumen mensual por producto.
* **Infraestructura:** Cómo se preparó el entorno virtualizado y de contenedores con *Docker*.
* **Gestión de Riesgos:** Qué dificultades técnicas aparecieron durante el proceso y las estrategias empleadas para resolverlas.
* **Evaluación de Impacto:** Qué aportes de valor específicos realizó el estudiante al ecosistema del proyecto.

### 3. Planilla de División de Tareas

Matriz detallada para la auditoría del proyecto que asocia la fuerza de trabajo con los objetivos del software:

* Consolidado de integrantes y el rol asumido por cada uno en el ciclo de vida del desarrollo.
* Registro pormenorizado de las tareas efectivamente realizadas.
* Alineación y relación directa de esas tareas con el objetivo general del trabajo.
* Justificación de las decisiones técnicas defendidas frente a la mesa examinadora.
* Una reflexión breve e individual de cada integrante sobre el aprendizaje y la ejecución del proyecto.

---

## 🛠️ Tecnologías Utilizadas

* **Backend:** Java / Spring Boot
* **Frontend:** Thymeleaf / HTML5 / CSS3 / JavaScript
* **Contenedores e Infraestructura:** Docker / Docker Compose
* **Base de Datos:** Motor Relacional (PostgreSQL / MySQL)

---
