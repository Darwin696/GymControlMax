## UNIVERSIDAD NACIONAL DE LOJA

**Facultad de la Energía, las Industrias y los Recursos Naturales No Renovables**
**Carrera de Ingeniería en Sistemas**

---

# GymControl
### Sistema de gestión integral para gimnasios

**Asignatura:** Programación Orientada a Objetos

**Docente:** ING. Wilman Chamba

**Integrantes:**
- Darwin Campoverde
- Pablo Pineda
- Galo Benítez
- Erick Rogel
- Jahir Campoverde

**Periodo académico:** 2026

---

## 1. Descripción del proyecto

**GymControl** es una aplicación web que automatiza la administración de un gimnasio: registro de miembros, control de membresías, cobros, ventas de productos, gestión de instructores/empleados y generación de reportes. El proyecto se desarrolló como aplicación de los principios de la **Programación Orientada a Objetos (POO)** sobre una arquitectura Jakarta EE, utilizando persistencia de datos en PostgreSQL y aplicando el **patrón DAO** para separar el acceso a datos de la lógica de negocio.

## 2. Objetivos

### 2.1 Objetivo general
Desarrollar un sistema web orientado a objetos que permita administrar los procesos operativos de un gimnasio (miembros, membresías, pagos, productos, instructores, empleados y reportes), aplicando los pilares de la POO: **abstracción, encapsulamiento, herencia y polimorfismo**.

### 2.2 Objetivos específicos
- Modelar el dominio del negocio mediante clases, jerarquías de herencia e interfaces.
- Implementar un módulo de autenticación para el acceso al sistema.
- Aplicar polimorfismo en el cálculo de vigencia de los distintos tipos de membresía.
- Persistir la información del sistema en una base de datos relacional (PostgreSQL) mediante JPA.
- Separar el acceso a datos de la lógica de negocio mediante el **patrón DAO**.
- Diseñar una interfaz web funcional con JSF y PrimeFaces.
- Validar las reglas de negocio críticas mediante pruebas unitarias (JUnit 5).

## 3. Justificación

La administración manual de un gimnasio (control de pagos, vencimiento de membresías, inventario de productos) es propensa a errores y pérdida de información. GymControl centraliza estos procesos en un sistema único, y sirve además como caso de estudio práctico de los conceptos de POO vistos en la asignatura: clases abstractas, herencia, sobrescritura de métodos (`@Override`), relaciones entre objetos persistentes y separación de responsabilidades mediante patrones de diseño (DAO).

## 4. Alcance / Módulos funcionales

| Módulo | Descripción |
|---|---|
| **Autenticación** | Inicio de sesión del administrador del sistema |
| **Dashboard** | Vista general del estado del gimnasio |
| **Miembros** | Registro, edición, consulta y baja de clientes (CRUD) |
| **Planes de membresía** | Gestión de membresías Mensual, Anual y VIP (CRUD) |
| **Pagos / Facturas** | Registro de cobros generados por membresías o productos |
| **Productos / Tienda** | Control de stock y ventas que generan factura |
| **Instructores** | Gestión del personal técnico del gimnasio (CRUD) |
| **Empleados** | Gestión del personal administrativo (recepción, etc.) que no es instructor (CRUD) |
| **Reportes** | Informes generales de la operación del gimnasio (ingresos, métodos de pago) |

## 5. Tecnologías utilizadas

| Componente | Tecnología |
|---|---|
| Lenguaje | Java 21 |
| Framework backend | Jakarta EE 10 (Web Profile) |
| Framework de vistas | JSF (Jakarta Faces) + PrimeFaces 15 |
| Persistencia | Jakarta Persistence (JPA) — implementación EclipseLink |
| Base de datos | PostgreSQL |
| Servidor de aplicaciones | Open Liberty |
| Gestor de dependencias | Maven |
| Pruebas unitarias | JUnit 5 (Jupiter) |
| Contenerización | Docker / Docker Compose |
| Administración de BD | Adminer |

## 6. Aplicación de los principios de la POO

El proyecto evidencia los cuatro pilares de la Programación Orientada a Objetos directamente en el modelo de dominio (`unl.edu.ec.gymcontrol.domain`):

**Abstracción**
- `Persona` y `Membresia` son clases **abstractas** que definen atributos y comportamientos comunes, sin poder instanciarse directamente.

**Encapsulamiento**
- Todos los atributos de las entidades son `private`, expuestos únicamente mediante métodos `get`/`set`, protegiendo la integridad de los datos.

