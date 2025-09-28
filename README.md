# Proyecto Gestion de Turnos de Hospital

## Requisitos Previos Para su ejecución
1. Tener la versión de **Oracle JDK 11** (Especificamente esa para evitar problemas de compatibilidad)
2. Netbeans IDE 21 u Eclipse IDE

## Instrucciones de Instalación y Ejecución
### Utilizando NetBeans IDE 21
1. Ejecutar el Programa NetBeans IDE 21
2. Seleccionar *Team > Git > Clone* insertar el link del repositorio (Este lo puedes obtener donde dice CODE arriba de los archivos que contienen este proyecto).
3. Selecciona la carpeta donde desees clonar este repositorio y abre el proyecto
4. Haz click derecho sobre el archivo VentanaPrincipal.java y selecciona **Run File**, sino Tambien puedes darle en el símbolo de play de color verde que aparece arriba en NetBeans.

### Utilizando Eclipse IDE
1. Ejecutar el Programa Eclipse IDE
2. Selecciona File > Import > Git > Project From Git > Clone URL ingresar el link de este repositorio que puedes obtenerlo donde se mencionó anteriormente y los datos solicitados.
3. Haz clic en Next.
4. Selecciona la carpeta donde desees clonar este repositorio y abre el proyecto.
5. Haz clic derecho sobre el proyecto importado y selecciona Run As > Java Application.


### Uso de esta

- Luego de la ejecución nos aparecerá una ventana tal que asi
<img width="384" height="360" alt="image" src="https://github.com/user-attachments/assets/8018f286-01a5-47e9-acc5-149d963e1f0e" />

Donde deberan seleccionar importar datos para luego, ingresar los csv's que ya vendran en el archivo.zip de este, solamente deberan buscarlo en su repertorio donde hayan guardado el programa y **seleccionar la carpeta Csv's**. Así 
podrán utilizar nuestro programa de manera correcta.

### Observaciones

- Al momento de buscar a las enfermeras, el formato de busqueda de rut será por ej: 1.234.567-8, así el programa los encontrará eficazmente, sino les aparecerá un mensaje de error.
- Además el programa funciona con el formato fecha **YYYY-MM-DD**. **Las fechas de las disponibilidades de las enfermeras empiezan desde 2025-09-29 SINO NO ENCONTRARÁN NINGUNA ENFERMERA**.
- Una funcionalidad les retornará un archivo .txt que se guardará en los archivos de este programa, para su simple acceso.

