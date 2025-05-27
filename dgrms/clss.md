# TechStore - Diagramme de Classes (Markdown)

Ce document décrit la structure des classes de l'application TechStore en utilisant Markdown.

## Légende de Visibilité
*   `+` : public
*   `-` : private
*   `#` : protected
*   `{static}` : membre statique
*   `{abstract}` : membre abstrait

---

## Package: `techstore.model` (Modèle MVC)

### Classe: `User` (Abstraite)
*   **Attributs**:
    *   `# id: int`
    *   `# username: String`
    *   `# password: String` (Devrait être haché en réalité)
    *   `# email: String`
*   **Méthodes**:
    *   `+ User(username, password, email)`
    *   `+ getId(): int`
    *   `+ getUsername(): String`
    *   `+ getEmail(): String`
    *   `+ checkPassword(password: String): boolean`
    *   `+ {abstract} getRole(): String`
*   **Note**: Classe de base pour les utilisateurs.

### Classe: `Admin`
*   **Hérite de**: `User`
*   **Méthodes**:
    *   `+ Admin(username, password, email)`
    *   `+ getRole(): String` (Implémentation)
*   **Patron de Conception**: Produit du `UserFactory` (Factory Method).

### Classe: `Client`
*   **Hérite de**: `User`
*   **Attributs**:
    *   `- shoppingCart: ShoppingCart`
*   **Méthodes**:
    *   `+ Client(username, password, email)`
    *   `+ getRole(): String` (Implémentation)
    *   `+ getShoppingCart(): ShoppingCart`
*   **Relations**:
    *   Composition: `1 Client` --possède-- `1 ShoppingCart`
*   **Patron de Conception**: Produit du `UserFactory` (Factory Method).

### Interface: `ProductComponent`
*   **Méthodes**:
    *   `+ getId(): int`
    *   `+ getName(): String`
    *   `+ getDescription(): String`
    *   `+ getPrice(): double`
*   **Patron de Conception**: Composant pour le patron Decorator.

### Classe: `Product`
*   **Implémente**: `ProductComponent`
*   **Attributs**:
    *   `- id: int`
    *   `- name: String`
    *   `- description: String`
    *   `- price: double`
    *   `- category: Category`
    *   `- brand: String` (Optionnel)
    *   `- warrantyMonths: int` (Optionnel)
    *   `- discount: double` (Optionnel)
*   **Méthodes**:
    *   `+ Product(id, name, description, price, category, brand, warrantyMonths, discount)`
    *   `+ getId(): int`
    *   `+ getName(): String`
    *   `+ getDescription(): String`
    *   `+ getPrice(): double`
    *   `+ getCategory(): Category`
    *   `... (autres getters/setters)`
*   **Relations**:
    *   Association: `1 Product` --appartient à-- `0..1 Category`
*   **Patron de Conception**:
    *   Composant concret pour le patron Decorator.
    *   Construit par `ProductBuilder` (Builder).
    *   Produit du `ProductFactory` (Factory Method).

### Classe: `Category`
*   **Attributs**:
    *   `- {static} nextId: int`
    *   `- id: int`
    *   `- name: String`
*   **Méthodes**:
    *   `+ Category(name: String)`
    *   `+ getId(): int`
    *   `+ getName(): String`

### Classe: `ShoppingCart`
*   **Attributs**:
    *   `- client: Client`
    *   `- items: List<CartItem>`
*   **Méthodes**:
    *   `+ ShoppingCart(client: Client)`
    *   `+ addProduct(product: ProductComponent, quantity: int)`
    *   `+ removeProduct(productId: int)`
    *   `+ updateQuantity(productId: int, quantity: int)`
    *   `+ getItems(): List<CartItem>`
    *   `+ getTotalCost(): double`
    *   `+ clearCart()`
    *   `+ isEmpty(): boolean`
*   **Relations**:
    *   Association: `1 ShoppingCart` --appartient à-- `1 Client`
    *   Composition: `1 ShoppingCart` --contient-- `0..* CartItem`
*   **Patron de Conception**:
    *   Utilise le patron Iterator implicitement via `getItems().iterator()`.