**Herencia**
- `Cliente` y `Empleado` heredan de `Persona`.
- `Instructor` hereda de `Empleado` (herencia de dos niveles).
- `MembresiaMensual`, `MembresiaAnual` y `MembresiaVIP` heredan de la clase abstracta `Membresia`.
- Estrategia de mapeo JPA: `Persona` usa `@Inheritance(JOINED)` con `@DiscriminatorColumn(name="dtype")`; `Membresia` usa `@Inheritance(SINGLE_TABLE)` con discriminador `tipo_membresia`.

**Polimorfismo**
- La clase `Membresia` declara el método abstracto `calcularFechaVencimiento()`, y cada subclase lo **sobrescribe** (`@Override`) con su propia regla de negocio:
  - `MembresiaMensual` → vence en 1 mes.
  - `MembresiaAnual` → vence en 1 año.
  - `MembresiaVIP` → vence en 1 año (con beneficios adicionales de negocio).
- Al invocar `membresia.calcularFechaVencimiento()` sobre una referencia de tipo `Membresia`, el sistema ejecuta la implementación específica de la subclase real en tiempo de ejecución.
- `EmpleadoDAO` usa `TYPE(e) = Empleado` en su consulta JPQL para excluir a `Instructor` (que también ES-UN `Empleado` polimórficamente) del listado de personal administrativo.

### 6.1 Diagrama de herencia (simplificado)

```
Persona (abstracta)
 ├── Cliente
 └── Empleado
      └── Instructor

Membresia (abstracta)
 ├── MembresiaMensual
 ├── MembresiaAnual
 └── MembresiaVIP
```

## 7. Arquitectura del sistema

```
gymControl/
├── src/main/java/unl/edu/ec/gymcontrol/
│   ├── bean/       # LoginBean, VistaGymBean → controladores JSF (managed beans)
│   ├── domain/     # Entidades del negocio (modelo POO)
│   ├── dao/        # GenericDAO<T,ID> + ClienteDAO, ProductoDAO, InstructorDAO,
│   │               # EmpleadoDAO, PagoDAO → acceso a datos (patrón DAO)
│   └── service/    # GymService → transacciones y reglas de negocio
├── src/test/java/unl/edu/ec/gymcontrol/domain/   # Pruebas unitarias JUnit 5
├── src/main/resources/META-INF/persistence.xml   # unidad de persistencia (JPA)
├── src/main/liberty/config/server.xml             # servidor, puerto y datasource
├── src/main/webapp/                                # vistas .xhtml (JSF + PrimeFaces)
├── db/init.sql                                      # script de esquema + datos de prueba
├── Dockerfile
├── docker-compose-dev-pg.yml                       # PostgreSQL para desarrollo
└── pom.xml
```

La aplicación sigue una separación de capas típica de Jakarta EE:
- **Capa de presentación:** vistas `.xhtml` (JSF/PrimeFaces).
- **Capa de control:** *managed beans* (`bean/`).
- **Capa de negocio / transacciones:** `GymService` — decide **cuándo** se abre/cierra una transacción (`UserTransaction`) y orquesta las llamadas a los DAO.
- **Capa de acceso a datos (patrón DAO):** cada entidad tiene su propio DAO (`ClienteDAO`, `ProductoDAO`, `InstructorDAO`, `EmpleadoDAO`, `PagoDAO`) que implementa la interfaz genérica `GenericDAO<T, ID>` y encapsula el `EntityManager` — el service **nunca** llama a `em.persist()/em.remove()/createQuery()` directamente.
- **Capa de persistencia:** entidades JPA (`domain/`) mapeadas a PostgreSQL.

### 7.1 Patrón DAO

```java
public interface GenericDAO<T, ID> {
    List<T> listarTodos();
    T buscarPorId(ID id);
    void guardar(T entity);
    void actualizar(T entity);
    void eliminar(ID id);
}
```

Cada DAO concreto (ej. `ClienteDAO`) implementa este contrato para una entidad específica. `GymService` los inyecta con `@Inject` y los usa dentro de sus transacciones:

```java
@Inject private ClienteDAO clienteDAO;

public void guardarCliente(Cliente c) {
    tx(() -> clienteDAO.guardar(c));   // GymService decide la transacción,
}                                       // ClienteDAO solo sabe persistir
```

## 8. Manual de instalación y ejecución

