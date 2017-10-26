package sample.chatserver;

/**
 * Created by cherginets-sv on 24.10.2017.
 */
public interface AuthService {
    void start();
    void stop();
    String getNickLoginByPass(String login, String pass);
}