### Classe: `CartItem`
*   **Attributs**:
    *   `- product: ProductComponent` (Peut être un `Product` ou un `ProductDecorator`)
    *   `- quantity: int`
*   **Méthodes**:
    *   `+ CartItem(product: ProductComponent, quantity: int)`
    *   `+ getProduct(): ProductComponent`
    *   `+ getQuantity(): int`
    *   `+ getTotalPrice(): double`
*   **Relations**:
    *   Association: `1 CartItem` --référence-- `1 ProductComponent`

### Classe: `Order`
*   **Attributs**:
    *   `- {static} nextId: int`
    *   `- id: int`
    *   `- client: Client`
    *   `- orderedItems: List<CartItem>`
    *   `- totalAmount: double`
    *   `- orderDate: LocalDateTime`
    *   `- status: String`
    *   `- paymentStrategy: PaymentStrategy` (Référence pour le patron Strategy)
*   **Méthodes**:
    *   `+ Order(client: Client, orderedItems: List<CartItem>, totalAmount: double)`
    *   `+ getId(): int`
    *   `+ getClient(): Client`
    *   `+ getOrderedItems(): List<CartItem>`
    *   `+ getTotalAmount(): double`
    *   `+ setStatus(status: String)`
    *   `+ setPaymentStrategy(paymentStrategy: PaymentStrategy)`
    *   `+ processPayment(): boolean`
*   **Relations**:
    *   Association: `1 Order` --passée par-- `1 Client`
    *   Agrégation: `1 Order` --inclut-- `0..* CartItem`
    *   Association: `1 Order` --utilise-- `0..1 PaymentStrategy`
*   **Patron de Conception**:
    *   Le contexte pour le patron Strategy (`paymentStrategy`).
    *   La méthode `processPayment()` utilise la `PaymentStrategy` sélectionnée.

---

## Package: `techstore.service`

### Interface: `CatalogueService`
*   **Méthodes**:
    *   `+ addProduct(product: Product)`
    *   `+ updateProduct(product: Product)`
    *   `+ deleteProduct(productId: int)`
    *   `+ findProductById(productId: int): Optional<Product>`
    *   `+ getAllProducts(): List<Product>`
    *   `+ findProductsByCategory(category: Category): List<Product>`
    *   `+ addCategory(category: Category)`
    *   `+ findCategoryByName(name: String): Optional<Category>`
    *   `+ findCategoryById(id: int): Optional<Category>`
    *   `+ getAllCategories(): List<Category>`
    *   `+ getNextProductId(): int`
*   **Patron de Conception**: Interface Sujet pour le patron Proxy.

---

## Package: `techstore.patterns`

### Sous-package: `singleton`
*   **Classe**: `UserManager`
    *   **Patron de Conception**: Singleton
    *   **Attributs**: `- {static} instance: UserManager`, `- users: List<User>`, `- notificationService: NotificationService`
    *   **Méthodes**: `+ {static} getInstance(): UserManager`, `+ addUser(user: User)`, `+ setNotificationService(ns: NotificationService)`
    *   **Note**: Gère une instance unique des utilisateurs. Notifie les `Observer`s via `NotificationService`.
*   **Classe**: `CatalogueManager`
    *   **Patron de Conception**: Singleton, RealSubject (pour Proxy)
    *   **Implémente**: `techstore.service.CatalogueService`
    *   **Attributs**: `- {static} instance: CatalogueManager`, `- products: List<Product>`, `...`
    *   **Méthodes**: `+ {static} getInstance(): CatalogueManager`, `+ addProduct(product: Product)`, `...`
    *   **Note**: Gère le catalogue. Cible du `CatalogueServiceProxy`.
*   **Classe**: `OrderManager`
    *   **Patron de Conception**: Singleton
    *   **Attributs**: `- {static} instance: OrderManager`, `- orders: List<Order>`, `- notificationService: NotificationService`
    *   **Méthodes**: `+ {static} getInstance(): OrderManager`, `+ placeOrder(order: Order)`, `+ setNotificationService(ns: NotificationService)`
    *   **Note**: Gère les commandes. Notifie les `Observer`s.

### Sous-package: `factory`
*   **Classe**: `UserFactory`
    *   **Patron de Conception**: Factory Method
    *   **Méthodes**: `+ createUser(type: UserType, username, password, email): User`
    *   **Relations**: Dépendance (crée) vers `User`, `Admin`, `Client`.
