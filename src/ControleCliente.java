import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Set;

public class ControleCliente extends Thread {

    private Socket socket;

    private PrintWriter usuario;

    private Set<PrintWriter> listaDeUsuarios;

    ControleCliente(Socket socket, Set<PrintWriter> usuario) {
        this.socket = socket;
        listaDeUsuarios = usuario;
    }

    @Override
    public void run() {

        try {
            BufferedReader leitor = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            usuario = new PrintWriter(socket.getOutputStream(), true);

            synchronized (listaDeUsuarios) {
                listaDeUsuarios.add(usuario);
            }

            String usuiario = leitor.readLine();
            transmitir(usuiario + " entrou no chat!");
            System.out.println("O" + usuario + "conectou ao servidor");

            String mensagem;
            while ((mensagem = leitor.readLine()) != null) {

                if(mensagem.equals("sair")) {
                    break;
                }

                transmitir(usuiario + ": " + mensagem);
            }

            synchronized (listaDeUsuarios) {
                listaDeUsuarios.remove(usuario);
            }

            transmitir("O" + usuiario + "saiu do chat!");
            System.out.println("O" + usuario + "se desconectou do servidor");

        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private void transmitir(String mensagem){
            synchronized (listaDeUsuarios) {
                listaDeUsuarios.forEach((usuario -> {
                    usuario.println(mensagem);
                }));
            }

    }
}
