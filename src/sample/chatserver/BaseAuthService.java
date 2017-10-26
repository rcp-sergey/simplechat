package sample.chatserver;

import java.util.ArrayList;

public class BaseAuthService implements AuthService{
    private class Entry {
        String login;
        String pass;
        String nick;

        public Entry(String login, String pass, String nick) {
            this.login = login;
            this.pass = pass;
            this.nick = nick;
        }
    }
    private ArrayList<Entry> entries;

    public BaseAuthService() {
        entries = new ArrayList<>();
        entries.add(new Entry("login1", "pass1", "nick1"));
        entries.add(new Entry("login2", "pass2", "nick2"));
        entries.add(new Entry("login3", "pass3", "nick3"));
        entries.add(new Entry("login4", "pass4", "nick4"));
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public String getNickLoginByPass(String login, String pass) {
        for (Entry e: entries) {
            if (e.login.equals(login) && e.pass.equals(pass)) return e.nick;
        }
        return null;
    }
}