*   **Classe**: `ProductFactory`
    *   **Patron de Conception**: Factory Method
    *   **Méthodes**: `+ createProduct(...): Product`, `+ createBasicProduct(...): Product`
    *   **Relations**: Dépendance (crée) vers `Product`. Utilise `ProductBuilder`.

### Sous-package: `builder`
*   **Classe**: `ProductBuilder`
    *   **Patron de Conception**: Builder
    *   **Attributs**: `- id: int`, `- name: String`, `- price: double`, `...` (attributs optionnels du produit)
    *   **Méthodes**: `+ ProductBuilder(id, name, price)`, `+ description(String): ProductBuilder`, `...` (méthodes fluides), `+ build(): Product`
    *   **Relations**: Dépendance (construit) vers `Product`.

### Sous-package: `strategy`
*   **Interface**: `PaymentStrategy`
    *   **Patron de Conception**: Strategy (Interface Stratégie)
    *   **Méthodes**: `+ pay(amount: double)`
*   **Classe**: `CreditCardPaymentStrategy`
    *   **Implémente**: `PaymentStrategy`
    *   **Attributs**: `- cardNumber: String`, `...`, `- paymentGateway: ExternalPaymentGateway`
    *   **Méthodes**: `+ pay(amount: double)`
    *   **Note**: Utilise un `Adapter` (`ExternalPaymentGateway`) pour le traitement réel.
*   **Classe**: `PayPalPaymentStrategy`
    *   **Implémente**: `PaymentStrategy`
    *   **Attributs**: `- email: String`, `...`, `- paymentGateway: ExternalPaymentGateway`
    *   **Méthodes**: `+ pay(amount: double)`
    *   **Note**: Utilise un `Adapter` (`ExternalPaymentGateway`) pour le traitement réel.

### Sous-package: `command`
*   **Interface**: `Command`
    *   **Patron de Conception**: Command (Interface Commande)
    *   **Méthodes**: `+ execute()`
*   **Classes**: `AddProductCommand`, `UpdateProductCommand`, `DeleteProductCommand`
    *   **Implémente**: `Command`
    *   **Attributs**: `- catalogueService: CatalogueService`, `- product: Product` (ou `productId`)
    *   **Méthodes**: `+ execute()`
    *   **Relations**: Utilise `CatalogueService` (le Proxy).
*   **Classe**: `AdminCommandInvoker`
    *   **Patron de Conception**: Invoker (pour Command)
    *   **Attributs**: `- command: Command`
    *   **Méthodes**: `+ setCommand(command: Command)`, `+ executeCommand()`
    *   **Relations**: Agrégation: `1 AdminCommandInvoker` --a-un-- `0..1 Command`

### Sous-package: `observer`
*   **Interface**: `Subject`
    *   **Patron de Conception**: Subject (pour Observer)
    *   **Méthodes**: `+ registerObserver(o: Observer)`, `+ removeObserver(o: Observer)`, `+ notifyObservers(message: String)`
*   **Interface**: `Observer`
    *   **Patron de Conception**: Observer (Interface Observateur)
    *   **Méthodes**: `+ update(message: String)`
*   **Classe**: `NotificationService`
    *   **Implémente**: `Subject`
    *   **Patron de Conception**: ConcreteSubject (pour Observer)
    *   **Attributs**: `- observers: List<Observer>`
    *   **Relations**: Agrégation: `1 NotificationService` --notifie-- `0..* Observer`
*   **Classe**: `AdminNotifier`
    *   **Implémente**: `Observer`
    *   **Patron de Conception**: ConcreteObserver (pour Observer)
    *   **Attributs**: `- adminName: String`
    *   **Méthodes**: `+ update(message: String)`
*   **Note**: `UserManager` et `OrderManager` utilisent `NotificationService` pour notifier les `AdminNotifier`.

