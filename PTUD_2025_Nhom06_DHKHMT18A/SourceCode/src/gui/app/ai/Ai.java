/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package gui.app.ai;

import entity.NhanVien;
import gui.app.trogiup.*;
import gui.app.trangchu.*;
import gui.app.hoso.*;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

/**
 *
 * @author AdminPoi
 */
public class Ai extends javax.swing.JPanel {

    /**
     * Creates new form TaiKhoanNV
     */
    public Ai(NhanVien nv) {
        initComponents();
        
        header2.jLabel14.setText("AI chat");
        header2.jLabel13.setText("Chào Nhân Viên");
        header2.jButton4.setText(nv.getTenNV());
            // Ẩn con trỏ chuột trong jTextPane1 (khu vực trò chuyện)
        jTextPane1.setCaretColor(new Color(0, 0, 0, 0));
        jTextPane1.setEditable(false);
        jTextPane1.setContentType("text/html");
        jTextPane1.setText("<html><body></body></html>");

        // Đặt focus mặc định cho jTextPane2 (ô nhập liệu)
        jTextPane2.requestFocusInWindow();
    }

    
    
    
        // Xử lý tin nhắn của người dùng
    private void handleUserMessage() {
        String userInput = jTextPane2.getText().trim();
        if (!userInput.isEmpty()) {
            // Hiển thị tin nhắn của người dùng (bên phải)
            appendMessage("Bạn", userInput, "right", "#D3D3D3");

            // Gọi script Python để lấy phản hồi từ chatbot
            String botResponse = callPythonScript(userInput);
            appendMessage("AIGPT", botResponse, "left", "#ADD8E6");

            // Xóa ô nhập liệu
            jTextPane2.setText("");
            jTextPane2.requestFocusInWindow();
        }
    }

    // Gọi script Python
    private String callPythonScript(String userInput) {
        try {
            // Lấy thư mục chứa lớp Java (thường là bin/gui/app/ai/ hoặc src/gui/app/ai/)
            String classDir = Paths.get(this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI())
                                  .getParent().toString();

            // Đi lên 4 cấp để đến thư mục gốc dự án (Quan-ly-ban-ve-tau-nha-ga/)
            String projectDir = Paths.get(classDir, "..").normalize().toString();

            // Đường dẫn đến python.exe trong môi trường ảo
            String pythonPath = Paths.get(projectDir, "pythonAI", "venvai", "Scripts", "python.exe").normalize().toString();
            // Đường dẫn đến chatbot.py
            String scriptPath = Paths.get(projectDir, "pythonAI", "chatbot.py").normalize().toString();
            
            // Thoát các ký tự đặc biệt trong userInput
            userInput = userInput.replace("\"", "\\\"");

            // Gọi script Python với tham số
            ProcessBuilder pb = new ProcessBuilder(pythonPath, scriptPath, userInput);
            pb.redirectErrorStream(true);
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

    // Thêm tin nhắn vào khu vực trò chuyện
        private void appendMessage(String sender, String message, String alignment, String bgColor) {
            String currentText = jTextPane1.getText();
            if (!currentText.contains("<html>")) {
                currentText = "<html><body></body></html>";
            }
            String messageHtml = "<div style='width: 100%; text-align: " + alignment + "; margin: 5px 0;'>" +
                                "<table style='display: inline-block; background-color: " + bgColor + "; " +
                                "border: 1px solid #000; border-radius: 10px; padding: 5px; max-width: 60%;'>" +
                                "<tr><td><b>" + sender + ":</b> " + message + "</td></tr>" +
                                "</table></div>";
            String newText = currentText.replace("</body>", messageHtml + "</body>");
            jTextPane1.setText(newText);
            jTextPane1.setCaretPosition(jTextPane1.getDocument().getLength());
        }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextPane1 = new javax.swing.JTextPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextPane2 = new javax.swing.JTextPane();
        jButton3 = new javax.swing.JButton();
        header2 = new component.Header();

        setPreferredSize(new java.awt.Dimension(1200, 800));

        jScrollPane1.setViewportView(jTextPane1);

        jTextPane2.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jScrollPane2.setViewportView(jTextPane2);

        jButton3.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jButton3.setText("Chat");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 1094, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 668, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane2)
                    .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, 44, Short.MAX_VALUE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(header2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(header2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        handleUserMessage();
    }//GEN-LAST:event_jButton3ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private component.Header header2;
    private javax.swing.JButton jButton3;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextPane jTextPane1;
    private javax.swing.JTextPane jTextPane2;
    // End of variables declaration//GEN-END:variables
}
