import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.util.HashSet;
import java.util.Set;

public class Main {

    private static final int PORTA = 12000;

    private static Set<PrintWriter> usuarios = new HashSet<>();

    public static void main(String[] args) {
        try(ServerSocket serverSocket = new ServerSocket(PORTA)) {
            System.out.println("Servidor rodando na porta " + PORTA + ".");
            while(true) {
                new ControleCliente(
                        serverSocket.accept(),
                        usuarios
                ).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
