/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package entidad;

import java.io.Serializable;
import java.util.List;
import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "rutina")
@NamedQueries({
    @NamedQuery(name = "Rutina.findAll", query = "SELECT r FROM Rutina r"),
    @NamedQuery(name = "Rutina.findByIdrutina", query = "SELECT r FROM Rutina r WHERE r.idrutina = :idrutina"),
    @NamedQuery(name = "Rutina.findByNombre", query = "SELECT r FROM Rutina r WHERE r.nombre = :nombre"),
    @NamedQuery(name = "Rutina.findByRealizada", query = "SELECT r FROM Rutina r WHERE r.realizada = :realizada"),
    @NamedQuery(name = "Rutina.findByPerfil",
                query = "SELECT r FROM Rutina r WHERE r.perfil.idperfil = :idperfil ORDER BY r.idrutina DESC"),
    @NamedQuery(name = "Rutina.findPendientesByPerfil",
                query = "SELECT r FROM Rutina r WHERE r.perfil.idperfil = :idperfil AND r.realizada = false ORDER BY r.idrutina DESC")
})
public class Rutina implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idrutina")
    private Integer idrutina;

    @Basic(optional = false)
    @Column(name = "nombre")
    private String nombre;

    @Column(name = "categoría")
    private String categoría;

    @Column(name = "imagen")
    private String imagen;

    @Column(name = "tiempo")
    private Integer tiempo;

    @Column(name = "descripción")
    private String descripción;

    @Basic(optional = false)
    @Column(name = "realizada")
    private boolean realizada;

    @JoinColumn(name = "perfil_idperfil", referencedColumnName = "idperfil")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Perfil perfil;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "rutina")
    private List<RutinaHasEjercicio> rutinaHasEjercicioList;

    public Rutina() {}

    public Rutina(Integer idrutina) {
        this.idrutina = idrutina;
    }

    public Rutina(Integer idrutina, String nombre, boolean realizada) {
        this.idrutina = idrutina;
        this.nombre = nombre;
        this.realizada = realizada;
    }

    public Integer getIdrutina() { return idrutina; }
    public void setIdrutina(Integer idrutina) { this.idrutina = idrutina; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCategoría() { return categoría; }
    public void setCategoría(String categoría) { this.categoría = categoría; }

    public String getImagen() { return imagen; }
    public void setImagen(String imagen) { this.imagen = imagen; }

    public Integer getTiempo() { return tiempo; }
    public void setTiempo(Integer tiempo) { this.tiempo = tiempo; }

    public String getDescripción() { return descripción; }
    public void setDescripción(String descripción) { this.descripción = descripción; }

    public boolean getRealizada() { return realizada; }
    public void setRealizada(boolean realizada) { this.realizada = realizada; }

    public Perfil getPerfil() { return perfil; }
    public void setPerfil(Perfil perfil) { this.perfil = perfil; }

    public List<RutinaHasEjercicio> getRutinaHasEjercicioList() { return rutinaHasEjercicioList; }
    public void setRutinaHasEjercicioList(List<RutinaHasEjercicio> rutinaHasEjercicioList) {
        this.rutinaHasEjercicioList = rutinaHasEjercicioList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idrutina != null ? idrutina.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Rutina)) return false;
        Rutina other = (Rutina) object;
        return !((this.idrutina == null && other.idrutina != null)
              || (this.idrutina != null && !this.idrutina.equals(other.idrutina)));
    }

    @Override
    public String toString() {
        return "entidad.Rutina[ idrutina=" + idrutina + " ]";
    }
}