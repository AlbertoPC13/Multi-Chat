package multichat;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {

    private ServerSocket socketServidor;

    public Servidor(ServerSocket socketServidor) {
        this.socketServidor = socketServidor;
    }

    public void startServer() {
        try {
            while (!socketServidor.isClosed()) {
                Socket socket = socketServidor.accept();
                System.out.println("Un nuevo usuario se ha conectado!");
                GestorClientes gestorClientes = new GestorClientes(socket);

                Thread thread = new Thread(gestorClientes);
                thread.start();
            }
        } catch (IOException e) {
            cerrarSocketServidor();
        }
    }

    public void cerrarSocketServidor() {
        try {
            if (socketServidor != null) {
                socketServidor.close();
            }
        } catch (IOException e) {
        }
    }

    public static void main(String[] args) throws IOException {
        ServerSocket socketServidor = new ServerSocket(1234);
        Servidor server = new Servidor(socketServidor);
        System.out.println("....SERVIDOR INICIADO....\n\n");
        server.startServer();
    }
}
