package commandlineparser;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class AccountManager {
    private User loggedUser;
    private String file;

    public User getLoggedUser() {
        return loggedUser;
    }
    
    public String getFile() {
        return file;
    }

    private final List<User> users = new ArrayList<>();
    
    public AccountManager(String file) throws NullPointerException {
        if (file == null)
            throw new NullPointerException("file");

        this.file = file;
        ReadJsonStorage(file);
    }

    public boolean LogUser(String login, String pass) {
        User user = users.stream().filter(u -> u.getLogin().equals(login)).findFirst().orElse(null);

        if (user == null)
            return false;

        if (!user.getPass().equals(pass))
            return false;

        loggedUser = user;
        return true;
    }

    public void Logout() {
        loggedUser = null;
    }
    
    private void ReadJsonStorage(String url) {
        JSONParser parser = new JSONParser();

        try (FileReader reader = new FileReader(url)) {
            JSONArray rawUsers = (JSONArray) parser.parse(reader);

            for (Object rawUser : rawUsers) {
                JSONObject raw = (JSONObject) rawUser;
                String login = raw.get("login").toString();
                String pass = raw.get("pass").toString();
                
                User u = new User(login, pass);
                users.add(u);
            }
        } catch (Exception e) {
            System.out.println("Storage manager failed with exception: " + e.getMessage());
        }
    }
}
