package visualization.canvas;

import jmath.datatypes.functions.Function2D;
import jmath.datatypes.functions.Function3D;
import jmath.datatypes.functions.Surface;
import jmath.datatypes.tuples.Point2D;
import jmath.datatypes.tuples.Point3D;
import jmath.parser.Function4DParser;
import swingutils.MainFrame;
import utils.Utils;
import visualization.shapes.shapes3d.Area;
import visualization.shapes.shapes3d.Curve3D;
import visualization.shapes.shapes3d.FlatSurface;
import visualization.shapes.shapes3d.Shape3D;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Graph3DCanvas extends Graph2DCanvas {

    public Graph3DCanvas() {
        super();
        setShowGrid(false);
        setShowAxis(false);
        setBackground(Color.BLACK);
        setShowMousePos(false);
        handleRotationByMouse();
    }

    private void handleRotationByMouse() {
        final int[] button = new int[1];
        var mousePoint = new Point();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                button[0] = e.getButton();
                mousePoint.setLocation(e.getPoint());
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            final double mouseSensitivity = 350;

            @Override
            public void mouseDragged(MouseEvent e) {
                int xDif = mousePoint.x - e.getX();
                int yDif = mousePoint.y - e.getY();
                if (e.isControlDown() || e.isAltDown()) {
                    if (button[0] == MouseEvent.BUTTON1) {
                        getRenderManager().forEach(el -> {
                            if (el instanceof Shape3D) {
                                if (e.isControlDown())
                                    ((Shape3D) el).rotate(-yDif / mouseSensitivity, xDif / mouseSensitivity, 0);
                                if (e.isAltDown())
                                    ((Shape3D) el).rotate(new Point3D(), -yDif / mouseSensitivity, xDif / mouseSensitivity, 0);
                            }
                        });
                    } else if (button[0] == MouseEvent.BUTTON3) {
                        getRenderManager().forEach(el -> {
                            if (el instanceof Shape3D) {
                                if (e.isControlDown())
                                    ((Shape3D) el).rotate(0, 0, (yDif+xDif) / mouseSensitivity);
                                if (e.isAltDown())
                                    ((Shape3D) el).rotate(new Point3D(), 0, 0, (yDif+xDif) / mouseSensitivity);
                            }
                        });
                    }
                }
                mousePoint.setLocation(e.getX(), e.getY());
                repaint();
            }
        });
    }

    public void addFunction3DToDraw(String f) {
        var func = Function4DParser.parser(f).f3D(0);
        var area = new Area(this, Utils.randomColor(),
                coordinateX(0), coordinateX(getWidth()),
                coordinateY(getHeight()), coordinateY(0),
                0.1, 0.1, func);
        area.setFill(true);
        area.setThickness(1.5f);
        addRender(area);
        stringBaseMap.put(f, area);
        repaint();
    }

    public static void simplePlotter(List<Point3D> points, CoordinatedCanvas canvas, Graphics2D g2d) {
        var vps = new ArrayList<>(points);
        vps.removeIf(e -> !Double.isFinite(e.x) || !Double.isFinite(e.y));
        var xa = new int[vps.size()];
        var ya = new int[vps.size()];
        int counter = 0;
        for (var p : vps) {
            xa[counter] = canvas.screenX(p.x);
            ya[counter++] = canvas.screenY(p.y);
        }
        g2d.drawPolyline(xa, ya, xa.length);
    }

    @Override
    protected JPanel getSettingPanel() {
        var settingPanel = new JPanel();
        settingPanel.setLayout(new BoxLayout(settingPanel, BoxLayout.Y_AXIS));
        var sp = new JPanel();
        sp.setLayout(new BoxLayout(sp, BoxLayout.Y_AXIS));

        var addFunc = new JButton("Add function3D");

        sp.add(addFunc);
        var wrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        wrapper.add(addFunc);
        sp.add(wrapper);
//        sp.add(getFunction3DList());

        addFunc.addActionListener(e -> addFunction3DToDraw(JOptionPane.showInputDialog("")));

        sp.setBorder(BorderFactory.createTitledBorder("Graph3D Canvas"));
        settingPanel.add(sp);
        settingPanel.add(super.getSettingPanel());
        return settingPanel;
    }

//    private JPanel getFunction3DList() {
//        var list  = new JTable(new DefaultTableModel(new Object[][]{}, new String[] {"No.", "Function3D Expression"})) {
//            @Override
//            public boolean isCellEditable(int row, int column) {
//                return false;
//            }
//        };
//        list.setRowHeight(30);
//        list.getColumnModel().getColumn(0).setMaxWidth(40);
//        var model = (DefaultTableModel) list.getModel();
//        int counter = 0;
//        for (var kv : stringBaseMap.entrySet())
//            if (kv.getValue() instanceof Function2D)
//                model.addRow(new Object[] {++counter, kv.getKey()});
//        var listPanel = new JPanel(new GridLayout(0, 1));
//        listPanel.add(new JScrollPane(list));
//
//        list.addMouseListener(new MouseAdapter() {
//            @Override
//            public void mouseReleased(MouseEvent e) {
//                if (listPanel.getComponentCount() == 2)
//                    listPanel.remove(1);
//                var pp = getFunction2DPropertiesPanel(functions.get(stringBaseMap.get(model.getValueAt(list.getSelectedRow(), 1).toString())));
//                var back = new JButton("Back");
//                back.addActionListener(ev -> {
//                    listPanel.remove(1);
//
//                });
//                var delete = new JButton("Delete");
//                delete.addActionListener(ev -> {
//                    var selected = list.getSelectedRow();
//                    if (selected == -1) {
//                        JOptionPane.showMessageDialog(Graph2DCanvas.this, "you should select a function first", "Error", JOptionPane.ERROR_MESSAGE);
//                        return;
//                    }
//                    var s = model.getValueAt(selected, 1).toString();
//                    functions.remove(stringBaseMap.get(s));
//                    stringBaseMap.remove(s);
//                    listPanel.remove(1);
//                    model.removeRow(selected);
//                    repaint();
//                    revalidate();
//                });
//                pp.add(delete);
//                listPanel.add(pp);
//                listPanel.repaint();
//                listPanel.revalidate();
//            }
//        });
//        listPanel.setPreferredSize(new Dimension(250, 350));
//        return listPanel;
//    }

