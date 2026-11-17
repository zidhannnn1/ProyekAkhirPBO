package id.ac.kampus.ecotrash.model;

/**
 * Basis abstrak untuk seluruh pengguna sistem Bank Sampah.
 * Identitas bersifat immutable setelah konstruksi.
 */
public abstract class Pengguna {

    protected final String idPengguna;
    protected final String namaLengkap;
    protected final String noTelepon;
    protected final String nomorRT;

    protected Pengguna(String idPengguna, String namaLengkap,
                       String noTelepon, String nomorRT) {
        if (idPengguna == null || idPengguna.isBlank()) {
            throw new IllegalArgumentException("idPengguna tidak boleh kosong");
        }
        this.idPengguna = idPengguna;
        this.namaLengkap = namaLengkap;
        this.noTelepon = noTelepon;
        this.nomorRT = nomorRT;
    }

    public abstract String peran();

    public String getIdPengguna() { return idPengguna; }
    public String getNamaLengkap() { return namaLengkap; }
    public String getNoTelepon() { return noTelepon; }
    public String getNomorRT() { return nomorRT; }
}
