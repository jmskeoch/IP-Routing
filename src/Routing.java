public class Routing {
    public Node root; // The tree begins with the root, and therefore has two children of type Node

    public Routing() {
        this.root = new Node(); // The root will be set appropriately in each method
    }


    public static void main(String[] args) {
        Routing routes = new Routing();
        routes.insert("255.0.0.0");
        routes.insert("10.8.52.3");
        routes.insert("10.8.10.8");

        routes.print();

        routes.blacklist("10.8.52.3");

        routes.print();
    }

    public void insert(String data) { insert(data, root); } // Wrapper method

    /**
     * This method appropriately inserts the number data as a node into the tree
     * @param data The ~Integer~ data to be inserted into the tree
     */
    public void insert(String data, Node current) {
        data = data.replace(".", ""); // Since we are working with IPs, remove all periods
        Node node = current.children[Integer.parseInt(data.substring(0, 1))]; // Set the current node to where we want to insert

        // Begin insertion cases
        if (data.length() == 0) return; // Stop insertion if there is no data to insert

        if (node == null) { // If there's no present node for our data, create it
            node = new Node();
            node.data = data;
            node.isIp = true;
        }
        else if (node.data.equals(data)) { // If the data has already been inserted, exit
            node.isIp = true;
        }
        else if (data.startsWith(node.data)) { // If our inserting data is longer than what is already present, insert the remainder as a new node
            insert(data.substring(node.data.length()), node);
        }
        else if (node.data.startsWith(data)) { // If our already present data is longer than what we want to insert, we need to split the already existing data
            Node splitNode = new Node();

            splitNode.data = node.data.substring(data.length());
            node.data = data; // Erase the parent data, as our new split node contains the remainder data
            node.isIp = true;

            splitNode.children = node.children; // Move the children below our new split node
            node.children = new Node[10]; // Erase the parent's children, as our split node holds the children
            node.children[Integer.parseInt(splitNode.data.substring(0, 1))] = splitNode; // Set our split node to our parent's child
        }
        else { // If our data doesn't perfectly match the prefix, we need to find where the difference is, and split there
            int idx = 0;
            Node splitNode = new Node();
            while (node.data.charAt(idx) == data.charAt(idx)) idx++; // Find the first, non-data-intersecting index

            splitNode.data = node.data.substring(idx); // Reset the node's data so there is none repeating
            node.data = node.data.substring(0, idx);

            splitNode.children = node.children;
            node.children = new Node[10]; // Properly reset both node's children
            node.children[Integer.parseInt(splitNode.data.substring(0, 1))] = splitNode;

            splitNode.isIp = node.isIp;
            node.isIp = false;

            insert(data.substring(idx), node); // Insert the other different data as a new node
        }
        current.children[Integer.parseInt(data.substring(0, 1))] = node;
    }

    public boolean search(String data) {
        return search(data, root);
    }

    public boolean search(String data, Node current) {
        if (data == null || data.length() == 0) return false;

        data = data.replace(".", "");
        Node node = current.children[Integer.parseInt(data.substring(0, 1))];
        if (node == null) return false;
        if (node.data.equals(data)) return node.isIp;
        if (data.startsWith(node.data)) return search(data.substring(node.data.length()), node);

        return false;
    }

    // Wrapper method
    public void print() {
        System.out.println("ALL ACCEPTED IP ADDRESSES");
        System.out.println("=========================");
        if (root != null) print(root, "");
    }

    /**
     * This method recursively prints out the radix tree's inserted IPs
     * @param current The current node we are attempting to print
     * @param str The recursive portion containing the actual data to print
     */
    public void print(Node current, String str) {
        if (current.isIp && !current.blacklisted) System.out.println(str);

        for(Node node: current.children) {
            if (node != null) print(node, str + node.data);
        }
    }

    // Wrapper method
    public void blacklist(String data) {
        blacklist(data, root);
    }
    /**
     * This method searches through the tree, inserts if the parameter isn't already existing, and marks the node as blacklisted
     * @param data The address to blacklist in 192.2.2.4 form
     * @param current Recursive parameter for a radix node
     */
    public void blacklist(String data, Node current) {
        if (data == null || data.length() == 0) return;

        data = data.replace(".", "");
        Node node = current.children[Integer.parseInt(data.substring(0, 1))];
        if (node == null) return;
        if (node.data.equals(data)) node.blacklisted = true;
        if (data.startsWith(node.data)) blacklist(data.substring(node.data.length()), node);
    }
}

/**
 * IP ROUTING TREE NODE CLASS
 * This class needs two instance variables: the data it stores, and a list for its children
 */
class Node {
    String data;
    boolean isIp;
    boolean blacklisted;
    Node[] children;

    public Node() {
        this.data = "";
        this.isIp = false;
        this.blacklisted = false;
        this.children = new Node[10];
    }
}
