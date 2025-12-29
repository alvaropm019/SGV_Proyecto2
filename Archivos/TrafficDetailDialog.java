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

/**
 * // >>> VENTANA DE DETALLES INDIVIDUAL // >>> Esta clase crea una ventana
 * flotante (JDialog) que muestra toda // >>> la informacion tecnica de un solo
 * avion seleccionado. // >>> Se actualiza en tiempo real desde RadarPanel.
 */
public class TrafficDetailDialog extends JDialog {

    // >>> Etiquetas para mostrar los valores en pantalla
    private JLabel lblCallsign, lblHex, lblSquawk;
    private JLabel lblAlt, lblSpeed, lblVertRate, lblTrack;
    private JLabel lblLat, lblLon;

    private JPanel contentPanel;

    // >>> Formateadores para que los numeros queden bonitos
    // >>> Ejemplo: Coordenadas con 4 decimales, velocidad sin decimales
    private DecimalFormat dfCoord = new DecimalFormat("0.0000");
    private DecimalFormat dfAlt = new DecimalFormat("00000");
    private DecimalFormat dfSpeed = new DecimalFormat("000");

    public TrafficDetailDialog(Frame owner) {
        // >>> Configuracion basica de la ventana flotante
        // >>> 'false' significa que no bloquea el resto del programa (ventana no modal)
        super(owner, "Ficha de Vuelo", false);
        this.setSize(300, 400);
        this.setLayout(new BorderLayout());

        // >>> Titulo superior con estilo oscuro
        JLabel title = new JLabel("INFORMACION DE TRAFICO", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 14));
        title.setOpaque(true);
        title.setBackground(Color.DARK_GRAY);
        title.setForeground(Color.CYAN);
        title.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        this.add(title, BorderLayout.NORTH);

        // >>> Panel central con los datos (Grid de 2 columnas)
        // >>> Columna 1: Nombre del dato, Columna 2: Valor del dato
        contentPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        contentPanel.setBackground(Color.BLACK);

        // >>> Inicializamos las etiquetas con valores por defecto y colores
        lblCallsign = createLabel("N/A", Color.GREEN);
        lblHex = createLabel("N/A", Color.LIGHT_GRAY);
        lblSquawk = createLabel("N/A", Color.YELLOW);
        lblAlt = createLabel("N/A", Color.WHITE);
        lblSpeed = createLabel("N/A", Color.WHITE);
        lblVertRate = createLabel("N/A", Color.WHITE);
        lblTrack = createLabel("N/A", Color.WHITE);
        lblLat = createLabel("N/A", Color.LIGHT_GRAY);
        lblLon = createLabel("N/A", Color.LIGHT_GRAY);

        // >>> Añadimos las filas al panel usando un metodo auxiliar
        addRow("Callsign:", lblCallsign);
        addRow("Hex ID:", lblHex);
        addRow("Squawk:", lblSquawk);
        addRow("----------------", new JLabel("")); // Separador estetico
        addRow("Altitud (ft):", lblAlt);
        addRow("Velocidad (kts):", lblSpeed);
        addRow("V. Rate (fpm):", lblVertRate);
        addRow("Rumbo (deg):", lblTrack);
        addRow("----------------", new JLabel("")); // Separador estetico
        addRow("Latitud:", lblLat);
        addRow("Longitud:", lblLon);

        this.add(contentPanel, BorderLayout.CENTER);
    }

    // >>> Metodo auxiliar para añadir una fila (Texto + Valor) al panel
    private void addRow(String name, JComponent component) {
        JLabel lblName = new JLabel(name);
        lblName.setForeground(Color.GRAY);
        lblName.setFont(new Font("Consolas", Font.BOLD, 12));
        contentPanel.add(lblName);
        contentPanel.add(component);
    }

    // >>> Metodo auxiliar para dar estilo a los valores (Fuente Consolas y color)
    private JLabel createLabel(String text, Color color) {
        JLabel l = new JLabel(text);
        l.setForeground(color);
        l.setFont(new Font("Consolas", Font.BOLD, 14));
        return l;
    }

    // >>> METODO PRINCIPAL DE ACTUALIZACION
    // >>> Este metodo es llamado constantemente para refrescar los datos
    // >>> cuando el avion se mueve o cambia de estado.
    public void updateInfo(Traffic t) {
        if (t == null) {
            return;
        }

        // 1. CALLSIGN (Identificador de vuelo)
        lblCallsign.setText(t.getCallsign());

        // 2. ICAO / HEX (Codigo unico del transpondedor)
        try {
            lblHex.setText(t.getICAO24());
        } catch (Exception e) {
            lblHex.setText("---");
        }

        // 3. SQUAWK (Codigo temporal asignado por el controlador)
        // >>> Usamos try-catch por si la libreria no tiene el dato disponible
        try {
            // lblSquawk.setText(t.getSquawk()); // Descomentar si existe el metodo
            lblSquawk.setText("----");
        } catch (Exception e) {
            lblSquawk.setText("----");
        }

        // 4. ALTITUD (Usamos getAlt_ext para mayor precision)
        lblAlt.setText(dfAlt.format(t.getAlt_ext()));

        // 5. VELOCIDAD SOBRE EL SUELO (Ground Speed)
        lblSpeed.setText(dfSpeed.format(t.getGs()));

        // 6. TASA VERTICAL (Si sube o baja)
        // >>> Añadimos el signo '+' si es positivo para que quede mas claro
        double vr = t.getVr();
        String vrStr = (vr >= 0 ? "+" : "") + dfSpeed.format(vr);
        lblVertRate.setText(vrStr);

        // 7. RUMBO (Hacia donde mira el morro del avion)
        lblTrack.setText(dfSpeed.format(t.getTrack()));

        // 8. COORDENADAS GPS
        if (t.getPosition_ext() != null) {
            lblLat.setText(dfCoord.format(t.getPosition_ext().getLatitude()));
            lblLon.setText(dfCoord.format(t.getPosition_ext().getLongitude()));
        }

        // >>> Actualizamos el titulo de la ventana con el nombre del vuelo
        this.setTitle("Vuelo: " + t.getCallsign());
    }
}
