import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Cliente {
    public static void main(String[] args) {

        System.out.println("Entre com seu nome");
        Scanner sc = new Scanner(System.in);
        String client = new Scanner(System.in).nextLine();

        try(Socket socket = new Socket("localhost", 12000);
            BufferedReader leitor = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter usuario = new PrintWriter(socket.getOutputStream(), true);
        ){
            usuario.println(client);

            Thread receptor = new Thread(() ->{
                try {
                    String mensagemRecebida;
                    while((mensagemRecebida = leitor.readLine()) != null){
                        System.out.println(mensagemRecebida);
                    }
                } catch(IOException ioException){
                    System.out.println("Finalizado");
                }
            });
            receptor.start();

            while(true){
                Thread.sleep(300);
                System.out.println("Escreva sua mensagem");
                String mensagem = sc.nextLine();

                if(mensagem.equalsIgnoreCase("sair")){
                    break;
                }

                usuario.println(mensagem);
            }
            System.out.println("Saindo da conversa");
            receptor.interrupt();
        } catch(IOException | InterruptedException ioException){
            ioException.printStackTrace();
        }
    }
}
