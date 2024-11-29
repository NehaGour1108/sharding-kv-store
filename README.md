# **Sharding Key-Value Store**

This project demonstrates two common sharding techniques, **Hash-Based Sharding** and **Range-Based Sharding**, to distribute key-value pairs across multiple database shards. It uses MySQL as the underlying database.

---

## **Features**
- **Hash-Based Sharding**:
  - Distributes keys across shards using a hash function.
  - Ensures even distribution of keys for scalability.

- **Range-Based Sharding**:
  - Distributes keys across shards based on character ranges.
  - Suitable for datasets with predictable key patterns.

- Supports:
  - **Insert/Update** key-value pairs.
  - **Retrieve** values for a given key.
  - **Dynamic Table Creation** if the target shard does not already have a required table.

---

## **Technologies Used**
- **Java**: Core programming language.
- **MySQL**: Relational database for shard storage.
- **JDBC**: Java Database Connectivity for database interaction.

---

## **Setup Instructions**

### **1. Prerequisites**
- **Java Development Kit (JDK)**: Ensure JDK 8 or higher is installed.
- **MySQL Database**: Install MySQL and set up three shards (databases) with appropriate ports:
  - `shard0`: Port `3306`
  - `shard1`: Port `3307`
  - `shard2`: Port `3308`

### **2. Database Configuration**
1. Create three databases:
   ```sql
   CREATE DATABASE shard0;
   CREATE DATABASE shard1;
   CREATE DATABASE shard2;
   ```
2. Make sure each database allows connections with a valid MySQL user. Update the credentials in the Java code:
   - `USER`: MySQL username (default is `root`).
   - `PASSWORD`: MySQL password.

### **3. Run the Project**
1. Clone this repository:
   ```bash
   git clone <repository_url>
   cd <repository_folder>
   ```
2. Compile the project:
   ```bash
   javac -cp .:mysql-connector-java-8.0.32.jar org/example/*.java
   ```
3. Run the **Hash-Based Sharding** example:
   ```bash
   java -cp .:mysql-connector-java-8.0.32.jar org.example.HashBasedSharding
   ```
4. Run the **Range-Based Sharding** example:
   ```bash
   java -cp .:mysql-connector-java-8.0.32.jar org.example.RangeBasedSharding
   ```

---

## **How It Works**

### **Hash-Based Sharding**
1. Determines the shard for a key using this formula:  
   ```java
   shard = Math.abs(key.hashCode()) % NUM_SHARDS;
   ```
2. Inserts or retrieves the key-value pair from the corresponding shard.

### **Range-Based Sharding**
1. Determines the shard for a key based on its first character:
   - Keys starting with `A-L` go to **Shard 0**.
   - Keys starting with `M-S` go to **Shard 1**.
   - Keys starting with `T-Z` go to **Shard 2**.

---

## **Usage Example**

### **Hash-Based Sharding**
```java
HashBasedSharding sharding = new HashBasedSharding();
sharding.put("key1", "value1");
System.out.println("Value for key1: " + sharding.get("key1"));
```

### **Range-Based Sharding**
```java
RangeBasedSharding sharding = new RangeBasedSharding();
sharding.put("apple", "fruit1");
System.out.println("Value for apple: " + sharding.get("apple"));
```

---

## **Project Structure**
```
src
└── org/example
    ├── HashBasedSharding.java  # Implements hash-based sharding logic.
    ├── RangeBasedSharding.java # Implements range-based sharding logic.
```

---

## **Future Enhancements**
- Add more sharding strategies (e.g., geographic or custom-defined).
- Implement shard scaling (adding/removing shards dynamically).
- Add caching for faster key retrieval.
