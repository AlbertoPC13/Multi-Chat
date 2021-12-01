package multichat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;

class GestorClientes implements Runnable {

    public static ArrayList<GestorClientes> listaClientes = new ArrayList<>();
    private Socket socket;
    private BufferedReader bufferLectura;
    private BufferedWriter bufferEscritura;
    private String nombreUsuario;

    public GestorClientes(Socket socket)
    {
        try {
            this.socket = socket;
            this.bufferEscritura = new BufferedWriter((new OutputStreamWriter(socket.getOutputStream())));
            this.bufferLectura = new BufferedReader((new InputStreamReader(socket.getInputStream())));
            this.nombreUsuario = bufferLectura.readLine();
            listaClientes.add(this);
            mensajeBroadcast("SERVER: " + nombreUsuario + " ha entrado al chat!");
        }
        catch(IOException e)
        {
            cerrarConexion(socket, bufferLectura, bufferEscritura);
        }
    }
    
    @Override
    public void run() {
        String mensajeCliente;
        
        while(socket.isConnected())
        {
            try
            {
                mensajeCliente = bufferLectura.readLine();
                mensajeBroadcast(mensajeCliente);
            }
            catch(IOException e)
            {
                cerrarConexion(socket, bufferLectura, bufferEscritura);
                break;
            }
        }
    }
    
    public void mensajeBroadcast(String mensaje)
    {
        for(GestorClientes manejadorCliente : listaClientes)
        {
            try
            {
                if(!manejadorCliente.nombreUsuario.equals(nombreUsuario))
                {
                    manejadorCliente.bufferEscritura.write(mensaje);
                    manejadorCliente.bufferEscritura.newLine();
                    manejadorCliente.bufferEscritura.flush();
                }
            }
            catch(IOException e)
            {
                cerrarConexion(socket, bufferLectura, bufferEscritura);
            }
        }
    }
    
    public void removerCliente()
    {
        listaClientes.remove(this);
        mensajeBroadcast("SERVER: " + nombreUsuario + " ha salido del chat!");
    }
    
    public void cerrarConexion(Socket socket, BufferedReader bufferLectura, BufferedWriter bufferEscritura)
    {
        removerCliente();
        try
        {
            if(bufferLectura != null)
            {
                bufferLectura.close();
            }
            if(bufferEscritura != null)
            {
                bufferEscritura.close();
            }
            if(socket != null)
            {
                socket.close();
            }
        }
        catch(IOException e)
        {
        }
    }
}
