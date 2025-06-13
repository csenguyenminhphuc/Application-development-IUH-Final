import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import javax.swing.*;

public class ChatInterface extends JFrame {
    private JTextPane chatPane;
    private JTextField inputField;

    public ChatInterface() {
        setTitle("Chatbot Trò Chuyện");
        setSize(500, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        chatPane = new JTextPane();
        chatPane.setContentType("text/html");
        chatPane.setEditable(false);
        chatPane.setText("<html><body></body></html>");
        chatPane.setCaretColor(new Color(0, 0, 0, 0));
        JScrollPane chatScroll = new JScrollPane(chatPane);
        chatScroll.setBorder(BorderFactory.createTitledBorder("Cuộc Trò Chuyện"));
        add(chatScroll, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputField = new JTextField();
        JButton sendButton = new JButton("Gửi");
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        add(inputPanel, BorderLayout.SOUTH);

        sendButton.addActionListener(e -> handleUserMessage());
        inputField.addActionListener(e -> handleUserMessage());
        inputField.requestFocusInWindow();
        setVisible(true);
    }

    private void handleUserMessage() {
        String userInput = inputField.getText().trim();
        if (!userInput.isEmpty()) {
            appendMessage("Bạn", userInput, "right", "#D3D3D3");
            String botResponse = callPythonScript(userInput);
            appendMessage("Chatbot", botResponse, "left", "#ADD8E6");
            inputField.setText("");
            inputField.requestFocusInWindow();
        }
    }

    private String callPythonScript(String userInput) {
        try {
            // Đường dẫn đến python.exe trong môi trường ảo venvai
            String pythonPath = "C:\\Users\\AdminPoi\\Desktop\\Code_PTUD\\Quan-ly-ban-ve-tau-nha-ga\\pythonAI\\venvai\\Scripts\\python.exe";
            String scriptPath = "C:\\Users\\AdminPoi\\Desktop\\Code_PTUD\\Quan-ly-ban-ve-tau-nha-ga\\pythonAI\\chatbot.py";
            
            // Thoát các ký tự đặc biệt trong userInput
            userInput = userInput.replace("\"", "\\\"");
            
            // Gọi script Python với tham số
            ProcessBuilder pb = new ProcessBuilder(pythonPath, scriptPath, userInput);
            pb.redirectErrorStream(true); // Gộp stderr vào stdout
            Process process = pb.start();

            // Đọc đầu ra với mã hóa UTF-8
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8)
            );
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line).append("\n");
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                return "Lỗi khi chạy script Python: exit code " + exitCode + "\nChi tiết lỗi: " + response.toString();
            }
            return response.toString().trim();
        } catch (Exception e) {
            e.printStackTrace();
            return "Lỗi khi gọi script Python: " + e.getMessage();
        }
    }

    private void appendMessage(String sender, String message, String alignment, String bgColor) {
        String currentText = chatPane.getText();
        if (!currentText.contains("<html>")) {
            currentText = "<html><body></body></html>";
        }
        String messageHtml = "<div style='width: 100%; text-align: " + alignment + "; margin: 5px 0;'>" +
                            "<table style='display: inline-block; background-color: " + bgColor + "; " +
                            "border: 1px solid #000; border-radius: 10px; padding: 5px; max-width: 60%;'>" +
                            "<tr><td><b>" + sender + ":</b> " + message + "</td></tr>" +
                            "</table></div>";
        String newText = currentText.replace("</body>", messageHtml + "</body>");
        chatPane.setText(newText);
        chatPane.setCaretPosition(chatPane.getDocument().getLength());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ChatInterface();
        });
    }
}