package commandlineparser;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class StorageManager {
    private String file;
    private final List<Product> products = new ArrayList<>();

    public String getFile() {
        return file;
    }

    public List<Product> getProducts() {
        return products;
    }

    public StorageManager(String file) throws NullPointerException {
        if (file == null)
            throw new NullPointerException("file");

        this.file = file;
        ReadJsonStorage(file);
    }

    private void ReadJsonStorage(String url) {
        JSONParser parser = new JSONParser();

        try (FileReader reader = new FileReader(url)) {
            JSONArray rawProducts = (JSONArray) parser.parse(reader);

            for (Object product : rawProducts) {
                JSONObject raw = (JSONObject) product;
                String name = raw.get("name").toString();
                double price = Double.parseDouble(raw.get("price").toString());
                int amount = Integer.parseInt(raw.get("amount").toString());

                Product p = new Product(name, price, amount);
                products.add(p);
            }
        } catch (Exception e) {
            System.out.printf("Storage manager failed with exception: %s\n" + e.getMessage());
        }
    }

    public Product CreateNewProduct(String name) {
        if (name == null)
            return null;

        Product product = new Product(name, 0, 0);
        getProducts().add(product);
        return product;
    }

    public Product FindProduct(String name) {
        if (name == null)
            return null;

        return getProducts().stream().filter(product -> product.getName().equals(name)).findFirst().orElse(null);
    }

    public boolean RemoveProduct(Product product) {
        if (product == null || !products.contains(product))
            return false;

        products.remove(product);
        return true;
    }

    @SuppressWarnings("unchecked")
    public void SaveStorage() throws IOException {
        JSONArray raw = new JSONArray();

        for (Product product: products) {
            JSONObject prodRaw = new JSONObject();
            prodRaw.put("name", product.getName());
            prodRaw.put("price", product.getPrice());
            prodRaw.put("amount", product.getAmount());
            raw.add(prodRaw);
        }

        try (FileWriter fileWriter = new FileWriter(getFile())) {
            fileWriter.write(raw.toJSONString());
        }
    }
}
