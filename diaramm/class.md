```mermaid
classDiagram
    direction LR

    %% -------- Packages (Subgraphs) --------
    subgraph techstore_model ["techstore.model"]
        direction TB
        class User {
            <<Abstract>>
            #id: int
            #username: String
            #password: String
            #email: String
            +User(username, password, email)
            +getId(): int
            +getRole()$ String
        }

        class Admin {
            +Admin(username, password, email)
            +getRole(): String
        }
        User <|-- Admin

        class Client {
            -shoppingCart: ShoppingCart
            +Client(username, password, email)
            +getRole(): String
            +getShoppingCart(): ShoppingCart
        }
        User <|-- Client

        class Product {
            %% Decorator: ConcreteComponent
            %% Builder: Built by ProductBuilder
            -id: int
            -name: String
            -price: double
            -description: String
            +Product(id, name, description, price, category, brand, warrantyMonths, discount)
            +getPrice(): double
            +getName(): String
            +getDescription(): String
            +getId(): int
        }
        Product --|> patterns_decorator_ProductComponent  %% Link to class defined in another subgraph

        class Category {
            -id: int
            -name: String
            +Category(name)
        }

        class CartItem {
            -product: patterns_decorator_ProductComponent  %% Link to class defined in another subgraph
            -quantity: int
            +CartItem(product, quantity)
        }

        class ShoppingCart {
            %% Iterator: Implicit via List
            -client: Client
            -items: List~CartItem~
            +ShoppingCart(client)
            +addProduct(product, quantity)
            +getItems(): List~CartItem~
        }

        class Order {
            %% Strategy: Context
            -id: int
            -client: Client
            -paymentStrategy: patterns_strategy_PaymentStrategy %% Link to class defined in another subgraph
            +Order(client, orderedItems, totalAmount)
            +setPaymentStrategy(strategy)
            +processPayment(): boolean
        }
    end

    subgraph techstore_service ["techstore.service"]
        direction TB
        class CatalogueService {
            <<Interface>>
            %% Proxy: Subject
            +addProduct(product)
            +updateProduct(product)
            +deleteProduct(productId)
            +findProductById(productId) Optional~Product~
            +getAllProducts() List~Product~
            +getNextProductId() int
        }
    end

    subgraph patterns_singleton ["techstore.patterns.singleton"]
        direction TB
        class CatalogueManager {
            %% Singleton
            %% Proxy: RealSubject
            -instance: CatalogueManager
            -CatalogueManager()
            +getInstance() CatalogueManager
            +addProduct(product)
        }
        CatalogueManager --|> techstore_service.CatalogueService %% Corrected referencing with dot

        class UserManager {
            %% Singleton
            -instance: UserManager
            -notificationService: patterns_observer_NotificationService %% Link to class in another subgraph
            -UserManager()
            +getInstance() UserManager
            +addUser(user)
        }

        class OrderManager {
            %% Singleton
            -instance: OrderManager
            -notificationService: patterns_observer_NotificationService %% Link to class in another subgraph
            -OrderManager()
            +getInstance() OrderManager
            +placeOrder(order)
        }
    end

    subgraph patterns_factory ["techstore.patterns.factory"]
        direction TB
        class UserFactory {
            %% FactoryMethod
            +createUser(type, username, password, email) User
        }
        class ProductFactory {
            %% FactoryMethod
            +createProduct(name, description, price, category, brand, warrantyMonths, discount) Product
        }
    end

    subgraph patterns_builder ["techstore.patterns.builder"]
        direction TB
        class ProductBuilder {
            %% Builder
            -id: int
            -name: String
            +ProductBuilder(id, name, price)
            +description(desc) ProductBuilder
            +build() Product
        }
    end

    subgraph patterns_strategy ["techstore.patterns.strategy"]
        direction TB
        class PaymentStrategy {
            <<Interface>>
            %% Strategy
            +pay(amount)
        }
        class CreditCardPaymentStrategy {
            %% Uses Adapter
            -paymentGateway: patterns_adapter_ExternalPaymentGateway %% Link
            +CreditCardPaymentStrategy(cardNumber, cvv, expiryDate)
            +pay(amount)
        }
        PaymentStrategy <|-- CreditCardPaymentStrategy

        class PayPalPaymentStrategy {
            %% Uses Adapter
            -paymentGateway: patterns_adapter_ExternalPaymentGateway %% Link
            +PayPalPaymentStrategy(email, password)
            +pay(amount)
        }
        PaymentStrategy <|-- PayPalPaymentStrategy
    end

    subgraph patterns_command ["techstore.patterns.command"]
        direction TB
        class Command {
            <<Interface>>
            %% Command
            +execute()
        }
        class AddProductCommand {
            -catalogueService: techstore_service_CatalogueService %% Receiver, Link
            -product: techstore_model_Product %% Link
            +AddProductCommand(service, product)
            +execute()
        }
        Command <|-- AddProductCommand

        class AdminCommandInvoker {
            %% Invoker
            -command: Command
            +setCommand(command)
            +executeCommand()
        }
    end

    subgraph patterns_observer ["techstore.patterns.observer"]
        direction TB
        class Subject {
            <<Interface>>
            %% Subject
            +registerObserver(observer)
            +removeObserver(observer)
            +notifyObservers(message)
        }
        class Observer {
            <<Interface>>
            %% Observer
            +update(message)
        }
        class NotificationService {
            %% ConcreteSubject
            -observers: List~Observer~
            +notifyObservers(message)
        }
        Subject <|-- NotificationService

        class AdminNotifier {
            %% ConcreteObserver
            +update(message)
        }
        Observer <|-- AdminNotifier
    end

    subgraph patterns_decorator ["techstore.patterns.decorator"]
        direction TB
        class ProductComponent {
            <<Interface>>
            %% Component
            +getId() int
            +getName() String
            +getDescription() String
            +getPrice() double
        }
        class ProductDecorator {
            <<Abstract>>
            %% Decorator
            #wrappedProduct: ProductComponent
            +ProductDecorator(wrappedProduct)
        }
        ProductComponent <|-- ProductDecorator
        ProductDecorator o-- ProductComponent : wraps

        class GiftWrapDecorator {
            %% ConcreteDecorator
            +getPrice() double
        }
        ProductDecorator <|-- GiftWrapDecorator

        class ExtendedWarrantyDecorator {
            %% ConcreteDecorator
            +getPrice() double
        }
        ProductDecorator <|-- ExtendedWarrantyDecorator
    end

    subgraph patterns_adapter ["techstore.patterns.adapter"]
        direction TB
        class ExternalPaymentGateway {
            <<Interface>>
            %% Target
            +processPayment(amount) boolean
        }
        class PayPalApi {
            %% Adaptee
            +sendPayment(email, totalAmount)
        }
        class CreditCardApi {
            %% Adaptee
            +chargeCreditCard(cardNumber, expiry, cvv, amount) boolean
        }
        class PayPalAdapter {
            %% Adapter
            -payPalApi: PayPalApi
            +processPayment(amount) boolean
        }
        ExternalPaymentGateway <|-- PayPalAdapter

        class CreditCardAdapter {
            %% Adapter
            -creditCardApi: CreditCardApi
            +processPayment(amount) boolean
        }
        ExternalPaymentGateway <|-- CreditCardAdapter
    end

    subgraph patterns_proxy ["techstore.patterns.proxy"]
        direction TB
        class CatalogueServiceProxy {
            %% Proxy
            -realCatalogueService: patterns_singleton_CatalogueManager %% Link
            -currentUser: techstore_model_User %% Link
            +CatalogueServiceProxy(currentUser)
            +addProduct(product)
        }
        CatalogueServiceProxy --|> techstore_service_CatalogueService %% Corrected referencing
    end

    subgraph patterns_templatemethod ["techstore.patterns.templatemethod"]
        direction TB
        class OrderProcessingTemplate {
            <<Abstract>>
            %% AbstractClass_TemplateMethod
            +processOrder(order, paymentStrategy)*
            #updateInventory()$
            #notifyClient(order)$
        }
        class OnlineOrderProcess {
            %% ConcreteClass_TemplateMethod
            #updateInventory()
            #notifyClient(order)
        }
        OrderProcessingTemplate <|-- OnlineOrderProcess
    end

    subgraph techstore_controller ["techstore.controller"]
        direction TB
        %% Part of MVC: Controller
        class AppController {
            +run()
        }
        class AdminController {
            +showAdminMenu()
        }
        class ClientController {
            +showClientMenu()
            +checkout()
        }
    end

    subgraph techstore_view ["techstore.view"]
        direction TB
        %% Part of MVC: View
        class ConsoleView {
            +displayProducts(products)
            +displayShoppingCart(cart)
        }
        class InputUtil {
            +getString(prompt) String
            +getInt(prompt) int
        }
    end

    subgraph techstore ["techstore"] %% Changed name to avoid conflict if `main` is a keyword
        direction TB
        class MainApp { %% Renamed from Main to avoid potential keyword clash
            +main(args) $
        }
    end

    %% -------- Relationships between classes in different subgraphs --------
    %% It's often better to qualify class names if they are not unique across the diagram,
    %% or if the renderer needs help. Mermaid might infer some.
    %% Example: techstore_model.Client or using the ID given to subgraph like techstore_model_Client

    techstore_model.Client "1" *-- "1" techstore_model.ShoppingCart : has
    techstore_model.ShoppingCart "1" *-- "0..*" techstore_model.CartItem : contains
    techstore_model.CartItem "1" o-- "1" patterns_decorator.ProductComponent : references
    techstore_model.Product "0..*" --> "0..1" techstore_model.Category : belongs to
    techstore_model.Order "0..*" --> "1" techstore_model.Client : placed by
    techstore_model.Order "1" *-- "1..*" techstore_model.CartItem : "consists of (copied)"
    techstore_model.Order "1" o-- "0..1" patterns_strategy.PaymentStrategy : "uses (Strategy)"

    patterns_factory.UserFactory ..> techstore_model.Admin : "creates (Admin)"
    patterns_factory.UserFactory ..> techstore_model.Client : "creates (Client)"
    patterns_factory.ProductFactory ..> techstore_model.Product : "creates"
    patterns_factory.ProductFactory ..> patterns_builder.ProductBuilder : "uses (Builder)"

    patterns_builder.ProductBuilder ..> techstore_model.Product : "builds"

    patterns_singleton.UserManager ..> patterns_observer.NotificationService : "uses (Observer)"
    patterns_singleton.OrderManager ..> patterns_observer.NotificationService : "uses (Observer)"
    patterns_observer.NotificationService "1" o-- "0..*" patterns_observer.Observer : maintains

    patterns_proxy.CatalogueServiceProxy ..> patterns_singleton.CatalogueManager : "delegates to (RealSubject)"
    patterns_proxy.CatalogueServiceProxy ..> techstore_model.User : "for access control"

    patterns_command.AdminCommandInvoker o-- patterns_command.Command : holds
    patterns_command.AddProductCommand ..> techstore_service.CatalogueService : "uses (Receiver)"

    patterns_strategy.CreditCardPaymentStrategy ..> patterns_adapter.ExternalPaymentGateway : "uses (Adapter Target)"
    patterns_strategy.PayPalPaymentStrategy ..> patterns_adapter.ExternalPaymentGateway : "uses (Adapter Target)"
    patterns_adapter.PayPalAdapter ..> patterns_adapter.PayPalApi : "adapts"
    patterns_adapter.CreditCardAdapter ..> patterns_adapter.CreditCardApi : "adapts"

    patterns_templatemethod.OrderProcessingTemplate ..> techstore_model.Order
    patterns_templatemethod.OrderProcessingTemplate ..> patterns_strategy.PaymentStrategy : uses

    techstore_controller.AppController ..> patterns_singleton.UserManager
    techstore_controller.AppController ..> techstore_service.CatalogueService : "(Proxy instance)"
    techstore_controller.AppController ..> patterns_singleton.OrderManager
    techstore_controller.AppController ..> patterns_factory.UserFactory : uses
    techstore_controller.AppController ..> patterns_observer.NotificationService : setup

    techstore_controller.AdminController ..> techstore_service.CatalogueService : "(Proxy instance)"
    techstore_controller.AdminController ..> patterns_command.AdminCommandInvoker : uses
    techstore_controller.AdminController ..> patterns_factory.ProductFactory : uses

    techstore_controller.ClientController ..> techstore_service.CatalogueService : "(Proxy instance)"
    techstore_controller.ClientController ..> patterns_singleton.OrderManager
    techstore_controller.ClientController ..> patterns_templatemethod.OnlineOrderProcess : "uses (TemplateMethod for checkout)"
    techstore_controller.ClientController ..> patterns_decorator.GiftWrapDecorator : "uses (Decorator)"
    techstore_controller.ClientController ..> patterns_decorator.ExtendedWarrantyDecorator : "uses (Decorator)"
    techstore_controller.ClientController ..> techstore_model.ShoppingCart : "manipulates"

    techstore.MainApp ..> techstore_controller.AppController : "starts app"
    techstore_controller.AppController ..> techstore_view.ConsoleView
    techstore_controller.AdminController ..> techstore_view.ConsoleView
    techstore_controller.ClientController ..> techstore_view.ConsoleView
    techstore_view.ConsoleView ..> techstore_model.ShoppingCart : "displays (read-only)"

    %% Notes:
    %% MVC Pattern: Indicated by techstore_model, techstore_view, techstore_controller subgraphs.
    %% Iterator Pattern: Implicit in List iterations (e.g., ShoppingCart.getItems(), ConsoleView.displayProducts).
```