### Sous-package: `decorator`
*   **Interface**: `ProductComponent` (déjà définie dans `techstore.model`)
*   **Classe Abstraite**: `ProductDecorator`
    *   **Implémente**: `ProductComponent`
    *   **Patron de Conception**: Decorator (Classe Décorateur Abstraite)
    *   **Attributs**: `# wrappedProduct: ProductComponent`
    *   **Méthodes**: `+ getId()`, `+ getName()`, `...` (délèguent à `wrappedProduct`)
    *   **Relations**: Composition: `1 ProductDecorator` --enveloppe-- `1 ProductComponent`
*   **Classes**: `GiftWrapDecorator`, `ExtendedWarrantyDecorator`
    *   **Hérite de**: `ProductDecorator`
    *   **Patron de Conception**: ConcreteDecorator (pour Decorator)
    *   **Méthodes**: Redéfinissent `getName()`, `getDescription()`, `getPrice()` pour ajouter des fonctionnalités/coûts.

### Sous-package: `adapter`
*   **Interface**: `ExternalPaymentGateway`
    *   **Patron de Conception**: Target (pour Adapter)
    *   **Méthodes**: `+ processPayment(amount: double): boolean`
*   **Classes (Adaptees - Systèmes Externes Simulés)**: `PayPalApi`, `CreditCardApi`
    *   **Patron de Conception**: Adaptee (pour Adapter)
    *   **Méthodes**: (spécifiques à chaque API, ex: `PayPalApi.sendPayment(...)`)
*   **Classes (Adapters)**: `PayPalAdapter`, `CreditCardAdapter`
    *   **Implémente**: `ExternalPaymentGateway`
    *   **Patron de Conception**: Adapter
    *   **Attributs**: `- payPalApi: PayPalApi` (ou `CreditCardApi`)
    *   **Méthodes**: `+ processPayment(amount: double): boolean` (traduit l'appel vers l'Adaptee)
    *   **Relations**: Dépendance (adapte) vers `PayPalApi` ou `CreditCardApi`.
    *   **Note**: `CreditCardPaymentStrategy` et `PayPalPaymentStrategy` utilisent ces adaptateurs.

### Sous-package: `proxy`
*   **Interface**: `CatalogueService` (déjà définie dans `techstore.service`)
*   **Classe (RealSubject)**: `CatalogueManager` (déjà définie dans `techstore.patterns.singleton`)
*   **Classe**: `CatalogueServiceProxy`
    *   **Implémente**: `techstore.service.CatalogueService`
    *   **Patron de Conception**: Proxy
    *   **Attributs**: `- realCatalogueService: CatalogueManager`, `- currentUser: User`
    *   **Méthodes**: `+ addProduct(product: Product)` (contrôle d'accès ici), `...` (autres méthodes avec ou sans contrôle d'accès), `+ findProductById(productId: int): Optional<Product>`
    *   **Relations**:
        *   Association: `1 CatalogueServiceProxy` --proxy pour-- `1 CatalogueManager`
        *   Dépendance: Vérifie le rôle de `User`.
    *   **Note**: Contrôle l'accès à `CatalogueManager` en fonction du rôle de `currentUser`.

### Sous-package: `templatemethod`
*   **Classe Abstraite**: `OrderProcessingTemplate`
    *   **Patron de Conception**: Template Method
    *   **Méthodes**:
        *   `+ {final} processOrder(order: Order, paymentStrategy: PaymentStrategy)` (Méthode Modèle)
        *   `# initializeOrder(order: Order)`
        *   `# assignPaymentMethod(order: Order, ps: PaymentStrategy)`
        *   `# validatePayment(order: Order): boolean`
        *   `# confirmPayment(order: Order)`
        *   `# {abstract} updateInventory()` (Étape primitive)
        *   `# {abstract} notifyClient(order: Order)` (Étape primitive)
        *   `# finalizeOrder(order: Order)`
        *   `# handlePaymentFailure(order: Order)`
    *   **Relations**: Utilise `Order` et `PaymentStrategy`.
    *   **Note**: `validatePayment()` invoque `Order.processPayment()` qui utilise le patron Strategy.
*   **Classe**: `OnlineOrderProcess`
    *   **Hérite de**: `OrderProcessingTemplate`
    *   **Méthodes**: Implémente `updateInventory()`, `notifyClient()`. Peut redéfinir d'autres étapes (ex: `finalizeOrder`).

---

