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
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "plan")
@NamedQueries({
    @NamedQuery(name = "Plan.findAll", query = "SELECT p FROM Plan p"),
    @NamedQuery(name = "Plan.findByIdplan", query = "SELECT p FROM Plan p WHERE p.idplan = :idplan"),
    @NamedQuery(name = "Plan.findByNombre", query = "SELECT p FROM Plan p WHERE p.nombre = :nombre"),
    @NamedQuery(name = "Plan.findByCupos", query = "SELECT p FROM Plan p WHERE p.cupos = :cupos")
})
public class Plan implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idplan")
    private Integer idplan;

    @Basic(optional = false)
    @Column(name = "nombre")
    private String nombre;

    @Basic(optional = false)
    @Column(name = "cupos")
    private Integer cupos;

    @Basic(optional = false)
    @Column(name = "max_perfiles")
    private Integer maxPerfiles;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "plan")
    private List<Cuenta> cuentaList;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "plan")
    private List<SesionActiva> sesionActivaList;

    public Plan() {}

    public Plan(Integer idplan) {
        this.idplan = idplan;
    }

    public Plan(Integer idplan, String nombre, Integer cupos, Integer maxPerfiles) {
        this.idplan = idplan;
        this.nombre = nombre;
        this.cupos = cupos;
        this.maxPerfiles = maxPerfiles;
    }

    public Integer getIdplan() { return idplan; }
    public void setIdplan(Integer idplan) { this.idplan = idplan; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Integer getCupos() { return cupos; }
    public void setCupos(Integer cupos) { this.cupos = cupos; }

    public Integer getMaxPerfiles() { return maxPerfiles; }
    public void setMaxPerfiles(Integer maxPerfiles) { this.maxPerfiles = maxPerfiles; }

    public List<Cuenta> getCuentaList() { return cuentaList; }
    public void setCuentaList(List<Cuenta> cuentaList) { this.cuentaList = cuentaList; }

    public List<SesionActiva> getSesionActivaList() { return sesionActivaList; }
    public void setSesionActivaList(List<SesionActiva> sesionActivaList) { this.sesionActivaList = sesionActivaList; }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idplan != null ? idplan.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Plan)) return false;
        Plan other = (Plan) object;
        return !((this.idplan == null && other.idplan != null)
              || (this.idplan != null && !this.idplan.equals(other.idplan)));
    }

    @Override
    public String toString() {
        return "entidad.Plan[ idplan=" + idplan + " ]";
    }
}