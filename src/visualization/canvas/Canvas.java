package visualization.canvas;

import com.sun.management.OperatingSystemMXBean;
import swingutils.MainFrame;
import utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.ConcurrentModificationException;

@SuppressWarnings("unused")
public class Canvas extends JLayeredPane implements Runnable {
    public static final int DEFAULT_REDRAW_DELAY = 4;
    private static int numberOfPanel = 0;
    private final Timer redrawTimer;
    private final RenderManager renderManager;
    protected Color backGround;
    private Image bgImage;
    private int fps;
    private long lastTime;
    private double timePerTick;
    private double delta;
    private long timer;
    private long loopCounter;
    private long ticksCounter;
    private long renderCounter;
    private Color infoColor;
    protected Font infoFont;
    private boolean showInfo;
    private boolean showBgImg;

    public Canvas() {
        redrawTimer = new Timer(DEFAULT_REDRAW_DELAY, e -> this.run());
        renderManager = new RenderManager();
        init();
    }

    private void init() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(MainFrame.DEFAULT_WIDTH, MainFrame.DEFAULT_HEIGHT));
        setName("Canvas: Id=" + numberOfPanel++);
        backGround = Color.DARK_GRAY.darker();
        loopCounter = 0;
        ticksCounter = 0;
        renderCounter = 0;
        delta = 0;
        timer = 1;
        bgImage = null;
        infoColor = Color.GREEN.darker();
        infoFont = new Font("serif", Font.BOLD, 11);
        showInfo = true;
        showBgImg = true;
        setFps(30);
        stop();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1 && getComponentCount() != 0 && e.isShiftDown())
                    removeSettingPanel();
                if (e.getButton() == MouseEvent.BUTTON3 && getComponentCount() == 0 && e.isShiftDown())
                    addSettingPanel();
            }
        });
    }

    protected JPanel getSettingPanel() {
        var settingPanel = new JPanel(new GridLayout(0, 2));
        var fps = new JButton("Set FPS");
        var start = new JButton("Start");
        var stop = new JButton("Stop");
        var showInfo = new JCheckBox("Show Info", null, isShowInfo());
        var showBgImg = new JCheckBox("Show BgImg", null, isShowBgImg());
        var changeBgColor = new JButton("BgColor");
        var setBgImg = new JButton("Background Image");
        var setInfoColor = new JButton("Info Font Color");
        var setInfoFontSize = new JButton("Info Font Size");
        var capture = new JButton("Capture");
        var tooltip = new JButton("TooltipText");

        settingPanel.add(fps);
        settingPanel.add(new JLabel(getName(), JLabel.CENTER));
        settingPanel.add(start);
        settingPanel.add(stop);
        settingPanel.add(showInfo);
        settingPanel.add(showBgImg);
        settingPanel.add(changeBgColor);
        settingPanel.add(setInfoColor);
        settingPanel.add(setInfoFontSize);
        settingPanel.add(setBgImg);
        settingPanel.add(capture);
        settingPanel.add(tooltip);

        start.addActionListener(e -> start());
        stop.addActionListener(e -> stop());
        showInfo.addActionListener(e -> setShowInfo(showInfo.isSelected()));
        showBgImg.addActionListener(e -> setShowBgImg(showBgImg.isSelected()));
        fps.addActionListener(e -> setFps(Integer.parseInt(JOptionPane.showInputDialog(Canvas.this, "Enter new FPS: (If any exception occurred nothing will change)", getFps()))));
        changeBgColor.addActionListener(e -> setBackground(JColorChooser.showDialog(Canvas.this, "Choose Background Color", getBackGround())));
        setInfoColor.addActionListener(e -> setInfoColor(JColorChooser.showDialog(Canvas.this, "Choose InfoText Color", getBackGround())));
        setBgImg.addActionListener(e -> setBgImage(JOptionPane.showInputDialog(Canvas.this, "Enter path of image: (If any exception occurred nothing will change)")));
        tooltip.addActionListener(e -> setToolTipText(JOptionPane.showInputDialog(Canvas.this, "Enter Tooltip Text of Canvas: (If any exception occurred nothing will change)", getToolTipText())));
        setInfoFontSize.addActionListener(e -> setInfoFont(new Font("serif", Font.BOLD, Integer.parseInt(JOptionPane.showInputDialog(Canvas.this, "Enter size of info font: (If any exception occurred nothing will change)", infoFont.getSize())))));
        capture.addActionListener(e -> Utils.saveJComponentImage(System.nanoTime() + "", Canvas.this));

        settingPanel.setBorder(BorderFactory.createTitledBorder("Plain Canvas"));
        return settingPanel;
    }

    public boolean isShowBgImg() {
        return showBgImg;
    }

    public void setShowBgImg(boolean showBgImg) {
        this.showBgImg = showBgImg;
        repaint();
    }

    public synchronized void start() {
        lastTime = System.nanoTime();
        redrawTimer.start();
        repaint();
    }

    public synchronized void stop() {
        redrawTimer.stop();
        repaint();
    }

    public RenderManager getRenderManager() {
        return renderManager;
    }

    public int getFps() {
        return fps;
    }

    public void setFps(int fps) {
        if (fps <= 0) {
            stop();
            return;
        }
        this.fps = fps;
        redrawTimer.setDelay(fps > 50 ? 0 : DEFAULT_REDRAW_DELAY);
        resetLoopInfo();
        start();
    }

    private void resetLoopInfo() {
        timer = 1;
        delta = 0;
        loopCounter = 0;
        ticksCounter = 0;
        renderCounter = 0;
        timePerTick = 1_000_000_000 / (double) fps;
    }

    public Image getBgImage() {
        return bgImage;
    }

    public void setBgImage(Image bgImage) {
        this.bgImage = bgImage;
        repaint();
    }

    public void setBgImage(String imgPath) {
        if (imgPath == null || !new File(imgPath).exists())
            return;
        setBgImage(new ImageIcon(imgPath).getImage());
    }

    public Color getBackGround() {
        return backGround;
    }

    public long getLoopCounter() {
        return loopCounter;
    }

    public long getTicksCounter() {
        return ticksCounter;
    }

    public double getTimePerTick() {
        return timePerTick;
    }

    public long getRealFps() {
        return renderCounter * 1_000_000_000 / timer + 1;
    }

    public long getRealTps() {
        return ticksCounter * 1_000_000_000 / timer + 1;
    }

    public long getLps() {
        return loopCounter * 1_000_000_000 / timer + 1;
    }

    public boolean isRunning() {
        return redrawTimer.isRunning();
    }

    public long getRenderCounter() {
        return renderCounter;
    }

    public Color getInfoColor() {
        return infoColor;
    }

    public void setInfoColor(Color infoColor) {
        this.infoColor = infoColor;
        repaint();
    }

    public Font getInfoFont() {
        return infoFont;
    }

    public void setInfoFont(Font infoFont) {
        this.infoFont = infoFont;
        repaint();
    }

    public boolean isShowInfo() {
        return showInfo;
    }

    public void setShowInfo(boolean showInfo) {
        this.showInfo = showInfo;
        repaint();
    }

    public void addRender(Render... renders) {
        renderManager.addRender(renders);
        repaint();
        revalidate();
    }

    public final void addSettingPanel() {
        removeSettingPanel();
        var p = getSettingPanel();
        p.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 18));
        add(new JScrollPane(p, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER), BorderLayout.EAST);
        repaint();
        revalidate();
    }

    public final void removeSettingPanel() {
        try {
            remove(0);
        } catch (Exception ignore) {}
        revalidate();
        repaint();
    }

    @Override
    public void setBackground(Color bg) {
        backGround = bg;
        repaint();
    }

    @Override
    public synchronized void paintComponent(Graphics g) {
        if (g == null)
            return;
        var g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (bgImage == null || !showBgImg) {
            g2d.setColor(backGround);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        } else {
            g2d.drawImage(bgImage, 0, 0, getWidth(), getHeight(), null);
        }

        try {
            renderManager.render(g2d);
        } catch (ConcurrentModificationException ignored) {}

        if (!showInfo)
            return;

        g2d.setFont(infoFont);
        g2d.setColor(infoColor);
        if (!isRunning()) {
            g2d.drawString("Not Dynamic", 0, 10);
            return;
        }

        g2d.drawString(
                "FPS: " + getRealFps() +
                        ", TPS: " + getRealTps() +
                        ", CPU: " + Utils.round(((OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean()).getProcessCpuLoad() * 100, 2) + "%"
                , 0, (int) (infoFont.getSize() * 0.8)
        );
    }

    @Override
    public final void run() {
        long now = System.nanoTime();
        delta += (now - lastTime) / timePerTick;
        timer += now - lastTime;
        lastTime = now;
        boolean flag = delta >= 1;

        while (delta >= 1) {
            renderManager.tick();
            delta--;
            ticksCounter++;
        }

        if (flag) {
            repaint();
            renderCounter++;
        }

        loopCounter++;
    }
}
