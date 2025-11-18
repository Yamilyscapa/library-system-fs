# Biblioteca - Sistema de Gestión de Biblioteca

## Arquitectura

El sistema sigue un diseño orientado a objetos con una clara separación de responsabilidades:

- **Capa de Entidades**: Modelos de dominio principales (`Book`, `Person`, `Loan`)
- **Capa de Servicios**: Lógica de negocio (`LibraryService`)
- **Capa de Almacenamiento**: Persistencia de datos (`FileStorage`)
- **Capa de Presentación**: Interfaz de usuario (`Main`)

### Jerarquía de Clases

```
SerializableEntity (interfaz)
├── Book
├── Person (abstracta)
│   ├── Student
│   └── Professor
└── Loan

Borrower (interfaz)
└── Person (implementa)
```

## Características

- Registro y gestión de libros
- Registro de personas (Estudiantes y Profesores)
- Gestión de préstamos con cálculo automático de fecha de vencimiento (14 días)
- Procesamiento de devoluciones
- Aplicación de límites de préstamos (Estudiantes: 3, Profesores: 5)
- Persistencia de datos en archivos CSV
- Validación de correo electrónico
- Seguimiento de disponibilidad de libros


El sistema creará una carpeta `data` en el directorio de trabajo actual para almacenar los archivos CSV.

## Documentación de Clases

### Clases Principales

#### `Main`
El punto de entrada de la aplicación. Proporciona una interfaz de menú de línea de comandos para interactuar con el sistema de biblioteca.

**Métodos Principales:**
- `main(String[] args)` - Punto de entrada de la aplicación
- `printMenu()` - Muestra el menú principal
- `addBook(Scanner, LibraryService)` - Maneja el registro de libros
- `registerPerson(Scanner, LibraryService)` - Maneja el registro de personas
- `registerLoan(Scanner, LibraryService)` - Maneja la creación de préstamos
- `registerReturn(Scanner, LibraryService)` - Maneja las devoluciones de libros

#### `LibraryService`
Clase de servicio central que gestiona todas las operaciones de la biblioteca y coordina entre entidades.

**Métodos Principales:**
- `load()` - Carga todos los datos desde archivos CSV
- `save()` - Guarda todos los datos en archivos CSV
- `addBook(String title, String author)` - Crea y registra un nuevo libro
- `addStudent(String name, String email)` - Crea y registra un nuevo estudiante
- `addProfessor(String name, String email)` - Crea y registra un nuevo profesor
- `loanBook(String bookId, String borrowerId)` - Crea un préstamo si las validaciones pasan
- `returnBook(String bookId)` - Marca un préstamo como devuelto y hace el libro disponible
- `findBook(String id)` - Busca un libro por ID
- `findPerson(String id)` - Busca una persona por ID
- `listBooks()` - Retorna todos los libros
- `listPeople()` - Retorna todas las personas
- `listActiveLoans()` - Retorna todos los préstamos activos
- `getAllLoans()` - Retorna todos los préstamos (activos y completados)
- `anyPersonByEmail(String email)` - Verifica si existe una persona con ese correo

**Reglas de Negocio:**
- Los libros deben estar disponibles para ser prestados
- Los prestatarios no pueden exceder su límite máximo de préstamos activos
- Los estudiantes pueden tener hasta 3 préstamos activos
- Los profesores pueden tener hasta 5 préstamos activos
- La duración del préstamo es de 14 días desde la fecha del préstamo
- Las direcciones de correo electrónico deben ser únicas entre todas las personas

#### `Book`
Representa un libro en el sistema de biblioteca.

**Campos:**
- `id` (String) - Identificador único (formato: `BOOK-XXXXXXXX`)
- `title` (String) - Título del libro
- `author` (String) - Autor del libro
- `available` (boolean) - Estado de disponibilidad

**Métodos Principales:**
- `getId()` - Retorna el ID del libro
- `getTitle()` - Retorna el título del libro
- `getAuthor()` - Retorna el autor del libro
- `isAvailable()` - Verifica si el libro está disponible
- `setAvailable(boolean)` - Establece el estado de disponibilidad
- `toRecord()` - Serializa el libro a formato CSV
- `fromRecord(String)` - Deserializa un libro desde formato CSV
- `toString()` - Retorna una representación en cadena formateada

#### `Person` (Abstracta)
Clase base para todas las personas en el sistema. Implementa las interfaces `SerializableEntity` y `Borrower`.

**Campos:**
- `id` (String) - Identificador único
- `name` (String) - Nombre de la persona
- `email` (String) - Dirección de correo electrónico de la persona

**Métodos Principales:**
- `getId()` - Retorna el ID de la persona
- `getName()` - Retorna el nombre de la persona
- `getEmail()` - Retorna el correo electrónico de la persona
- `getType()` - Método abstracto para retornar el tipo de persona
- `maxActiveLoans()` - Método abstracto para retornar el máximo de préstamos activos
- `toRecord()` - Serializa la persona a formato CSV
- `fromRecord(String)` - Deserializa una persona desde formato CSV
- `toString()` - Retorna una representación en cadena formateada

