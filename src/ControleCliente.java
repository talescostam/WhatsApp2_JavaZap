import java.io.*;
import java.net.Socket;
import java.util.Set;

public class ControleCliente extends Thread {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private Set<PrintWriter> usuarios;
    private String nome;

    public ControleCliente(Socket socket, Set<PrintWriter> usuarios) throws IOException {
        this.socket = socket;
        this.usuarios = usuarios;
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(socket.getOutputStream(), true);
    }

    public void run() {
        try {
            synchronized (usuarios) {
                usuarios.add(out);
            }

            String msg;
            while ((msg = in.readLine()) != null) {
                System.out.println(msg);
                if (nome == null) {
                    nome = msg.split(" ")[0];
                }
                synchronized (usuarios) {
                    for (PrintWriter usuario : usuarios) {
                        usuario.println(msg);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            synchronized (usuarios) {
                usuarios.remove(out);
            }
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getNome() {
        return nome;
    }
}