//    private JPanel getFunction2DPropertiesPanel(Area area) {
//        var panel = new JPanel(new GridLayout(0, 2));
//        var color = new JButton("Color");
//        var precision = new JSlider(1, 3000, );
//        var precisionLabel = new JLabel("Precision: " + Utils.round(1 / (getXScale() * (double) p.get(ACCURACY_RATE)), 4));
//        var thicknessLabel = new JLabel("Thickness: " + p.get(THICKNESS));
//        var thickness = new JSlider(1, 1000, (int) (((float) p.get(THICKNESS)) * 10));
//        var up = new JButton("UpBound");
//        var low = new JButton("LowBound");
//        var visible = new JCheckBox("Visible", (boolean) p.get(IS_VISIBLE));
//        var root = new JCheckBox("ShowRoots", (boolean) p.get(SHOW_ROOTS));
//        var stationary = new JCheckBox("ShowStationaryPoints", (boolean) p.get(SHOW_STATIONARY_POINTS));
//
//        panel.add(color);
//        panel.add(new JLabel());
//        panel.add(precisionLabel);
//        panel.add(precision);
//        panel.add(thicknessLabel);
//        panel.add(thickness);
//        panel.add(up);
//        panel.add(low);
//        panel.add(visible);
//        panel.add(root);
//        panel.add(stationary);
//
//        color.addActionListener(e -> {
//            p.put(COLOR, JColorChooser.showDialog(Graph2DCanvas.this, "ChooseFunctionColor", (Color) p.get(COLOR)));
//            repaint();
//            revalidate();
//        });
//        precision.addChangeListener(e -> {
//            p.put(ACCURACY_RATE, precision.getValue() / 1000d);
//            precisionLabel.setText("Precision: " + Utils.round(1 / (getXScale() * (double) p.get(ACCURACY_RATE)), 4));
//            repaint();
//            revalidate();
//        });
//        thickness.addChangeListener(e -> {
//            p.put(THICKNESS, thickness.getValue() / 10f);
//            thicknessLabel.setText("Thickness: " + thickness.getValue() / 10f);
//            repaint();
//            revalidate();
//        });
//        visible.addActionListener(e -> {
//            p.put(IS_VISIBLE, visible.isSelected());
//            repaint();
//            revalidate();
//        });
//        root.addActionListener(e -> {
//            p.put(SHOW_ROOTS, root.isSelected());
//            repaint();
//            revalidate();
//        });
//        stationary.addActionListener(e -> {
//            p.put(SHOW_STATIONARY_POINTS, stationary.isSelected());
//            repaint();
//            revalidate();
//        });
//        up.addActionListener(e -> {
//            p.put(UP_BOUND, Double.parseDouble(JOptionPane.showInputDialog(Graph2DCanvas.this, "Enter Up Bound: (any exception won't change anything)", p.get(UP_BOUND))));
//            repaint();
//            revalidate();
//        });
//        low.addActionListener(e -> {
//            p.put(LOW_BOUND, Double.parseDouble(JOptionPane.showInputDialog(Graph2DCanvas.this, "Enter Low Bound: (any exception won't change anything)", p.get(LOW_BOUND))));
//            repaint();
//            revalidate();
//        });
//
//        return panel;
//    }


    //    @Override
//    protected void drawAxis(Graphics2D g2d) {
//        var xAxis = new Line3D(this,
//                new Point3D(coordinateX(0), coordinateY(getHeight() / 2 - shiftY), 0),
//                new Point3D(coordinateX(getWidth()), coordinateY(getHeight() / 2 - shiftY), 0),
//                Color.BLUE, 1.8f);
//        var yAxis = new Line3D(this,
//                new Point3D(coordinateX(getWidth() / 2 - shiftX), coordinateY(0), 0),
//                new Point3D(coordinateY(getWidth() / 2 - shiftX), coordinateY(getHeight()), 0),
//                Color.RED, 1.8f);
//        var zAxis = new Line3D(this,
//                new Point3D(0, 0, -50),
//                new Point3D(0, 0, 50),
//                Color.GREEN, 1.8f);
//        try {
//            var c = ((Shape3D) renderManager.getRenderList().get(renderManager.getRenderList().size() - 1)).getCurrentAngle();
//            xAxis.rotate(c.x, c.y, c.z);
//            yAxis.rotate(c.x, c.y, c.z);
//            zAxis.rotate(c.x, c.y, c.z);
//        } catch (Exception ignore) {}
//        //        addRender(xAxis, yAxis, zAxis);
//        //        xAxis.render(g2d);
//        //        yAxis.render(g2d);
//        //        zAxis.render(g2d);
//    }
//
//    @Override
//    protected void drawGrid(Graphics2D g2d) {
//        super.drawGrid(g2d);
//    }
}
