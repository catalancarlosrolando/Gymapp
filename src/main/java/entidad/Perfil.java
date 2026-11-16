/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package entidad;

import java.io.Serializable;
import java.util.Date;
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
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
@Table(name = "perfil")
@NamedQueries({
    @NamedQuery(name = "Perfil.findAll", query = "SELECT p FROM Perfil p"),
    @NamedQuery(name = "Perfil.findByIdperfil", query = "SELECT p FROM Perfil p WHERE p.idperfil = :idperfil"),
    @NamedQuery(name = "Perfil.findByNombre", query = "SELECT p FROM Perfil p WHERE p.nombre = :nombre"),
    @NamedQuery(name = "Perfil.findByTema", query = "SELECT p FROM Perfil p WHERE p.tema = :tema"),
    @NamedQuery(name = "Perfil.findByCuenta", query = "SELECT p FROM Perfil p WHERE p.cuenta.idcuenta = :idcuenta ORDER BY p.idperfil")
})
public class Perfil implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idperfil")
    private Integer idperfil;

    @Basic(optional = false)
    @Column(name = "nombre")
    private String nombre;

    @Column(name = "email")
    private String email;

    @Column(name = "fechanacimiento")
    @Temporal(TemporalType.DATE)
    private Date fechanacimiento;

    @Column(name = "genero")
    private String genero;

    @Column(name = "tema")
    private String tema;

    @Column(name = "imagen")
    private String imagen;

    @JoinColumn(name = "cuenta_idcuenta", referencedColumnName = "idcuenta")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Cuenta cuenta;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "perfil")
    private List<Rutina> rutinaList;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "perfil")
    private List<SesionActiva> sesionActivaList;

    public Perfil() {}

    public Perfil(Integer idperfil) {
        this.idperfil = idperfil;
    }

    public Perfil(Integer idperfil, String nombre) {
        this.idperfil = idperfil;
        this.nombre = nombre;
    }

    public Integer getIdperfil() { return idperfil; }
    public void setIdperfil(Integer idperfil) { this.idperfil = idperfil; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Date getFechanacimiento() { return fechanacimiento; }
    public void setFechanacimiento(Date fechanacimiento) { this.fechanacimiento = fechanacimiento; }

    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }

    public String getTema() { return tema; }
    public void setTema(String tema) { this.tema = tema; }

    public String getImagen() { return imagen; }
    public void setImagen(String imagen) { this.imagen = imagen; }

    public Cuenta getCuenta() { return cuenta; }
    public void setCuenta(Cuenta cuenta) { this.cuenta = cuenta; }

    public List<Rutina> getRutinaList() { return rutinaList; }
    public void setRutinaList(List<Rutina> rutinaList) { this.rutinaList = rutinaList; }

    public List<SesionActiva> getSesionActivaList() { return sesionActivaList; }
    public void setSesionActivaList(List<SesionActiva> sesionActivaList) { this.sesionActivaList = sesionActivaList; }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idperfil != null ? idperfil.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Perfil)) return false;
        Perfil other = (Perfil) object;
        return !((this.idperfil == null && other.idperfil != null)
              || (this.idperfil != null && !this.idperfil.equals(other.idperfil)));
    }

    @Override
    public String toString() {
        return "entidad.Perfil[ idperfil=" + idperfil + " ]";
    }
}