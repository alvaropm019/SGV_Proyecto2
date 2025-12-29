/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Archivos;

/**
 *
 * @author jorge
 */
import traffic.Traffic;
import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;

// >>> VENTANA FLOTANTE DE COMPARACION
// >>> Esta clase extiende JDialog para crear una ventana secundaria independiente.
// >>> Su objetivo es mostrar los datos de dos aviones lado a lado (Columna A vs Columna B).
public class TrafficComparisonDialog extends JDialog {

    // >>> Etiquetas (Labels) dinamicas.
    // >>> Guardamos estas referencias para poder cambiar su texto (setText) en tiempo real
    // >>> cuando los aviones se muevan.
    private JLabel lblCallsign1, lblCallsign2;
    private JLabel lblAlt1, lblAlt2;
    private JLabel lblSpeed1, lblSpeed2;
    private JLabel lblVertRate1, lblVertRate2;

    // >>> Formateador numerico para que la velocidad se vea limpia (ej: "450" en vez de "450.234")
    private DecimalFormat df = new DecimalFormat("000");

    public TrafficComparisonDialog(Frame owner) {
        // >>> Configuracion de la ventana.
        // >>> 'false' (modal) significa que podemos seguir clicando en el radar con esta ventana abierta.
        super(owner, "Comparador de Vuelos", false);
        this.setSize(400, 300);
        this.setLayout(new BorderLayout());

        // >>> Titulo superior estetico
        JLabel title = new JLabel("COMPARATIVA EN TIEMPO REAL", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 14));
        title.setOpaque(true);
        title.setBackground(Color.DARK_GRAY);
        title.setForeground(Color.ORANGE);
        this.add(title, BorderLayout.NORTH);

        // >>> PANEL CENTRAL (GRIDLAYOUT)
        // >>> Usamos un Grid (Rejilla) de 3 columnas para alinear perfectamente los datos:
        // >>> [ Etiqueta ] [ Valor Avion 1 ] [ Valor Avion 2 ]
        JPanel contentPanel = new JPanel(new GridLayout(0, 3, 10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        contentPanel.setBackground(Color.BLACK); // Fondo negro para estilo radar

        // Cabeceras de la tabla
        addHeader(contentPanel, "DATO");
        addHeader(contentPanel, "AVION A");
        addHeader(contentPanel, "AVION B");

        // >>> Creacion de filas.
        // >>> En cada fila añadimos: 1. Nombre del dato, 2. Label vacio A, 3. Label vacio B.
        addLabel(contentPanel, "Callsign:", Color.GRAY);
        lblCallsign1 = createValLabel(Color.GREEN);
        contentPanel.add(lblCallsign1);
        lblCallsign2 = createValLabel(Color.GREEN);
        contentPanel.add(lblCallsign2);

        addLabel(contentPanel, "Altitud (ft):", Color.GRAY);
        lblAlt1 = createValLabel(Color.WHITE);
        contentPanel.add(lblAlt1);
        lblAlt2 = createValLabel(Color.WHITE);
        contentPanel.add(lblAlt2);

        addLabel(contentPanel, "Velocidad:", Color.GRAY);
        lblSpeed1 = createValLabel(Color.WHITE);
        contentPanel.add(lblSpeed1);
        lblSpeed2 = createValLabel(Color.WHITE);
        contentPanel.add(lblSpeed2);

        addLabel(contentPanel, "V. Rate:", Color.GRAY);
        lblVertRate1 = createValLabel(Color.WHITE);
        contentPanel.add(lblVertRate1);
        lblVertRate2 = createValLabel(Color.WHITE);
        contentPanel.add(lblVertRate2);

        this.add(contentPanel, BorderLayout.CENTER);
    }

    // >>> Metodos auxiliares para no repetir codigo al crear etiquetas
    private void addHeader(JPanel p, String text) {
        JLabel l = new JLabel(text, SwingConstants.CENTER);
        l.setForeground(Color.ORANGE);
        l.setFont(new Font("Arial", Font.BOLD, 12));
        p.add(l);
    }

    private void addLabel(JPanel p, String text, Color c) {
        JLabel l = new JLabel(text);
        l.setForeground(c);
        p.add(l);
    }

    private JLabel createValLabel(Color c) {
        JLabel l = new JLabel("-");
        l.setForeground(c);
        l.setHorizontalAlignment(SwingConstants.CENTER);
        return l;
    }

    // >>> METODO DE ACTUALIZACION (CORE)
    // >>> Este metodo es llamado desde RadarPanel.paintComponent() muchas veces por segundo.
    // >>> Recibe los dos objetos Traffic seleccionados y vuelca sus datos en las etiquetas.
    public void updateComparison(Traffic t1, Traffic t2) {
        // Proteccion contra nulos (por si un avion desaparece mientras comparamos)
        if (t1 == null || t2 == null) {
            return;
        }

        lblCallsign1.setText(t1.getCallsign());
        lblCallsign2.setText(t2.getCallsign());

        // Convertimos a entero para quitar decimales molestos en altitud
        lblAlt1.setText(String.valueOf((int) t1.getAlt_ext()));
        lblAlt2.setText(String.valueOf((int) t2.getAlt_ext()));

        lblSpeed1.setText(df.format(t1.getGs()));
        lblSpeed2.setText(df.format(t2.getGs()));

        lblVertRate1.setText(String.valueOf((int) t1.getVr()));
        lblVertRate2.setText(String.valueOf((int) t2.getVr()));
    }
}
