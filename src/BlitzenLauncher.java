package fulgor.launcher;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class BlitzenLauncher {
    public static void Init(){
        var frame = new JFrame("Blitzen Launcher");
        frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);
        frame.setSize(400, 200);
        frame.setLayout(new GridLayout(3, 1));
        var blitzenPathField = new JTextField();
        var projectNameField = new JTextField();
        var launchButton = new JButton("Launch Blitzen");
        frame.add(new LabeledPanel("Blitzen Folder:", blitzenPathField));
        frame.add(new LabeledPanel("Project Name:", projectNameField));
        frame.add(launchButton);
        launchButton.addActionListener(e -> {
            String blitzenPath = blitzenPathField.getText();
            String projectName = projectNameField.getText();

            if (blitzenPath.isEmpty() || projectName.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please fill in all fields.");
                return;
            }

            var bilbo = new File(blitzenPath + "/bilbo/bilbo.py");

            if (!bilbo.exists()) {
                JOptionPane.showMessageDialog(frame, "bilbo.py not found in Blitzen path.");
                return;
            }

            try {
                var pb = new ProcessBuilder("python", bilbo.getAbsolutePath(), projectName);
                pb.inheritIO();
                var process = pb.start();

                var reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }

                int exitCode = process.waitFor(); // wait for Bilbo to finish
                System.out.println("Bilbo exited with " + exitCode);

                if(exitCode == 0){
                    var blitzenExe = new File(blitzenPath + "/build/Debug/BlitzenEngine.exe");
                    if(blitzenExe.exists()){
                        new ProcessBuilder(blitzenExe.getAbsolutePath()).directory(blitzenExe.getParentFile()).start();
                    }
                    else{
                        JOptionPane.showMessageDialog(frame, "Blitzen.exe not found.");
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Failed to launch Bilbo:\n" + ex.getMessage());
            }
        });
        frame.setVisible(true);
    }

    static class LabeledPanel extends JPanel {
        public LabeledPanel(String label, JTextField field) {
            setLayout(new BorderLayout());
            add(new JLabel(label), BorderLayout.WEST);
            add(field, BorderLayout.CENTER);
        }
    }
}