### 8.1 Requisitos previos
- JDK 21
- Maven (o el wrapper incluido `./mvnw` / `mvnw.cmd`)
- Docker y Docker Compose

### 8.2 Levantar la base de datos

```bash
cd gymControl
docker compose -f docker-compose-dev-pg.yml up -d
```

Esto crea la base `gym` en PostgreSQL (usuario `gymuser`, puerto `5435`), acorde a la configuración del `server.xml`. El archivo `db/init.sql` se monta como script de inicialización (`docker-entrypoint-initdb.d`) y se ejecuta **automáticamente la primera vez** que se crea el volumen: crea el esquema completo y siembra datos de prueba (2 clientes, 1 instructor, 1 empleado, membresías, productos y pagos).

> Si se necesita re-sembrar los datos desde cero, borra el volumen antes de levantar de nuevo:
> ```bash
> docker compose -f docker-compose-dev-pg.yml down -v
> docker compose -f docker-compose-dev-pg.yml up -d
> ```

### 8.3 Compilar y ejecutar la aplicación

**Windows (PowerShell)**
```powershell
cd gymControl
.\mvnw.cmd clean package liberty:run
```

**Linux 
```bash
cd gymControl
./mvnw clean package liberty:run
```

### 8.4 Ejecutar las pruebas unitarias

```powershell
.\mvnw.cmd test
```

Cubre el polimorfismo de `Membresia` (cálculo de vigencia por subclase), la relación bidireccional `Cliente`–`Membresia`, y la jerarquía de herencia `Persona → Empleado → Instructor`.

### 8.5 Acceder al sistema
Abrimos el navegador en: **http://localhost:9080**

Credenciales de acceso:
- **Usuario:** `admin`
- **Contraseña:** `admin123`

## 9. Conclusiones

- El desarrollo de GymControl permitió aplicar de forma práctica los cuatro pilares de la POO sobre un caso de negocio real (gestión de un gimnasio).
- El uso de clases abstractas (`Persona`, `Membresia`) facilitó la reutilización de código y estableció un contrato claro para las subclases.
- El polimorfismo en `calcularFechaVencimiento()` demostró cómo un mismo mensaje enviado a distintos objetos produce comportamientos diferentes, sin necesidad de estructuras condicionales explícitas.
- El uso de Jakarta EE y JPA permitió mapear directamente el modelo de objetos al modelo relacional, reforzando la relación entre POO y persistencia de datos.
- La separación explícita en capas DAO/Service clarificó las responsabilidades: el DAO solo sabe *cómo* guardar un dato, el Service decide *cuándo* y *con qué transacción* hacerlo.
- Las pruebas unitarias permitieron validar de forma automática el comportamiento polimórfico de las membresías, detectando errores antes de la demostración en clase.

## 10. Recomendaciones

- Ampliar la cobertura de pruebas unitarias a la capa DAO usando una base de datos en memoria (H2) o Testcontainers.
- Agregar roles de usuario adicionales (recepcionista, instructor) más allá del administrador único actual, aprovechando ya la entidad `Empleado`.
- Implementar notificaciones automáticas de vencimiento de membresía.
- Declarar explícitamente `@Column(name="...")` en todos los campos de las entidades para no depender de la convención de nombres por defecto del proveedor JPA (ver sección 11).

## 11. Nota técnica: nombres de columna explícitos

Durante el desarrollo se detectó que, al no declarar `@Column(name="...")` explícito, el proveedor JPA (EclipseLink) genera nombres de columna distintos a los usados en `db/init.sql` para atributos de varias palabras (ej. `fechaRegistro` → `fecharegistro` en vez de `fecha_registro`). Por eso, todos los atributos compuestos del modelo (`Cliente.fechaRegistro`, `Membresia.fechaInicio/fechaVencimiento`, `Pago.nombreCliente`, `Instructor.imageUrl`) declaran su columna explícitamente. De igual forma, `Persona` declara `@DiscriminatorColumn(name="dtype")` y cada subclase su `@DiscriminatorValue` (`CLIENTE`, `EMPLEADO`, `INSTRUCTOR`) para que la herencia `JOINED` funcione de forma predecible independientemente del proveedor JPA usado.

## 12. Referencias

- Oracle. *Jakarta EE 10 Platform Specification.*
- PrimeFaces. *PrimeFaces 15 User Guide.*
- Open Liberty. *Open Liberty Documentation.*
- PostgreSQL Global Development Group. *PostgreSQL 16 Documentation.*
- JUnit Team. *JUnit 5 User Guide.*