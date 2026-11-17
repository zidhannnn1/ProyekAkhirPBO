package id.ac.kampus.ecotrash.model;

/**
 * Petugas operasional bank sampah yang memproses setoran warga.
 */
public class PetugasBankSampah extends Pengguna {

    private final String nipPetugas;
    private final String jabatan;

    public PetugasBankSampah(String idPengguna, String namaLengkap,
                             String noTelepon, String nomorRT,
                             String nipPetugas, String jabatan) {
        super(idPengguna, namaLengkap, noTelepon, nomorRT);
        this.nipPetugas = nipPetugas;
        this.jabatan = jabatan;
    }

    @Override
    public String peran() {
        return "Petugas - " + jabatan;
    }

    public String getNipPetugas() { return nipPetugas; }
    public String getJabatan() { return jabatan; }
}
