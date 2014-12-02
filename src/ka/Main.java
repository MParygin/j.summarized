package ka;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.apache.commons.io.FileUtils;

/**
 * Main class
 */
public class Main extends JFrame implements ActionListener, ChangeListener {
    
    private static final String ACTION_FILE = "file";
    private static final String ACTION_SUMMARIZE = "summarize";
    
    int percent = 100;
    JTextArea area;
    JComboBox<Dictionary> combo;
    JLabel lpercent;
    JLabel info;
    String text;
    Article article;
    
    public Main(String title) throws HeadlessException {
        super(title);
        
        // top panel
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnFile = new JButton("File");
        btnFile.setActionCommand(ACTION_FILE);
        btnFile.addActionListener(this);
        top.add(btnFile);
        this.combo = Dictionaries.getCombo();
        top.add(this.combo);
        JButton btnSum = new JButton("Summarize");
        btnSum.setActionCommand(ACTION_SUMMARIZE);
        btnSum.addActionListener(this);
        top.add(btnSum);
        JSlider slider = new JSlider(0, 100, this.percent);
        slider.addChangeListener(this);
        top.add(slider);
        this.lpercent = new JLabel(this.percent + " %");
        top.add(this.lpercent);
        slider.setValue(this.percent);
        
        // area
        this.area = new JTextArea();
        this.area.setLineWrap(true);
        
        // info
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        this.info = new JLabel();
        bottom.add(this.info);
        
        // root panel
        JPanel root = new JPanel(new BorderLayout());
        root.add(top, BorderLayout.NORTH);
        root.add(new JScrollPane(this.area, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER), BorderLayout.CENTER);
        root.add(bottom, BorderLayout.SOUTH);
        
        // this frame
        getContentPane().add(root);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String action = e.getActionCommand();
        if (ACTION_FILE.equals(action)) {
            // read file
            JFileChooser ch = new JFileChooser(".");
            ch.setMultiSelectionEnabled(false);
            if (ch.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) {
                return;
            }
            try {
                // read the file
                this.text = FileUtils.readFileToString(ch.getSelectedFile());
                this.area.setText(this.text);
                this.info.setText("Read file: " + ch.getSelectedFile().getName());
            } catch (IOException ex) {
            }
        } else if (ACTION_SUMMARIZE.equals(action)) {
            // summarize
            Dictionary dictionary = this.combo.getItemAt(this.combo.getSelectedIndex());
            this.article = new Article(dictionary.rule, this.area.getText());
            this.info.setText("Lines: " + article.lineCount());
            this.article.grade();
        }
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        this.percent = ((JSlider)e.getSource()).getValue();
        this.lpercent.setText(this.percent + " %");
        if (this.article != null) {
            // 1) get maximum score
            double max = this.article.getMaxScore();
            // 2) calc level
            double level = (100 - this.percent) * max / 100.0;
            // 3) build text
            this.area.setText(this.article.filter(level));
            this.info.setText("Filtered by level: " + this.percent + "%");
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new Main("JSummarized");
    }


}
