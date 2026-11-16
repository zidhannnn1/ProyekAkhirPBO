package id.ac.kampus.ecotrash.model;

/**
 * Entitas petugas operasional bank sampah RT.
 * Mengelola penimbangan sampah dan penerbitan bukti setoran.
 */
public class PetugasBankSampah extends Pengguna {
    private String nipPetugas;
    private String jabatan;

    public PetugasBankSampah(String idPengguna, String namaLengkap, String noTelepon, String nomorRT, String nipPetugas, String jabatan) {
        super(idPengguna, namaLengkap, noTelepon, nomorRT);
        this.nipPetugas = nipPetugas;
        this.jabatan = jabatan;
    }

    @Override
    public String peran() {
        return "Petugas Operasional - " + jabatan;
    }

    public String getNipPetugas() {
        return nipPetugas;
    }

    public String getJabatan() {
        return jabatan;
    }
}

