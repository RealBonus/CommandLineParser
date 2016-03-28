package commandlineparser;

import java.util.ArrayList;
import java.util.List;

public class AccountManager {
    private User loggedUser;

    public User getLoggedUser() {
        return loggedUser;
    }

    private final List<User> users;
    {
        users = new ArrayList<>(3);
        users.add(new User("admin", "admin"));
        users.add(new User("TheRock", "iLovePancakes"));
        users.add(new User("user", "god"));
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
}