## Package: `techstore.controller` (Contrôleur MVC)

### Classe: `AppController`
*   **Attributs**:
    *   `- view: ConsoleView`
    *   `- userManager: UserManager`
    *   `- userFactory: UserFactory`
    *   `- notificationService: NotificationService`
    *   `- currentUser: User`
    *   `- catalogueService: CatalogueService` (Instance de `CatalogueServiceProxy`)
    *   `- orderManager: OrderManager`
*   **Méthodes Clés**:
    *   `+ AppController()` (Initialise les gestionnaires, services, et `seedData()`)
    *   `+ run()` (Boucle principale de l'application)
    *   `- showLoginMenu()`
    *   `- login()`
    *   `- registerClient()`
*   **Relations**:
    *   Utilise `ConsoleView`, `UserManager`, `UserFactory`, `NotificationService`, `CatalogueServiceProxy`, `OrderManager`.
    *   Crée et délègue à `AdminController` ou `ClientController`.

### Classe: `AdminController`
*   **Attributs**:
    *   `- view: ConsoleView`
    *   `- admin: Admin`
    *   `- catalogueService: CatalogueService` (Instance de `CatalogueServiceProxy`)
    *   `- commandInvoker: AdminCommandInvoker`
    *   `- productFactory: ProductFactory` (indirectement via Builder pour création)
    *   `- userManager: UserManager`
    *   `- orderManager: OrderManager`
*   **Méthodes Clés**:
    *   `+ AdminController(...)`
    *   `+ showAdminMenu()`
    *   `- manageProducts()`
    *   `- addProduct()` (Utilise `ProductBuilder` et `AddProductCommand`)
    *   `...` (autres actions admin)
*   **Relations**:
    *   Utilise `ConsoleView`, `Admin`, `CatalogueServiceProxy`, `AdminCommandInvoker`, `ProductFactory` (ou `ProductBuilder`), `UserManager`, `OrderManager`.

### Classe: `ClientController`
*   **Attributs**:
    *   `- view: ConsoleView`
    *   `- client: Client`
    *   `- catalogueService: CatalogueService` (Instance de `CatalogueServiceProxy`)
    *   `- orderManager: OrderManager`
*   **Méthodes Clés**:
    *   `+ ClientController(...)`
    *   `+ showClientMenu()`
    *   `- viewProducts()`
    *   `- addProductToCart()` (Utilise le patron Decorator)
    *   `- checkout()` (Utilise les patrons Strategy et Template Method)
    *   `...` (autres actions client)
*   **Relations**:
    *   Utilise `ConsoleView`, `Client`, `CatalogueServiceProxy`, `OrderManager`.
    *   Utilise les patrons `Decorator`, `Strategy`, `TemplateMethod`.

---

## Package: `techstore.view` (Vue MVC)

### Classe: `ConsoleView`
*   **Méthodes**:
    *   `+ displayMessage(String)`
    *   `+ displayProducts(List<Product>)`
    *   `+ displayProductDetails(ProductComponent)`
    *   `+ displayShoppingCart(ShoppingCart)`
    *   `+ displayCategories(List<Category>)`
    *   `+ displayOrders(List<Order>)`
    *   `...` (autres méthodes d'affichage)
*   **Note**: Responsable de l'affichage des informations à la console.

### Classe: `InputUtil`
*   **Méthodes (statiques)**:
    *   `+ {static} getString(prompt: String): String`
    *   `+ {static} getInt(prompt: String): int`
    *   `+ {static} getDouble(prompt: String): double`
    *   `+ {static} closeScanner()`
*   **Note**: Utilitaire pour lire les entrées utilisateur.

---

## Classe Principale: `techstore.Main`
*   **Méthodes**:
    *   `+ {static} main(String[] args)`
*   **Relations**: Crée et lance `AppController`.

---

## Patron de Conception: Iterator
*   **Note**: Utilisé implicitement via les Collections Java (ex: `List.iterator()`).
*   **Exemples d'utilisation**:
    *   `ShoppingCart.getItems().iterator()` pour parcourir les `CartItem`.
    *   `CatalogueManager.getAllProducts().iterator()` pour parcourir les `Product`.
    *   Dans `ConsoleView` pour afficher des listes.

---