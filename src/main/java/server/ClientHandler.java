package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {
    private Socket socket;
    private ChatServer server;
    private DataInputStream in;
    private DataOutputStream out;
    private String nick;
    private AuthService authService;

    public ClientHandler(Socket socket, ChatServer server, AuthService authService) {

        try {
            this.authService = authService;
            this.server      = server;
            this.socket      = socket;
            this.in          = new DataInputStream(socket.getInputStream());
            this.out         = new DataOutputStream(socket.getOutputStream());
            new Thread(() -> {
                try {
                    authentificate();
                    readMessage();
                } finally {
                    closeConnection();
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public String getNick() {
        return nick;
    }

    private void authentificate() {
        while (true) {
            try {
                final String message = in.readUTF();
                if (message.startsWith("/auth")) {
                  final String[] split  = message.split("\\p{Blank}+");
                  final String login    = split[1];
                  final String password = split[2];
                  final String nick     = authService.getNickByLoginAndPassword(login, password);
                  if (nick != null) {
                      if (server.isNickBusy(nick)) {
                          sendMessage("Пользователь уже авторизован");
                      }
                      sendMessage("/authok " + nick);
                      this.nick = nick;
                      server.broadcast("Пользователь " + nick + " зашел в чат");
                      server.subscribe(this);
                      break;
                  } else {
                      sendMessage("Неверный логин или пароль");
                  }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void closeConnection() {
        sendMessage("/end");
        if (in != null) {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (out != null) {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (socket != null) {
            server.unsubscribe(this);
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMessage(String message) {
        try {
            out.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readMessage() {
        while (true) {
            try {
                final String message = in.readUTF();
                if ("/end".equals(message)) {
                    break;
                } else if (message.startsWith("/w")) {
                   final String[] split = message.split("\\p{Blank}+");
                   final String addressee = split[1];
                   server.broadcastPersonal("[Личное сообщение] "+ nick + ": " + message, addressee);
                }
                server.broadcast(nick + ": " + message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
