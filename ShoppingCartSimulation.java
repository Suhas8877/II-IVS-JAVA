import java.util.*;
import java.sql.*;

class Product {
    int id;
    String name;
    double price;
    int stock;

    Product(int id, String name, double price, int stock) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
    }
}

class CartItem {
    Product product;
    int quantity;

    CartItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    double getTotal() {
        return product.price * quantity;
    }
}

public class ShoppingCartSimulation {

    static List<Product> products = new ArrayList<>();
    static List<CartItem> cart = new ArrayList<>();

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        loadProducts();

        while (true) {
            System.out.println("\n1.View Products  2.Add to Cart  3.View Cart");
            System.out.println("4.Remove from Cart  5.Bill  6.Exit");
            System.out.print("Enter choice: ");
            int choice = sc.nextInt();

            switch (choice) {
                case 1:
                    viewProducts();
                    break;
                case 2:
                    addToCart(sc);
                    break;
                case 3:
                    viewCart();
                    break;
                case 4:
                    removeFromCart(sc);
                    break;
                case 5:
                    generateBill();
                    break;
                case 6:
                    System.exit(0);
                default:
                    System.out.println("Invalid choice");
            }
        }
    }


    static void viewProducts() {
        for (Product p : products) {
            System.out.println(p.id + " " + p.name + " Rs." + p.price + " Stock:" + p.stock);
        }
    }

    static void addToCart(Scanner sc) {
    System.out.print("Enter product id: ");
    int id = sc.nextInt();
    System.out.print("Enter quantity: ");
    int qty = sc.nextInt();

    for (Product p : products) {
        if (p.id == id) {
            if (p.stock >= qty) {
                cart.add(new CartItem(p, qty));
                p.stock -= qty;
                System.out.println("Added to cart");
            } else {
                System.out.println("Insufficient stock");
            }
            return;
        }
    }
    System.out.println("Product not found");
}

   static void loadProducts() {
    try {
        Connection con = DBConnection.getConnection();

        String query = "SELECT * FROM products";
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(query);

        while (rs.next()) {
            products.add(new Product(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getDouble("price"),
                rs.getInt("stock")
            ));
        }

        con.close();

    } catch (Exception e) {
        System.out.println("Error loading products from DB");
        e.printStackTrace();
    }
}

    static void viewCart() {
        if (cart.isEmpty()) {
            System.out.println("Cart is empty");
            return;
        }

        for (CartItem c : cart) {
            System.out.println("ID:" + c.product.id + " "
                    + c.product.name + " x " + c.quantity
                    + " = Rs." + c.getTotal());
        }
    }

    static void removeFromCart(Scanner sc) {
        if (cart.isEmpty()) {
            System.out.println("Cart is empty");
            return;
        }

        viewCart();

        System.out.print("Enter product id to remove: ");
        int id = sc.nextInt();

        Iterator<CartItem> iterator = cart.iterator();

        while (iterator.hasNext()) {
            CartItem c = iterator.next();

            if (c.product.id == id) {

                System.out.print("Enter quantity to remove: ");
                int qty = sc.nextInt();

                if (qty > c.quantity) {
                    System.out.println("You only have " + c.quantity + " in cart.");
                    return;
                }

                c.product.stock += qty;
                c.quantity -= qty;

                if (c.quantity == 0) {
                    iterator.remove();
                }

                System.out.println("Quantity removed successfully.");
                return;
            }
        }

        System.out.println("Product not found in cart");
    }

    static void generateBill() {
        if (cart.isEmpty()) {
            System.out.println("Cart is empty");
            return;
        }

        Scanner sc = new Scanner(System.in);

        double total = 0;

        for (CartItem c : cart) {
            total += c.getTotal();
        }

        System.out.println("Total Amount: Rs." + total);

        System.out.print("Enter discount coupon (or type 'no'): ");
        String coupon = sc.next();

        double discount = 0;

        if (coupon.equalsIgnoreCase("mark17a")) {
            discount = total * 0.10;
            System.out.println("10% coupon applied!");
        } else if (coupon.equalsIgnoreCase("virat18")) {
            discount = total * 0.18;
            System.out.println("18% coupon applied!");
        } else if (coupon.equalsIgnoreCase("rcb2026")) {
            discount = total * 0.26;
            System.out.println("26% coupon applied!");
        } else if (coupon.equalsIgnoreCase("no")) {
            System.out.println("No coupon applied.");
        } else {
            System.out.println("Invalid coupon! No discount applied.");
        }

        double finalAmount = total - discount;

        System.out.println("Discount: Rs." + discount);
        System.out.println("Final Payable Amount: Rs." + finalAmount);
    }
}