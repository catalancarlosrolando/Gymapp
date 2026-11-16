/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package entidad;

import java.io.Serializable;
import java.math.BigDecimal;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

@Entity
@Table(name = "rutina_has_ejercicio")
@NamedQueries({
    @NamedQuery(name = "RutinaHasEjercicio.findAll",
                query = "SELECT r FROM RutinaHasEjercicio r"),
    @NamedQuery(name = "RutinaHasEjercicio.findByRutina",
                query = "SELECT r FROM RutinaHasEjercicio r WHERE r.rutina.idrutina = :idrutina")
})
public class RutinaHasEjercicio implements Serializable {

    private static final long serialVersionUID = 1L;

    @EmbeddedId
    protected RutinaHasEjercicioPK rutinaHasEjercicioPK;

    @Column(name = "series")
    private Integer series;

    @Column(name = "repeticiones")
    private Integer repeticiones;

    @Column(name = "peso")
    private BigDecimal peso;

    @Column(name = "tiempo")
    private Integer tiempo;

    @Column(name = "descanso")
    private Integer descanso;

    @JoinColumn(name = "ejercicio_idejercicio", referencedColumnName = "idejercicio",
                insertable = false, updatable = false)
    @ManyToOne(fetch = FetchType.EAGER)
    private Ejercicio ejercicio;

    @JoinColumn(name = "rutina_idrutina", referencedColumnName = "idrutina",
                insertable = false, updatable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Rutina rutina;

    public RutinaHasEjercicio() {}

    public RutinaHasEjercicio(RutinaHasEjercicioPK rutinaHasEjercicioPK) {
        this.rutinaHasEjercicioPK = rutinaHasEjercicioPK;
    }

    public RutinaHasEjercicio(int rutinaIdrutina, int ejercicioIdejercicio) {
        this.rutinaHasEjercicioPK = new RutinaHasEjercicioPK(rutinaIdrutina, ejercicioIdejercicio);
    }

    public RutinaHasEjercicioPK getRutinaHasEjercicioPK() { return rutinaHasEjercicioPK; }
    public void setRutinaHasEjercicioPK(RutinaHasEjercicioPK rutinaHasEjercicioPK) {
        this.rutinaHasEjercicioPK = rutinaHasEjercicioPK;
    }

    public Integer getSeries() { return series; }
    public void setSeries(Integer series) { this.series = series; }

    public Integer getRepeticiones() { return repeticiones; }
    public void setRepeticiones(Integer repeticiones) { this.repeticiones = repeticiones; }

    public BigDecimal getPeso() { return peso; }
    public void setPeso(BigDecimal peso) { this.peso = peso; }

    public Integer getTiempo() { return tiempo; }
    public void setTiempo(Integer tiempo) { this.tiempo = tiempo; }

    public Integer getDescanso() { return descanso; }
    public void setDescanso(Integer descanso) { this.descanso = descanso; }

    public Ejercicio getEjercicio() { return ejercicio; }
    public void setEjercicio(Ejercicio ejercicio) { this.ejercicio = ejercicio; }

    public Rutina getRutina() { return rutina; }
    public void setRutina(Rutina rutina) { this.rutina = rutina; }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (rutinaHasEjercicioPK != null ? rutinaHasEjercicioPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof RutinaHasEjercicio)) return false;
        RutinaHasEjercicio other = (RutinaHasEjercicio) object;
        return !((this.rutinaHasEjercicioPK == null && other.rutinaHasEjercicioPK != null)
              || (this.rutinaHasEjercicioPK != null
                  && !this.rutinaHasEjercicioPK.equals(other.rutinaHasEjercicioPK)));
    }

    @Override
    public String toString() {
        return "entidad.RutinaHasEjercicio[ " + rutinaHasEjercicioPK + " ]";
    }
}