
## 4. Description des Composants Clés

### 4.1. Modèle (Package `model`)

*   **`Product.java`**: Représente un article en vente. Il implémente `ProductComponent` pour le pattern Decorator et est construit par `ProductBuilder`.
    *   Attributs: `id`, `name`, `description`, `price`, `category`, `brand` (optionnel), `warrantyMonths` (optionnel), `discount` (optionnel).
*   **`Category.java`**: Représente une catégorie de produits.
*   **`User.java` (abstrait)**: Classe de base pour les utilisateurs.
    *   Sous-classes: `Admin.java`, `Client.java` (produits du UserFactory).
*   **`ShoppingCart.java`**: Gère la collection de `CartItem` pour un client. Utilise implicitement l'Iterator pour parcourir les articles.
*   **`CartItem.java`**: Représente un article (potentiellement décoré via `ProductComponent`) et sa quantité dans le panier.
*   **`Order.java`**: Représente une commande passée par un client. Contient la liste des articles, le total, et le statut. Utilise une `PaymentStrategy` pour le paiement.

### 4.2. Vue (Package `view`)

*   **`ConsoleView.java`**: Gère toutes les sorties vers la console, comme l'affichage des menus, des produits, du panier, etc.
*   **`InputUtil.java`**: Classe utilitaire avec des méthodes statiques pour lire de manière robuste les entrées de l'utilisateur (chaînes, entiers, doubles) depuis la console.

### 4.3. Contrôleur (Package `controller`)

*   **`AppController.java`**: Le contrôleur principal qui gère le flux général de l'application, y compris la connexion, l'enregistrement, et la délégation aux contrôleurs spécifiques (Admin ou Client). Initialise les services et les singletons.
*   **`AdminController.java`**: Gère les interactions et la logique spécifiques à l'administrateur (gestion des produits, catégories, utilisateurs, commandes). Utilise le pattern Command pour les actions administratives.
*   **`ClientController.java`**: Gère les interactions et la logique spécifiques au client (consultation des produits, gestion du panier, passage de commande). Utilise les patterns Decorator, Strategy, et Template Method lors de l'ajout au panier et du paiement.

### 4.4. Services et Managers (Principalement dans `patterns.singleton` et `service`)

