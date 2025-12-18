# Microservicios de Orden y Pago

Este proyecto es una aplicación basada en microservicios que simula un flujo de órdenes y pagos utilizando Java, Spring Boot, Kafka, PostgreSQL y Docker.

## Arquitectura

El sistema consta de dos microservicios:

*   **OrderMS**: Gestiona la creación y el estado de las órdenes. Expone una API REST para los clientes, produce mensajes en Kafka cuando se realiza una orden y persiste la información de la orden en una base de datos PostgreSQL.
*   **PaymentMS**: Simula el procesamiento de pagos. Consume los mensajes de las órdenes desde Kafka, procesa el pago y produce un mensaje con el estado del pago de vuelta a Kafka.

## Prerrequisitos

*   [Docker](https://www.docker.com/get-started)
*   [Docker Compose](https://docs.docker.com/compose/install/)
*   [Git](https://git-scm.com/downloads)
*   Java 17 o superior (si se desea construir los proyectos manualmente)
*   Maven (si se desea construir los proyectos manualmente)
*   [cURL](https://curl.se/download.html) o una herramienta como [Postman](https://www.postman.com/downloads/)

## Cómo Ejecutar

1.  **Clonar el repositorio:**
    ```bash
    git clone [repositorio Order-Payment-services](https://github.com/georgearevalo/Order-Payment-services.git)
    cd Order-Payment-services
    ```

2.  **Construir y ejecutar los servicios:**
    Usar Docker Compose para construir y ejecutar todos los servicios con un solo comando:
    ```bash
    docker-compose up --build
    ```
    Este comando hará lo siguiente:
    *   Construirá las imágenes de Docker para `OrderMS` y `PaymentMS`.
    *   Iniciará los contenedores para `OrderMS`, `PaymentMS`, `PostgreSQL`, `Kafka` y `Zookeeper`.
    *   Inicializará la base de datos `order_db` y creará la tabla `orders` usando el script `init.sql`.

## Cómo Probar

Puedes usar `cURL` para interactuar con la API REST de `OrderMS`.

1.  **Crear una nueva orden:**
    Enviar una solicitud POST al endpoint `/orders` con la información del producto y los datos de la tarjeta.

    ```bash
    curl --location 'http://localhost:8081/orders' \
    --header 'Content-Type: application/json' \
    --data '{
        "productInfo": "Laptop",
        "cardData": "1234-5678-9012-3456"
    }'
    ```
    La respuesta contendrá los detalles de la orden con el estado inicial "PENDIENTE". Anotar el `id` de la respuesta.

2.  **Verificar el estado inicial de la orden:**
    Enviar una solicitud GET al endpoint `/orders/{id}`, reemplazando `{id}` con el ID de la orden del paso anterior.

    ```bash
    curl --location 'http://localhost:8081/orders/<your-order-id>'
    ```
    El estado debería ser "PENDIENTE".

3.  **Observar los logs:**
    Puedes revisar los logs de los contenedores `paymentms` y `orderms` para ver el flujo de mensajes.
    ```bash
    docker-compose logs -f paymentms
    docker-compose logs -f orderms
    ```
    En los logs de `paymentms`, verás el mensaje "Processing payment for order...".
    En los logs de `orderms`, verás que el mensaje se consume desde el tema `payment-processed`.

4.  **Verificar el estado final de la orden:**
    Después de unos segundos, enviar nuevamente la solicitud GET para verificar el estado de la orden. Debería actualizarse a "PAGADO" o "FALLO_PAGO".
    ```bash
    curl --location 'http://localhost:8081/orders/<your-order-id>'
    ```

### Probar con Postman

Como alternativa a `cURL`, puedes usar la colección de Postman incluida en este proyecto para probar la API.

1.  **Importar la colección**:
    *   Abre Postman.
    *   Haz clic en `File > Import...`.
    *   Selecciona el archivo `Order-Payment-API.postman_collection.json` que se encuentra en la raíz del proyecto.
    *   O haz clic en el siguiente botón para importar la colección directamente:

    [![Run in Postman](https://run.pstmn.io/button.svg)](https://app.getpostman.com/run-collection/your-collection-id)
    > **Nota**: Deberás reemplazar `your-collection-id` con el ID real de tu colección después de subirla.

2.  **Ejecuta las solicitudes**:
    *   Una vez importada, verás la colección "Order-Payment API" en tu workspace.
    *   **Crear Orden**: Abre la solicitud `POST Create Order` y haz clic en `Send`. La información del producto y la tarjeta ya están pre-configuradas en el `Body`.
    *   **Consultar Orden**: Abre la solicitud `GET Get Order by ID`. Reemplaza `:order_id` en la URL con el `id` de la orden que creaste en el paso anterior y haz clic en `Send`.

    Puedes seguir los mismos pasos de la sección de `cURL` para observar los logs y verificar el cambio de estado de la orden.
