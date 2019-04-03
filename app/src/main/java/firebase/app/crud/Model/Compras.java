package firebase.app.crud.Model;

public class Compras {

    private String id;
    private String fecha;
    private String cantidad;
    private String nomclie;
    private String descompra;

    public Compras() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getCantidad() {
        return cantidad;
    }

    public void setCantidad(String cantidad) {
        this.cantidad = cantidad;
    }

    public String getNomclie() {
        return nomclie;
    }

    public void setNomclie(String nomclie) {
        this.nomclie = nomclie;
    }

    public String getDescompra() {
        return descompra;
    }

    public void setDescompra(String descompra) {
        this.descompra = descompra;
    }

    @Override
    public String toString() {
        return fecha;
    }
}