*   **`UserManager.java` (Singleton)**: Gère la liste des utilisateurs (création, recherche). Sujet pour le pattern Observer (notifie lors de l'inscription).
*   **`CatalogueManager.java` (Singleton)**: Gère le catalogue de produits et les catégories. C'est le `RealSubject` pour le pattern Proxy (`CatalogueServiceProxy`).
*   **`OrderManager.java` (Singleton)**: Gère la liste des commandes. Sujet pour le pattern Observer (notifie lors d'une nouvelle commande).
*   **`CatalogueService.java` (Interface dans `service`)**: Interface pour la gestion du catalogue, utilisée par le Proxy.

## 5. Implémentation des Design Patterns

Douze design patterns sont intégrés pour structurer l'application :

### 5.1. Singleton (Package `patterns.singleton`)

*   **Objectif**: Assurer qu'une classe n'a qu'une seule instance et fournir un point d'accès global à cette instance.
*   **Implémentation**:
    *   `UserManager`: Instance unique pour gérer tous les utilisateurs.
    *   `CatalogueManager`: Instance unique pour gérer le catalogue de produits.
    *   `OrderManager`: Instance unique pour gérer toutes les commandes.
*   **Utilisation**: Chaque classe a un constructeur privé et une méthode statique `getInstance()` qui retourne l'unique instance. Cela permet un accès partagé et contrôlé aux données de gestion critiques.

### 5.2. Factory Method (Package `patterns.factory`)

*   **Objectif**: Définir une interface pour créer un objet, mais laisser les sous-classes décider quelle classe instancier.
*   **Implémentation**:
    *   `UserFactory`: `createUser()` méthode qui retourne un objet `User` (soit `Admin`, soit `Client`) en fonction d'un type fourni.
    *   `ProductFactory`: `createProduct()` et `createBasicProduct()` méthodes qui utilisent `ProductBuilder` pour instancier des objets `Product` complexes ou simples.
*   **Utilisation**: Simplifie la création d'objets complexes et découple le client de la connaissance des classes concrètes à instancier. `AppController` utilise `UserFactory` pour l'enregistrement. `AdminController` peut utiliser `ProductFactory`.

### 5.3. Builder (Package `patterns.builder`)

*   **Objectif**: Séparer la construction d'un objet complexe de sa représentation afin que le même processus de construction puisse créer différentes représentations.
*   **Implémentation**:
    *   `ProductBuilder`: Permet de construire un objet `Product` étape par étape, en spécifiant des attributs obligatoires dans le constructeur et des attributs optionnels via des méthodes "fluent". La méthode `build()` finalise la création.
*   **Utilisation**: Facilite la création d'objets `Product` avec de nombreux attributs, dont certains sont optionnels (marque, garantie, remise), rendant le code plus lisible que de multiples constructeurs surchargés. Utilisé par `ProductFactory`.

### 5.4. Strategy (Package `patterns.strategy`)

*   **Objectif**: Définir une famille d'algorithmes, encapsuler chacun d'eux, et les rendre interchangeables. Permet à l'algorithme de varier indépendamment des clients qui l'utilisent.
*   **Implémentation**:
    *   `PaymentStrategy` (Interface): Définit la méthode `pay(double amount)`.
    *   `CreditCardPaymentStrategy`, `PayPalPaymentStrategy`: Implémentations concrètes pour différents moyens de paiement. Elles utilisent le pattern Adapter pour interagir avec des API de paiement simulées.
*   **Utilisation**: `Order` possède une référence à une `PaymentStrategy`. Lors du processus de commande (via `OrderProcessingTemplate`), la stratégie de paiement choisie par le client est invoquée. Cela permet d'ajouter facilement de nouvelles méthodes de paiement.

### 5.5. Command (Package `patterns.command`)

*   **Objectif**: Encapsuler une requête en tant qu'objet, ce qui permet de paramétrer les clients avec différentes requêtes, de mettre en file d'attente ou de journaliser les requêtes, et de supporter les opérations annulables (non implémenté ici).
*   **Implémentation**:
    *   `Command` (Interface): Définit la méthode `execute()`.
    *   `AddProductCommand`, `UpdateProductCommand`, `DeleteProductCommand`: Commandes concrètes pour les opérations de gestion de produits. Elles encapsulent l'action et le récepteur (`CatalogueService`).
    *   `AdminCommandInvoker`: Reçoit un objet `Command` et l'exécute via sa méthode `executeCommand()`.
*   **Utilisation**: `AdminController` crée des objets Command pour les actions sur le catalogue et les passe à `AdminCommandInvoker`. Cela découple l'initiateur de la requête de l'objet qui sait comment effectuer l'action.

### 5.6. Observer (Package `patterns.observer`)

*   **Objectif**: Définir une dépendance un-à-plusieurs entre des objets de sorte que lorsqu'un objet change d'état, tous ses dépendants soient notifiés et mis à jour automatiquement.
*   **Implémentation**:
    *   `Subject` (Interface): Définit les méthodes pour enregistrer, supprimer et notifier les observateurs.
    *   `Observer` (Interface): Définit la méthode `update(String message)` que les observateurs doivent implémenter.
    *   `NotificationService` (Concrete Subject): Maintient une liste d'observateurs et les notifie.
    *   `AdminNotifier` (Concrete Observer): Reçoit des notifications et les affiche (simulant une notification à l'administrateur).
*   **Utilisation**:
    *   `UserManager` et `OrderManager` utilisent `NotificationService` pour notifier `AdminNotifier` lors de l'inscription d'un nouveau client ou de la création d'une nouvelle commande.
    *   Permet une communication découplée : les sujets (managers) n'ont pas besoin de connaître les observateurs concrets.

### 5.7. Decorator (Package `patterns.decorator`)

*   **Objectif**: Attacher dynamiquement des responsabilités supplémentaires à un objet. Les décorateurs fournissent une alternative flexible à la sous-classification pour étendre les fonctionnalités.
*   **Implémentation**:
    *   `ProductComponent` (Interface): Définit l'interface commune pour les produits et les décorateurs. `Product` est un `ConcreteComponent`.
    *   `ProductDecorator` (Abstract Decorator): Maintient une référence à un `ProductComponent` et implémente l'interface `ProductComponent`.
    *   `GiftWrapDecorator`, `ExtendedWarrantyDecorator`: Décorateurs concrets qui ajoutent des fonctionnalités (et modifient le prix/description) au `ProductComponent` qu'ils enveloppent.
*   **Utilisation**: Dans `ClientController`, lorsqu'un client ajoute un produit au panier, il peut choisir d'ajouter un emballage cadeau ou une extension de garantie. Ces options sont ajoutées en enveloppant l'objet `Product` avec les décorateurs correspondants. Le `ShoppingCart` stocke des `ProductComponent`.

### 5.8. Adapter (Package `patterns.adapter`)

*   **Objectif**: Convertir l'interface d'une classe en une autre interface que le client attend. Permet à des classes de travailler ensemble alors qu'elles ne le pourraient pas autrement à cause d'interfaces incompatibles.
*   **Implémentation**:
    *   `ExternalPaymentGateway` (Target Interface): L'interface que les stratégies de paiement (`CreditCardPaymentStrategy`, `PayPalPaymentStrategy`) attendent.
    *   `PayPalApi`, `CreditCardApi` (Adaptees): Classes simulées avec des interfaces différentes pour le traitement des paiements.
    *   `PayPalAdapter`, `CreditCardAdapter`: Implémentent `ExternalPaymentGateway` et traduisent les appels vers les méthodes des `Adaptees` correspondants.
*   **Utilisation**: Les stratégies de paiement utilisent les adaptateurs pour interagir avec les systèmes de paiement externes (simulés) de manière uniforme via l'interface `ExternalPaymentGateway`.

### 5.9. Proxy (Package `patterns.proxy`)

*   **Objectif**: Fournir un substitut ou un placeholder pour un autre objet afin de contrôler l'accès à celui-ci.
*   **Implémentation**:
    *   `CatalogueService` (Interface Subject): Définit les opérations de gestion du catalogue.
    *   `CatalogueManager` (RealSubject): L'objet réel qui effectue les opérations sur le catalogue.
    *   `CatalogueServiceProxy`: Implémente `CatalogueService` et détient une référence à `CatalogueManager`. Il contrôle l'accès aux méthodes sensibles (ajout, modification, suppression de produit/catégorie) en vérifiant si `currentUser` est un `Admin`. Les opérations de lecture sont généralement autorisées pour tous.
*   **Utilisation**: `AppController` instancie `CatalogueServiceProxy` avec l'utilisateur actuellement connecté. Les `AdminController` et `ClientController` interagissent avec le catalogue via cette instance de proxy, garantissant que seuls les administrateurs peuvent effectuer des modifications.

### 5.10. MVC (Modèle-Vue-Contrôleur)

*   **Objectif**: Séparer l'application en trois composants interconnectés pour promouvoir la séparation des préoccupations, ce qui améliore la maintenabilité et la testabilité.
*   **Implémentation**:
    *   **Modèle**: Packages `model`, `patterns.singleton` (managers). Contient les données et la logique métier.
    *   **Vue**: Package `view` (`ConsoleView`, `InputUtil`). Gère l'affichage et l'entrée utilisateur brute.
    *   **Contrôleur**: Package `controller` (`AppController`, `AdminController`, `ClientController`). Traite les entrées utilisateur, interagit avec le modèle, et met à jour la vue.
*   **Utilisation**: Structure l'ensemble de l'application, permettant une organisation claire du code et une meilleure gestion de la complexité.

### 5.11. Template Method (Package `patterns.templatemethod`)

*   **Objectif**: Définir le squelette d'un algorithme dans une opération, en reportant certaines étapes à des sous-classes. Permet aux sous-classes de redéfinir certaines étapes d'un algorithme sans changer la structure de l'algorithme.
*   **Implémentation**:
    *   `OrderProcessingTemplate` (Abstract Class): Définit la méthode `processOrder(Order, PaymentStrategy)` qui est la méthode template (`final`). Cette méthode appelle une série d'étapes protégées (certaines concrètes, d'autres abstraites).
    *   `OnlineOrderProcess` (Concrete Class): Étend `OrderProcessingTemplate` et implémente les étapes abstraites (`updateInventory`, `notifyClient`) spécifiques à une commande en ligne.
*   **Utilisation**: `ClientController`, lors du checkout, utilise `OnlineOrderProcess` pour traiter une commande. Cela assure un processus de commande cohérent tout en permettant des variations (par exemple, un `StorePickupOrderProcess` pourrait avoir des étapes différentes pour la notification et l'inventaire).

### 5.12. Iterator

*   **Objectif**: Fournir un moyen d'accéder séquentiellement aux éléments d'un objet agrégé sans exposer sa représentation sous-jacente.
*   **Implémentation**:
    *   Ce pattern est **utilisé implicitement** via les interfaces et classes de la Java Collections Framework (`List`, `Set`, etc.). Par exemple, `List<Product>` retournée par `CatalogueManager.getAllProducts()` est `Iterable`.
    *   Les boucles `for-each` (ex: `for (Product p : productList)`) utilisent l'Iterator en coulisses.
*   **Utilisation**:
    *   `ConsoleView` parcourt les listes de produits, d'articles du panier, de catégories, et de commandes pour les afficher.
    *   `ShoppingCart` retourne une copie de sa liste d'items, permettant une itération externe.
    *   Aucune classe Iterator personnalisée n'a été nécessaire car les fonctionnalités standard de Java suffisent.

## 6. Flux d'Exécution Typiques

### 6.1. Inscription et Connexion Client

1.  `Main` lance `AppController`.
2.  `AppController` affiche le menu principal (Login/Register/Exit).
3.  Si "Register":
    *   `AppController` demande les infos client.
    *   Utilise `UserFactory` pour créer un objet `Client`.
    *   Ajoute le client via `UserManager`.
    *   `UserManager` (Subject) notifie `NotificationService`, qui notifie `AdminNotifier` (Observer).
4.  Si "Login":
    *   `AppController` demande les identifiants.
    *   Vérifie via `UserManager`.
    *   Si succès, `currentUser` est défini. `CatalogueServiceProxy` est (ré)initialisé avec ce `currentUser`.
    *   `AppController` délègue au `ClientController` ou `AdminController`.

### 6.2. Client: Ajout d'un Produit au Panier

1.  `ClientController` affiche les options.
2.  Le client choisit "Add Product to Cart".
3.  `ClientController` récupère la liste des produits via `CatalogueService` (Proxy).
4.  Le client sélectionne un produit.
5.  Le client peut choisir des options (Decorator): Gift Wrap, Extended Warranty.
    *   Si oui, le `Product` est enveloppé par les décorateurs correspondants (`GiftWrapDecorator`, `ExtendedWarrantyDecorator`).
6.  Le `ProductComponent` (original ou décoré) est ajouté au `ShoppingCart` du client.

### 6.3. Client: Passage de Commande

1.  `ClientController` affiche l'option "Checkout".
2.  Affiche le contenu du `ShoppingCart` et le total.
3.  Le client choisit une méthode de paiement (Strategy): Credit Card ou PayPal.
    *   Une instance de `CreditCardPaymentStrategy` ou `PayPalPaymentStrategy` est créée. Celles-ci utilisent des Adapters (`CreditCardAdapter`, `PayPalAdapter`) pour interagir avec des API simulées.
4.  Une `Order` est créée.
5.  `ClientController` utilise `OnlineOrderProcess` (Template Method) pour traiter la commande.
    *   `OrderProcessingTemplate.processOrder()` est appelée.
    *   Elle exécute les étapes: initialisation, assignation et validation du paiement (qui appelle `paymentStrategy.pay()`), confirmation, mise à jour de l'inventaire, notification client, finalisation.
6.  Si le paiement réussit, l' `Order` est ajoutée via `OrderManager`.
7.  `OrderManager` (Subject) notifie `NotificationService`, qui notifie `AdminNotifier` (Observer).
8.  Le panier du client est vidé.

### 6.4. Admin: Ajout d'un Produit

1.  `AdminController` affiche les options.
2.  L'admin choisit "Manage Products" puis "Add Product".
3.  `AdminController` demande les détails du produit.
4.  L'admin peut utiliser `ProductFactory` (qui utilise `ProductBuilder`) pour créer le `Product`.
5.  Un `AddProductCommand` est créé avec le nouveau `Product` et le `CatalogueService` (Proxy).
6.  `AdminCommandInvoker` exécute la commande.
7.  `AddProductCommand.execute()` appelle `catalogueService.addProduct()`.
8.  `CatalogueServiceProxy` vérifie que l'utilisateur est un admin, puis délègue l'appel à `CatalogueManager.addProduct()`.

## 7. Comment Compiler et Exécuter

1.  Assurez-vous que la structure des dossiers correspond aux packages définis.
2.  Placez-vous dans le répertoire parent du dossier `techstore`.
3.  Compilez tous les fichiers Java :
    ```bash
    javac $(find techstore -name "*.java")
    ```
    Ou, si vous listez les fichiers manuellement (plus verbeux mais peut aider au débogage des chemins) :
    ```bash
    javac techstore/Main.java techstore/model/*.java techstore/patterns/*/*.java techstore/service/*.java techstore/controller/*.java techstore/view/*.java
    ```
4.  Exécutez l'application :
    ```bash
    java techstore.Main
    ```

## 8. Conclusion et Pistes d'Amélioration

TechStore démontre l'application pratique de douze design patterns dans un contexte Java. Chaque pattern contribue à une meilleure conception, en favorisant la flexibilité, la réutilisabilité et la maintenabilité.

**Pistes d'amélioration potentielles:**

*   **Persistance des données**: Utiliser une base de données (JDBC, JPA/Hibernate) au lieu de collections en mémoire.
*   **Interface Utilisateur**: Migrer vers une interface graphique (Swing, JavaFX) ou une application web (Spring Boot).
*   **Gestion des Erreurs**: Implémenter une gestion des exceptions plus robuste et des retours utilisateurs plus clairs.
*   **Tests Unitaires**: Ajouter des tests JUnit pour valider le comportement des différents composants.
*   **Sécurité**: Hachage des mots de passe, meilleure gestion des sessions.
*   **Fonctionnalités Avancées**: Recherche de produits plus complexe, recommandations, gestion des stocks plus détaillée, annulation de commande (pourrait utiliser le pattern Command pour undo).
*   **Injection de Dépendances**: Utiliser un framework comme Spring pour gérer les dépendances au lieu de l'instanciation manuelle ou des Singletons pour tout.

Cette documentation fournit une base pour comprendre la structure et le fonctionnement de l'application TechStore.