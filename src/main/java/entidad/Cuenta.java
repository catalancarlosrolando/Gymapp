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
@Table(name = "cuenta")
@NamedQueries({
    @NamedQuery(name = "Cuenta.findAll", query = "SELECT c FROM Cuenta c"),
    @NamedQuery(name = "Cuenta.findByIdcuenta", query = "SELECT c FROM Cuenta c WHERE c.idcuenta = :idcuenta"),
    @NamedQuery(name = "Cuenta.findByUsername", query = "SELECT c FROM Cuenta c WHERE c.username = :username"),
    @NamedQuery(name = "Cuenta.findByPassword", query = "SELECT c FROM Cuenta c WHERE c.password = :password")
})
public class Cuenta implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idcuenta")
    private Integer idcuenta;

    @Basic(optional = false)
    @Column(name = "username")
    private String username;

    @Basic(optional = false)
    @Column(name = "password")
    private String password;

    @JoinColumn(name = "plan_idplan", referencedColumnName = "idplan")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Plan plan;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "cuenta")
    private List<Perfil> perfilList;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "cuenta")
    private List<SesionActiva> sesionActivaList;

    public Cuenta() {}

    public Cuenta(Integer idcuenta) {
        this.idcuenta = idcuenta;
    }

    public Cuenta(Integer idcuenta, String username, String password) {
        this.idcuenta = idcuenta;
        this.username = username;
        this.password = password;
    }

    public Integer getIdcuenta() { return idcuenta; }
    public void setIdcuenta(Integer idcuenta) { this.idcuenta = idcuenta; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Plan getPlan() { return plan; }
    public void setPlan(Plan plan) { this.plan = plan; }

    public List<Perfil> getPerfilList() { return perfilList; }
    public void setPerfilList(List<Perfil> perfilList) { this.perfilList = perfilList; }

    public List<SesionActiva> getSesionActivaList() { return sesionActivaList; }
    public void setSesionActivaList(List<SesionActiva> sesionActivaList) { this.sesionActivaList = sesionActivaList; }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idcuenta != null ? idcuenta.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Cuenta)) return false;
        Cuenta other = (Cuenta) object;
        return !((this.idcuenta == null && other.idcuenta != null)
              || (this.idcuenta != null && !this.idcuenta.equals(other.idcuenta)));
    }

    @Override
    public String toString() {
        return "entidad.Cuenta[ idcuenta=" + idcuenta + " ]";
    }
}