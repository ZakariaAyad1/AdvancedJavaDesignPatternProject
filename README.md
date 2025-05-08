# AdvancedJavaDesignPatternProject
TechStore is a Java  demo showcasing twelve design patterns through functionalities like product browsing, shopping cart management, and admin controls. This project serves as a practical learning tool for object-oriented design principles.

# TechStore - Application Console Java de Gestion de Produits

![Java Version](https://img.shields.io/badge/Java-8+-blue.svg)
![License](https://img.shields.io/badge/License-MIT-green.svg) <!-- Ou la licence de votre choix -->
![Build Status](https://img.shields.io/badge/Build-Passing-brightgreen) <!-- Mettez à jour manuellement ou via CI -->

TechStore est une application console Java simplifiée qui simule une boutique en ligne de produits technologiques. Son objectif principal est de démontrer l'application pratique de **douze design patterns fondamentaux** dans un contexte orienté objet.

## Table des Matières

- [Introduction](#introduction)
- [Fonctionnalités](#fonctionnalités)
  - [Client](#client)
  - [Administrateur](#administrateur)
- [Design Patterns Implémentés](#design-patterns-implémentés)
- [Architecture](#architecture)
- [Prérequis](#prérequis)
- [Installation et Exécution](#installation-et-exécution)
  - [Compilation](#compilation)
  - [Exécution](#exécution)
- [Guide d'Utilisation](#guide-dutilisation)
- [Structure du Projet](#structure-du-projet)
- [Diagramme de Classes](#diagramme-de-classes)
- [Limitations](#limitations)
- [Améliorations Futures](#améliorations-futures)
- [Contribution](#contribution)
- [Licence](#licence)

## Introduction

Ce projet sert d'exemple pratique pour les développeurs et étudiants souhaitant comprendre comment structurer une application en utilisant des principes de conception solides et des design patterns reconnus. L'application permet aux clients de parcourir des produits, de gérer un panier et de passer des commandes, tandis que les administrateurs peuvent gérer le catalogue de produits, les catégories, les utilisateurs et les commandes.

## Fonctionnalités

### Client

*   ✅ Inscription et Connexion
*   ✅ Consultation du catalogue de produits
*   ✅ Visualisation des détails d'un produit
*   ✅ Ajout d'options aux produits (ex: emballage cadeau, extension de garantie - via Decorator)
*   ✅ Gestion du panier d'achat (ajout, modification de quantité, suppression)
*   ✅ Finalisation de commande avec choix du mode de paiement (Carte Bancaire, PayPal - via Strategy & Adapter)
*   ✅ Consultation de l'historique des commandes personnelles

### Administrateur

*   ✅ Connexion sécurisée (via Proxy pour les actions sensibles)
*   ✅ Gestion du catalogue de produits (CRUD - via Command)
    *   Utilisation du pattern Builder pour la création de produits complexes.
*   ✅ Gestion des catégories de produits
*   ✅ Visualisation de toutes les commandes clients
*   ✅ Visualisation de tous les utilisateurs enregistrés
*   ✅ Notifications pour les événements importants (ex: nouvelle inscription, nouvelle commande - via Observer)

## Design Patterns Implémentés

L'application intègre les douze design patterns suivants :

1.  ✨ **Singleton**: Pour `UserManager`, `CatalogueManager`, `OrderManager`.
2.  🏭 **Factory Method**: Pour la création de `User` (Admin, Client) et `Product`.
3.  🧱 **Builder**: Pour la construction d'objets `Product` complexes.
4.  🎯 **Strategy**: Pour implémenter différentes stratégies de paiement (`CreditCardPaymentStrategy`, `PayPalPaymentStrategy`).
5.  💻 **Command**: Pour encapsuler les actions administratives (ajout, modification, suppression de produit).
6.  👀 **Observer**: Pour notifier l'administrateur des inscriptions ou commandes.
7.  🎁 **Decorator**: Pour ajouter dynamiquement des fonctionnalités optionnelles à un `Product` (emballage cadeau, extension de garantie).
8.  🔌 **Adapter**: Pour l'intégration de systèmes de paiement externes simulés (`PayPalApi`, `CreditCardApi`).
9.  🛡️ **Proxy**: Pour contrôler l'accès aux fonctionnalités sensibles de gestion du catalogue.
10. 🏛️ **MVC (Modèle-Vue-Contrôleur)**: Pour séparer les données, la logique métier et l'interface console.
11. 📜 **Template Method**: Pour définir la trame du processus de commande avec des variantes.
12. 🚶 **Iterator**: Utilisé implicitement via les collections Java pour parcourir les produits et le contenu du panier.

## Architecture

Le projet suit une architecture **Modèle-Vue-Contrôleur (MVC)** :

*   **Modèle**: Classes dans `techstore.model`, services et managers (ex: `CatalogueManager`). Contient les données et la logique métier.
*   **Vue**: Classes dans `techstore.view` (`ConsoleView`, `InputUtil`). Gère l'affichage en console.
*   **Contrôleur**: Classes dans `techstore.controller` (`AppController`, `AdminController`, `ClientController`). Gère les interactions utilisateur et le flux de l'application.

## Prérequis

*   Java Development Kit (JDK) 8 ou une version ultérieure.

## Installation et Exécution

1.  **Clonez le dépôt :**
    ```bash
    git clone https://github.com/VOTRE_NOM_UTILISATEUR/NOM_DU_DEPOT.git
    cd NOM_DU_DEPOT
    ```

2.  **Structure des fichiers :**
    Assurez-vous que les fichiers source sont dans la structure de packages attendue (ex: `techstore/model/Product.java`). Le point d'entrée est `techstore.Main`.

### Compilation

Ouvrez un terminal à la racine du projet (le dossier contenant le dossier `techstore`) et exécutez :

```bash
# Sur Linux/macOS
javac $(find techstore -name "*.java")

# Sur Windows (PowerShell)
# Get-ChildItem -Recurse -Filter "*.java" | ForEach-Object { javac $_.FullName }
# Ou plus simplement, si votre classpath est bien configuré, naviguez dans le dossier parent de techstore et faites :
# javac techstore/Main.java techstore/model/*.java techstore/patterns/*/*.java techstore/service/*.java techstore/controller/*.java techstore/view/*.java
# (adaptez les wildcards selon la profondeur des sous-dossiers de patterns)
