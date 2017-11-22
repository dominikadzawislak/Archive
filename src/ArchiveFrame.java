import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

public class ArchiveFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	public JTextArea textArea;

	public ArchiveFrame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 791, 428);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		JPanel panel_1 = new JPanel();
		contentPane.add(panel_1, BorderLayout.CENTER);
		panel_1.setLayout(new BorderLayout(0, 0));

		textArea = new JTextArea();
		panel_1.add(textArea, BorderLayout.CENTER);
		textArea.setColumns(10);
		
		JScrollPane scroll = new JScrollPane(textArea); //place the JTextArea in a scroll pane
		panel_1.add(scroll, BorderLayout.CENTER);

		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.EAST);
		panel.setLayout(new GridLayout(5, 0, 0, 0));

		JButton btnNewButton = new JButton("SAVE FILE");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser filec = new JFileChooser();
				if (filec.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					File file = filec.getSelectedFile();
					String path = file.getAbsolutePath();
					path = path.replace("\\", "\\\\");
					textArea.append("Selected file: " + path + "\n");
					try {
						Client.saveFileEvent(path);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					;
				}
			}
		});
		panel.add(btnNewButton);

		JButton btnNewButton_1 = new JButton("DELETE FILE");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String fileNameD = null;
				fileNameD = JOptionPane.showInputDialog("Please enter file to delete (e.g.'abc.txt'): \n");

				try {
					Client.deleteFileEvent(fileNameD);
				} catch (IOException e1) {
					e1.printStackTrace();
				}

			}
		});
		panel.add(btnNewButton_1);

		JButton btnNewButton_2 = new JButton("CREATE .ZIP FILE");
		btnNewButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String fileNameD = null;

				fileNameD = JOptionPane.showInputDialog("Please enter file to transfer to .zip (e.g.'abc.txt'): \n");
				try {
					Client.sendZipFileEvent(fileNameD);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}

		});
		panel.add(btnNewButton_2);

		JButton btnNewButton_3 = new JButton("SHOW ARCHIVE");
		btnNewButton_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					Client.showArchiveEvent();
				} catch (IOException e1) {
					e1.printStackTrace();
				}

			}

		});
		panel.add(btnNewButton_3);

		JButton btnNewButton_4 = new JButton("RETURN FILE");
		btnNewButton_4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String fileNameD = null;

				fileNameD = JOptionPane.showInputDialog("Please enter file to return (e.g.'abc.txt'): \n");

				try {
					Client.sendReturnFileEvent(fileNameD);
				} catch (IOException e1) {
					e1.printStackTrace();
				}

			}
		});
		panel.add(btnNewButton_4);
	}
	
	

}