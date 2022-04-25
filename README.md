# pochoclito
App para ver las peliculas o series mas populares

Features
- Listado de peliculas ordenadas por popularidad
- Listado de series de tv ordenadas por popularidad
- Buscador por titulo
- Pagina de detalle de pelicuas o series
- Reproduccion de video en el detalle
- Funciona offline con los datos que ya se consultaron

Tech stack
- Clean architecture con MVVM
- Kotlin flow y coroutinas
- Hilt para DI
- Jetpack
    - Compose (con Navigation)
    - Lifecycle Viewmodels
    - Paging
    - Room

Como correr la aplicacion:
- Agregar en el archivo 'local.properties' la siguiente linea de configuracion: api.access.token="<API_TOKEN>"
  donde <API_TOKEN> es la clave obtenida al registrarse a TMDB
- Simplemente compilar y ejecutar la app.

Como correr los tests:
- Unitarios: ejecutar el comando gradle /testDebudUnitTest
- Integracion: ejecutar los tests en los packages "androidTest" de los modulos 'app' y 'data'

Mejoras pendientes
- No soporta contenido apaisado 
- No hay lint customizado
- Database migration (auto migration)
- Agregar una libreria de logs y reporte de errores
- Se probo la libreria de YouTube oficial, pero tiene varios bugs y no va bien con Compose
- Agregar Styles in compose
- Carga de red luego que detecta conexion
- Pull to refresh
