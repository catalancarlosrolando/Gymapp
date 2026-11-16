/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package entidad;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.Basic;
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
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

/**
 * Entidad que materializa la sesión activa de un perfil.
 * Garantiza ACID real: la sesión persiste aunque GlassFish se reinicie.
 */
@Entity
@Table(name = "sesion_activa")
@NamedQueries({
    @NamedQuery(name = "SesionActiva.findAll",
                query = "SELECT s FROM SesionActiva s"),
    @NamedQuery(name = "SesionActiva.findByIdsesion",
                query = "SELECT s FROM SesionActiva s WHERE s.idsesion = :idsesion"),
    @NamedQuery(name = "SesionActiva.findByPerfil",
                query = "SELECT s FROM SesionActiva s WHERE s.perfil.idperfil = :idperfil"),
    @NamedQuery(name = "SesionActiva.countByPlan",
                query = "SELECT COUNT(s) FROM SesionActiva s WHERE s.plan.idplan = :idplan"),
    @NamedQuery(name = "SesionActiva.findByCuenta",
                query = "SELECT s FROM SesionActiva s WHERE s.cuenta.idcuenta = :idcuenta"),
    @NamedQuery(name = "SesionActiva.deleteByPerfil",
                query = "DELETE FROM SesionActiva s WHERE s.perfil.idperfil = :idperfil")
})
public class SesionActiva implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idsesion")
    private Integer idsesion;

    @Basic(optional = false)
    @Column(name = "fecha_inicio")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaInicio;

    @Column(name = "http_session_id")
    private String httpSessionId;

    @JoinColumn(name = "perfil_idperfil", referencedColumnName = "idperfil")
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private Perfil perfil;

    @JoinColumn(name = "cuenta_idcuenta", referencedColumnName = "idcuenta")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Cuenta cuenta;

    @JoinColumn(name = "plan_idplan", referencedColumnName = "idplan")
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private Plan plan;

    public SesionActiva() {}

    public SesionActiva(Integer idsesion) {
        this.idsesion = idsesion;
    }

    public SesionActiva(Integer idsesion, Date fechaInicio) {
        this.idsesion = idsesion;
        this.fechaInicio = fechaInicio;
    }

    public Integer getIdsesion() { return idsesion; }
    public void setIdsesion(Integer idsesion) { this.idsesion = idsesion; }

    public Date getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(Date fechaInicio) { this.fechaInicio = fechaInicio; }

    public String getHttpSessionId() { return httpSessionId; }
    public void setHttpSessionId(String httpSessionId) { this.httpSessionId = httpSessionId; }

    public Perfil getPerfil() { return perfil; }
    public void setPerfil(Perfil perfil) { this.perfil = perfil; }

    public Cuenta getCuenta() { return cuenta; }
    public void setCuenta(Cuenta cuenta) { this.cuenta = cuenta; }

    public Plan getPlan() { return plan; }
    public void setPlan(Plan plan) { this.plan = plan; }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idsesion != null ? idsesion.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof SesionActiva)) return false;
        SesionActiva other = (SesionActiva) object;
        return !((this.idsesion == null && other.idsesion != null)
              || (this.idsesion != null && !this.idsesion.equals(other.idsesion)));
    }

    @Override
    public String toString() {
        return "entidad.SesionActiva[ idsesion=" + idsesion + " ]";
    }
}