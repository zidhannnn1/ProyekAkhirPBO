package id.ac.kampus.ecotrash.model;

/**
 * Kelas abstrak dasar yang memodelkan pengguna sistem EcoTrash.
 * Menerapkan prinsip Abstraksi dan Pewarisan (Inheritance).
 */
public abstract class Pengguna {
    protected String idPengguna;
    protected String namaLengkap;
    protected String noTelepon;
    protected String nomorRT;

    public Pengguna(String idPengguna, String namaLengkap, String noTelepon, String nomorRT) {
        if (idPengguna == null || idPengguna.trim().isEmpty()) {
            throw new IllegalArgumentException("ID Pengguna tidak boleh kosong.");
        }
        this.idPengguna = idPengguna;
        this.namaLengkap = namaLengkap;
        this.noTelepon = noTelepon;
        this.nomorRT = nomorRT;
    }

    public abstract String peran();

    public boolean login(String username, String password) {
        return this.idPengguna.equalsIgnoreCase(username) && password != null && password.length() >= 4;
    }

    public String getIdPengguna() {
        return idPengguna;
    }

    public String getNamaLengkap() {
        return namaLengkap;
    }

    public String getNoTelepon() {
        return noTelepon;
    }

    public String getNomorRT() {
        return nomorRT;
    }
}

