/*
 * TrafficGraphic.java (MODIFICADO PARA ESTELAS)
 */
package GUI;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import traffic.Traffic;
import traffic.TrafficExtended;
// *** IMPORTANTE: Importamos la clase Position ***
import de.serosystems.lib1090.Position; 

public class TrafficGraphic implements TrafficExtended {
    private Traffic tr;
    private GenericRadarPanel rp;
    
    private PositionGraphic gPos;
    private Color color;    
    private boolean selected;
    private boolean inConflict = false;
    private TrafficPlot tfp;
    private boolean visible=false;

    // *** CAMBIO 1: La lista ahora guarda 'Position' (GPS), no gráficos
    private List<Position> trail; 
     
    public TrafficGraphic(Traffic tr, GenericRadarPanel rp) {
        this.tr=tr;
        this.rp=rp;
        this.color = Color.WHITE;        
        this.selected = false;
        
        // Inicializamos la lista
        this.trail = new ArrayList<>();
        
        this.gPos = new PositionGraphic(tr.getPosition_ext(),rp);
        tfp=new TrafficPlot(this);
        tr.setTrafficExt(this); 
    }

    @Override
    public void newPosition() { 
        // *** CAMBIO 2: Guardamos la coordenada GPS actual
        if (tr.getPosition_ext() != null) {
            // Guardamos una COPIA de la posición para que no cambie
            Position p = tr.getPosition_ext();
            // (La clase Position suele ser inmutable o clonable, la guardamos tal cual)
            this.trail.add(p);
            
            // Limitamos el tamaño para no saturar memoria (ej. 50 puntos)
            if (this.trail.size() > 50) {
                this.trail.remove(0);
            }
        }

        // Actualizamos la posición gráfica actual (esto sigue igual para el icono)
        this.gPos = new PositionGraphic(tr.getPosition_ext(), rp);
    }

    // *** CAMBIO 3: El getter devuelve lista de Position
    public List<Position> getTrail() {
        return trail;
    }
    
    // ... RESTO DE MÉTODOS IGUAL QUE ANTES ...
    
    public Traffic getTraffic() { return tr; }
    public TrafficPlot getTrafficPlot() { return tfp; }
    public GenericRadarPanel getRadarPanel() { return rp; }
    public PositionGraphic getgPos() { return gPos; }
    public Color getColor() { return color; }
    public void setColor(Color color) { this.color = color; }
    public synchronized boolean isSelected() { return selected; }
    public synchronized void setSelected(boolean selected) { this.selected = selected; }
    public synchronized boolean isVisible() { return visible; }
    public void setInConflict(boolean state) { this.inConflict = state; }
    public boolean isInConflict() { return inConflict; }
}