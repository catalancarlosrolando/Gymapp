/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package entidad;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class RutinaHasEjercicioPK implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "rutina_idrutina")
    private int rutinaIdrutina;

    @Column(name = "ejercicio_idejercicio")
    private int ejercicioIdejercicio;

    public RutinaHasEjercicioPK() {}

    public RutinaHasEjercicioPK(int rutinaIdrutina, int ejercicioIdejercicio) {
        this.rutinaIdrutina = rutinaIdrutina;
        this.ejercicioIdejercicio = ejercicioIdejercicio;
    }

    public int getRutinaIdrutina() { return rutinaIdrutina; }
    public void setRutinaIdrutina(int rutinaIdrutina) { this.rutinaIdrutina = rutinaIdrutina; }

    public int getEjercicioIdejercicio() { return ejercicioIdejercicio; }
    public void setEjercicioIdejercicio(int ejercicioIdejercicio) {
        this.ejercicioIdejercicio = ejercicioIdejercicio;
    }

    @Override
    public int hashCode() {
        return Objects.hash(rutinaIdrutina, ejercicioIdejercicio);
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof RutinaHasEjercicioPK)) return false;
        RutinaHasEjercicioPK other = (RutinaHasEjercicioPK) object;
        return this.rutinaIdrutina == other.rutinaIdrutina
            && this.ejercicioIdejercicio == other.ejercicioIdejercicio;
    }

    @Override
    public String toString() {
        return "entidad.RutinaHasEjercicioPK[ rutina=" + rutinaIdrutina
             + ", ejercicio=" + ejercicioIdejercicio + " ]";
    }
}