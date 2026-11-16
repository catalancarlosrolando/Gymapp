/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package entidad;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "ejercicio")
@NamedQueries({
    @NamedQuery(name = "Ejercicio.findAll", query = "SELECT e FROM Ejercicio e"),
    @NamedQuery(name = "Ejercicio.findByIdejercicio",
                query = "SELECT e FROM Ejercicio e WHERE e.idejercicio = :idejercicio"),
    @NamedQuery(name = "Ejercicio.findByNombre",
                query = "SELECT e FROM Ejercicio e WHERE e.nombre = :nombre")
})
public class Ejercicio implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idejercicio")
    private Integer idejercicio;

    @Basic(optional = false)
    @Column(name = "nombre")
    private String nombre;

    @Column(name = "imagen")
    private String imagen;

    @Column(name = "tiempo")
    private Integer tiempo;

    @Column(name = "series")
    private Integer series;

    @Column(name = "repeticiones")
    private Integer repeticiones;

    @Column(name = "peso")
    private BigDecimal peso;

    @Column(name = "descanso")
    private Integer descanso;

    @Column(name = "descripción")
    private String descripción;

    @OneToMany(mappedBy = "ejercicio")
    private List<RutinaHasEjercicio> rutinaHasEjercicioList;

    public Ejercicio() {}

    public Ejercicio(Integer idejercicio) {
        this.idejercicio = idejercicio;
    }

    public Ejercicio(Integer idejercicio, String nombre) {
        this.idejercicio = idejercicio;
        this.nombre = nombre;
    }

    public Integer getIdejercicio() { return idejercicio; }
    public void setIdejercicio(Integer idejercicio) { this.idejercicio = idejercicio; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getImagen() { return imagen; }
    public void setImagen(String imagen) { this.imagen = imagen; }

    public Integer getTiempo() { return tiempo; }
    public void setTiempo(Integer tiempo) { this.tiempo = tiempo; }

    public Integer getSeries() { return series; }
    public void setSeries(Integer series) { this.series = series; }

    public Integer getRepeticiones() { return repeticiones; }
    public void setRepeticiones(Integer repeticiones) { this.repeticiones = repeticiones; }

    public BigDecimal getPeso() { return peso; }
    public void setPeso(BigDecimal peso) { this.peso = peso; }

    public Integer getDescanso() { return descanso; }
    public void setDescanso(Integer descanso) { this.descanso = descanso; }

    public String getDescripción() { return descripción; }
    public void setDescripción(String descripción) { this.descripción = descripción; }

    public List<RutinaHasEjercicio> getRutinaHasEjercicioList() { return rutinaHasEjercicioList; }
    public void setRutinaHasEjercicioList(List<RutinaHasEjercicio> rutinaHasEjercicioList) {
        this.rutinaHasEjercicioList = rutinaHasEjercicioList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idejercicio != null ? idejercicio.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Ejercicio)) return false;
        Ejercicio other = (Ejercicio) object;
        return !((this.idejercicio == null && other.idejercicio != null)
              || (this.idejercicio != null && !this.idejercicio.equals(other.idejercicio)));
    }

    @Override
    public String toString() {
        return "entidad.Ejercicio[ idejercicio=" + idejercicio + " ]";
    }
}