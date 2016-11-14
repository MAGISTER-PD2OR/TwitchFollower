package View;
import StreamList.*;
import net.miginfocom.swing.MigLayout;
import org.gpl.JSplitButton.JSplitButton;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class View extends JFrame{

    private JButton btnAdd, btnRemove;
    private JPanel pnlDisplay, pnlSettings;
    private JScrollPane scrDisplay;
    private JLabel selected, header, lblSettings, lblDisplay;
    private JSeparator sepSettings, sepDisplay;
    private JSplitButton sbtnOpen;

    private static final Color TWITCH_PURPLE = new Color(100, 65, 164);
    private static final Color BORDER_COLOR = Color.white;
    private static final Color TEXT_COLOR = TWITCH_PURPLE;
    private static final Color SELECT_FOREGROUND_COLOR = Color.white;
    private static final Color SELECT_BACKGROUND_COLOR = new Color(123, 90, 204);
    private static final Color BACKGROUND_COLOR = Color.white;
    private static final Color BACKGROUND_LABEL_COLOR = TWITCH_PURPLE;
    private static final Color FOREGROUND_LABEL_COLOR = Color.white;

    public View(){
        setLookAndFeel();
        setName("Twitch Follower");
        setTitle("Twitch Follower");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(600, 300));
        setSize(new Dimension(600, 600));
        getContentPane().setBackground(BACKGROUND_COLOR);
        setLayout(new MigLayout());

        initComponents();
    }

    private void initComponents(){
        pnlDisplay = new JPanel();
        pnlDisplay.setLayout(new MigLayout("wrap 2, flowx"));
        pnlDisplay.setMaximumSize(new Dimension(getWidth(), getHeight()-250));
        pnlDisplay.setAutoscrolls(true);
        pnlDisplay.setBackground(BACKGROUND_COLOR);

        pnlSettings = new JPanel();
        pnlSettings.setLayout(new MigLayout());
        pnlSettings.setBackground(BACKGROUND_COLOR);

        scrDisplay = new JScrollPane(pnlDisplay);
        scrDisplay.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrDisplay.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrDisplay.setBorder(BorderFactory.createEmptyBorder());
        scrDisplay.getVerticalScrollBar().setUnitIncrement(5);
        scrDisplay.setBackground(BACKGROUND_COLOR);

        btnAdd = new JButton("+");
        btnAdd.setFocusable(false);

        btnRemove = new JButton("-");
        btnRemove.setFocusable(false);

        try{
            BufferedImage img = ImageIO.read(getClass().getClassLoader().getResource("resources/Header3.png"));
            ImageIcon icon = new ImageIcon(img);
            header = new JLabel("", icon, JLabel.LEFT);

            img = ImageIO.read(getClass().getClassLoader().getResource("resources/Twitch.png"));
            setIconImage(img);
            header.addMouseListener(new HeaderListener());
        }catch (IOException e){
            System.out.println("Error reading GUI graphics");
        }

        sepSettings = new JSeparator(SwingConstants.HORIZONTAL);
        sepSettings.setBackground(TWITCH_PURPLE);

        sepDisplay = new JSeparator(SwingConstants.HORIZONTAL);
        sepDisplay.setBackground(TWITCH_PURPLE);

        lblSettings = new JLabel("<html><b>Settings");
        lblSettings.setForeground(BACKGROUND_LABEL_COLOR);

        lblDisplay = new JLabel("<html><b>Streams");
        lblDisplay.setForeground(BACKGROUND_LABEL_COLOR);

        pnlSettings.add(btnAdd, "west, gapy 5");
        pnlSettings.add(btnRemove, "west, gapx 5, gapy 5, pushx");

        add(header, "wrap, align center");
        add(lblDisplay, "al left, wrap, gapx 10, gapy 10");
        add(sepDisplay, "w 250, wrap");
        add(scrDisplay, "pushx, pushy, grow, wrap");
        add(lblSettings, "al left, wrap, gapx 10");
        add(sepSettings, "w 250, wrap");
        add(pnlSettings, "grow, pushx");

        MouseListener deselectListener = new DeselectListener();
        addComponentListener(new ResizeListener());
        addMouseListener(deselectListener);
        pnlDisplay.addMouseListener(deselectListener);

    }

    private void setLookAndFeel(){
        try{
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }catch(ClassNotFoundException|InstantiationException|IllegalAccessException|UnsupportedLookAndFeelException e){
            System.out.println(e.getMessage());
        }
    }

    // Listeners

    public void btnAddListener(ActionListener listener){
        btnAdd.addActionListener(listener);
    }

    public void btnRemoveListener(ActionListener listener){
        btnRemove.addActionListener(listener);
    }


    // Getters
    public JLabel getSelected(){
        return selected;
    }

    public JPanel getDisplayPanel(){
        return pnlDisplay;
    }

    public String getSbtnOpenText(){
        return sbtnOpen.getText();
    }


    // Setters
    public void setSelected(JLabel selected){
        this.selected = selected;
    }


    //  Other
    public void addStreamLabel(StreamNode temp){
        String status = "<font color=#e62e00> Offline";

        if(temp.getStatus().equals("Online")){
            status = "<font color=#66ff66> Online";
        }

        String labelText = "<html><body style='width: 100%'><b>" + temp.getName() +
                "</b><i>" + status + "</i><br>" + temp.getGame() +"</html>";

        JLabel tempLabel = new JLabel(labelText);

        Border paddingBorder = BorderFactory.createEmptyBorder(7, 7, 7, 0);
        Border border = BorderFactory.createLineBorder(BORDER_COLOR, 1);

        tempLabel.setIcon(new ImageIcon(temp.getLogo()));
        tempLabel.setOpaque(true);
        tempLabel.setBorder(BorderFactory.createCompoundBorder(border, paddingBorder));
        tempLabel.addMouseListener(new SelectListener());
        tempLabel.setName(temp.getName());

        setDeselectProperties(tempLabel);

        if(selected != null && tempLabel.getName().equals(selected.getName())){
            setSelectProperties(tempLabel);
            selected = tempLabel;
        }

        if(temp.getStatus().equals("Online")){
            pnlDisplay.add(tempLabel, "pushx, grow", 0);
        }else{
            pnlDisplay.add(tempLabel, "pushx, grow");
        }

    }

    public void setSelectProperties(JLabel label){
        label.setBackground(SELECT_BACKGROUND_COLOR);
        label.setForeground(SELECT_FOREGROUND_COLOR);
    }

    public void setDeselectProperties(JLabel label){
        label.setBackground(BACKGROUND_LABEL_COLOR);
        label.setForeground(FOREGROUND_LABEL_COLOR);
    }

    // Listeners
    private class ResizeListener implements ComponentListener {
        @Override
        public void componentResized(ComponentEvent e) {
            JPanel pnlDisplay = getDisplayPanel();
            pnlDisplay.setSize(new Dimension(getWidth(), getHeight()-250));
            pnlDisplay.setMaximumSize(new Dimension(getWidth(), getHeight()-250));

            validate();
            repaint();
        }

        @Override
        public void componentMoved(ComponentEvent e) {}

        @Override
        public void componentShown(ComponentEvent e) {}

        @Override
        public void componentHidden(ComponentEvent e) {}
    }

    private class HeaderListener implements MouseListener{
        @Override
        public void mouseClicked(MouseEvent e) {
            if(Desktop.isDesktopSupported()){
                try{
                    Desktop.getDesktop().browse(new URI("http://www.twitch.tv/directory"));
                }catch(URISyntaxException |IOException ex){
                    System.out.println(ex.getMessage());
                }
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {}

        @Override
        public void mouseReleased(MouseEvent e) {}

        @Override
        public void mouseEntered(MouseEvent e) {
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        @Override
        public void mouseExited(MouseEvent e) {
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }
    }

    private class SelectListener implements MouseListener{

        @Override
        public void mouseClicked(MouseEvent e) {
            if(selected != null){
                setDeselectProperties(selected);
            }

            if(e.getComponent() != selected){
                setSelectProperties((JLabel) e.getComponent());
                selected = (JLabel)e.getComponent();
            }else{
                selected = null;
            }

            if(SwingUtilities.isRightMouseButton(e)){
                PopupMenu popup = new PopupMenu(View.this);
                popup.show(e.getComponent(), e.getX(), e.getY());
            }

            validate();
            repaint();

        }

        @Override
        public void mousePressed(MouseEvent e) {}

        @Override
        public void mouseReleased(MouseEvent e) {}

        @Override
        public void mouseEntered(MouseEvent e) {
            setSelectProperties((JLabel)e.getComponent());
        }

        @Override
        public void mouseExited(MouseEvent e) {
            if(e.getComponent() != selected){
                setDeselectProperties((JLabel)e.getComponent());
            }

        }
    }

    private class DeselectListener implements MouseListener{
        @Override
        public void mouseClicked(MouseEvent e) {
            if(selected != null){
                setDeselectProperties(selected);
                selected = null;
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {

        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }
    }

}