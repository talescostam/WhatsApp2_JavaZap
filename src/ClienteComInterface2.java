import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClienteComInterface2 {
    private JFrame frame;
    private JTextField messageField;
    private JTextField nameField;
    private PrintWriter usuario;
    private Box messagesBox;
    private String nome;

    public ClienteComInterface2(String serverAddress, int port) {
        // Configurações da janela
        frame = new JFrame("WhatsApp 2");
        try {
            ImageIcon icon = new ImageIcon(ImageIO.read(new File("src/images/whatsapp2logo.png")));
            frame.setIconImage(icon.getImage());
        } catch (IOException e) {
            e.printStackTrace();
        }
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);
        frame.setLayout(new BorderLayout());
        frame.setVisible(true);
        frame.getContentPane().setBackground(Color.BLUE);

        // Área de texto para exibir o chat
        messagesBox = Box.createVerticalBox();
        JScrollPane scrollPane = new JScrollPane(messagesBox);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Campo para digitar nome
        nameField = new JTextField(20);
        JButton connectButton = new JButton("Conectar");
        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                conectarAoServidor(serverAddress, port);
            }
        });

        JPanel namePanel = new JPanel();
        namePanel.add(new JLabel("Nome:"));
        namePanel.add(nameField);
        namePanel.add(connectButton);
        namePanel.setBackground(Color.CYAN);
        frame.add(namePanel, BorderLayout.NORTH);

        // Campo para digitar mensagem e botão de envio
        messageField = new JTextField(30);
        JButton sendButton = new JButton("Enviar");
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                enviarMensagem();
            }
        });

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(messageField, BorderLayout.CENTER);
        panel.add(sendButton, BorderLayout.EAST);
        panel.setBackground(Color.CYAN);
        frame.add(panel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private void conectarAoServidor(String serverAddress, int port) {
        nome = nameField.getText();
        if (nome.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Por favor, insira um nome.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Socket socket = new Socket(serverAddress, port);
            BufferedReader leitor = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            usuario = new PrintWriter(socket.getOutputStream(), true);

            usuario.println(nome + " entrou no chat!");

            Thread receptor = new Thread(() -> {
                try {
                    String mensagemRecebida;
                    while ((mensagemRecebida = leitor.readLine()) != null) {
                        if (!mensagemRecebida.startsWith(nome + ":")) {
                            adicionarMensagem(mensagemRecebida, false);
                        }
                    }
                } catch (IOException ioException) {
                    adicionarMensagem("Finalizado", false);
                }
            });
            receptor.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void enviarMensagem() {
        String mensagem = messageField.getText();
        if (mensagem != null && !mensagem.trim().isEmpty()) {
            usuario.println(nome + ": " + mensagem);
            adicionarMensagem(mensagem, true);
            messageField.setText("");
        }
    }

    private void adicionarMensagem(String mensagem, boolean enviada) {
        JPanel messagePanel = new JPanel();
        messagePanel.setLayout(new FlowLayout(enviada ? FlowLayout.RIGHT : FlowLayout.LEFT));
        messagePanel.setBorder(new EmptyBorder(5, 10, 5, 10));
        messagePanel.setBackground(Color.CYAN);

        JLabel messageLabel = new JLabel(mensagem);
        messageLabel.setOpaque(true);
        messageLabel.setBackground(enviada ? Color.LIGHT_GRAY : Color.GRAY);
        messageLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));


        messagePanel.add(messageLabel);

        messagesBox.add(messagePanel);
        messagesBox.revalidate();
        messagesBox.repaint();


        JScrollBar verticalScrollBar = ((JScrollPane) messagesBox.getParent().getParent()).getVerticalScrollBar();
        SwingUtilities.invokeLater(() -> verticalScrollBar.setValue(verticalScrollBar.getMaximum()));
    }

    public static void main(String[] args) {
        new ClienteComInterface("localhost", 12000);
    }
}