#### `Student`
Representa un prestatario estudiante. Extiende `Person`.

**Características Principales:**
- Préstamos activos máximos: **3**
- Identificador de tipo: `STUDENT`
- Prefijo de ID: `STU-`

#### `Professor`
Representa un prestatario profesor. Extiende `Person`.

**Características Principales:**
- Préstamos activos máximos: **5**
- Identificador de tipo: `PROFESSOR`
- Prefijo de ID: `PRO-`

#### `Loan`
Representa una transacción de préstamo entre un prestatario y un libro.

**Campos:**
- `id` (String) - Identificador único (formato: `LOAN-XXXXXXXX`)
- `bookId` (String) - Referencia al libro prestado
- `borrowerId` (String) - Referencia al prestatario
- `loanDate` (LocalDate) - Fecha en que se creó el préstamo
- `dueDate` (LocalDate) - Fecha en que el libro debe ser devuelto
- `returned` (boolean) - Estado de devolución

**Métodos Principales:**
- `getId()` - Retorna el ID del préstamo
- `getBookId()` - Retorna el ID del libro
- `getBorrowerId()` - Retorna el ID del prestatario
- `getLoanDate()` - Retorna la fecha del préstamo
- `getDueDate()` - Retorna la fecha de vencimiento
- `isReturned()` - Verifica si el préstamo ha sido devuelto
- `isActive()` - Verifica si el préstamo está actualmente activo
- `markReturned()` - Marca el préstamo como devuelto
- `toRecord()` - Serializa el préstamo a formato CSV
- `fromRecord(String)` - Deserializa un préstamo desde formato CSV

#### `FileStorage<T>`
Clase utilitaria genérica para leer y escribir entidades en archivos CSV.

**Parámetros de Tipo:**
- `T` - Debe extender `SerializableEntity`

**Métodos Principales:**
- `loadAll()` - Carga todas las entidades desde el archivo CSV
- `saveAll(Collection<T>)` - Guarda todas las entidades en el archivo CSV
- `ensureDirectory()` - Crea la estructura de directorios si no existe

**Características:**
- Creación automática de directorios
- Manejo de errores para operaciones de I/O
- Omite registros inválidos durante la carga

### Interfaces

#### `SerializableEntity`
Interfaz para entidades que pueden ser serializadas a y desde formato CSV.

**Métodos:**
- `toRecord()` - Convierte la entidad a una cadena de registro CSV

#### `Borrower`
Interfaz para entidades que pueden tomar libros en préstamo.

**Métodos:**
- `getId()` - Retorna el ID del prestatario
- `getName()` - Retorna el nombre del prestatario
- `maxActiveLoans()` - Retorna el número máximo de préstamos activos permitidos

## Generación de IDs

El sistema utiliza generación de IDs basada en UUID con prefijos:

- Libros: `BOOK-XXXXXXXX` (uso de UUID)
- Estudiantes: `STU-XXXXXXXX`
- Profesores: `PRO-XXXXXXXX`
- Préstamos: `LOAN-XXXXXXXX`

## Reglas de Validación

1. **Registro de Libros:**
   - El título y el autor son obligatorios (no pueden estar vacíos)

2. **Registro de Personas:**
   - El nombre y el correo electrónico son obligatorios (no pueden estar vacíos)
   - El correo electrónico debe ser único entre todas las personas

3. **Creación de Préstamos:**
   - El libro debe existir y estar disponible
   - El prestatario debe existir
   - El prestatario no debe exceder el límite máximo de préstamos activos

4. **Procesamiento de Devoluciones:**
   - El libro debe tener un préstamo activo

## Manejo de Errores

El sistema maneja los errores de manera elegante:

- Las opciones de menú inválidas muestran un mensaje de error
- Los datos faltantes o inválidos muestran mensajes de error apropiados
- Los errores de E/S de archivos se registran en stderr pero no cierran la aplicación
- Los registros CSV inválidos se omiten durante la carga

## Ejemplo de Sesión

```
===== Biblioteca =====
1. Registrar libro
2. Registrar persona
3. Listar libros
4. Listar personas
5. Registrar préstamo
6. Registrar devolución
7. Listar préstamos
8. Guardar y salir
Seleccione una opción: 1

Título del libro: El Gran Gatsby
Autor: F. Scott Fitzgerald
Libro registrado con ID BOOK-ABC12345

===== Biblioteca =====
...
Seleccione una opción: 2

Tipo (1-Estudiante, 2-Profesor): 1
Nombre: Juan Pérez
Correo: juan@ejemplo.com
Persona registrada con ID STU-XYZ67890

===== Biblioteca =====
...
Seleccione una opción: 5

ID del libro: BOOK-ABC12345
ID de la persona: STU-XYZ67890
Préstamo registrado con ID LOAN-DEF45678
```

## Persistencia de Datos

Los datos se guardan automáticamente cuando:
- Se registra un libro
- Se registra una persona
- Se crea un préstamo
- Se devuelve un libro
- El usuario selecciona la opción 8 (Guardar y salir)