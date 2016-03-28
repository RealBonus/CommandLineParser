package commandlineparser;

public class User {
    private final String login;
    private final String pass;

    public String getLogin() {
        return login;
    }

    public String getPass() {
        return pass;
    }

    public User(String login, String pass) {
        this.login = login;
        this.pass = pass;
    }
}
