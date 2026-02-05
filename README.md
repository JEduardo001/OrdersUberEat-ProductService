Product Service
Microservicio responsable de la gestión del catálogo de productos y el control de inventario asíncrono. Actúa como validador de stock dentro del flujo de creación de pedidos.

Stack Tecnológico
Java: 21

Framework: Spring Boot 4.0.1

Base de Datos: PostgreSQL

Mensajería: Apache Kafka (Event-Driven)

Service Discovery: Netflix Eureka

API Endpoints
Base Path: /api/product

GET /: Obtiene el catálogo completo de productos con paginación.

GET /{idProduct}: Obtiene la información detallada de un producto por UUID.

POST /: Registra un nuevo producto en el catálogo.

PUT /: Actualiza la información técnica o de stock de un producto existente.

Orquestación de Inventario (Kafka)
Este servicio participa críticamente en la coreografía de pedidos para asegurar la consistencia del stock.

Eventos Consumidos
order.created.pending: Activa la verificación de stock para un pedido recién creado.

changed.status.order.failed: Gatilla la reversión del stock (rollback) en caso de que el flujo del pedido falle en pasos posteriores.

Eventos Producidos
inventory.stock.reserved: Notifica que el stock fue descontado exitosamente.

inventory.stock.reserved.failed: Notifica que no hay existencias suficientes para procesar el pedido.

failed.send.event.dlq: Almacena eventos que no pudieron ser publicados correctamente.

Patrones de Resiliencia y Consistencia
Transactional Outbox: Garantiza que la actualización del stock en PostgreSQL y la notificación a Kafka ocurran de forma atómica.

Idempotencia: Implementada mediante ProcessedEventService para evitar procesar dos veces el mismo descuento de inventario.

Trazabilidad: Persistencia del correlationId para mantener el hilo de ejecución desde el Order Service.

Configuración de Infraestructura
Puerto: 5013

Base de Datos: ProductServiceOrderUberEatsDB

Instancia: Registrado en clúster Eureka para balanceo de carga.
