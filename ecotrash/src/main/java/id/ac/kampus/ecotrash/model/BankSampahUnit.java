package id.ac.kampus.ecotrash.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Entitas induk unit bank sampah pada tingkat komunitas / RT.
 * Memiliki hubungan Agregasi (◇) dengan KategoriSampah dan PetugasBankSampah
 * (objek diterima dari luar, siklus hidupnya independen).
 */
public class BankSampahUnit {
    private String namaUnit;
    private String alamatRT;
    private List<KategoriSampah> daftarKategori;
    private List<PetugasBankSampah> daftarPetugas;

    public BankSampahUnit(String namaUnit, String alamatRT) {
        this.namaUnit = namaUnit;
        this.alamatRT = alamatRT;
        this.daftarKategori = new ArrayList<>();
        this.daftarPetugas = new ArrayList<>();
    }

    /**
     * Menerima objek yang dibuat dari luar (Karakteristik Agregasi).
     */
    public void tambahKategori(KategoriSampah kategori) {
        if (kategori != null && !daftarKategori.contains(kategori)) {
            daftarKategori.add(kategori);
        }
    }

    public void daftarkanPetugas(PetugasBankSampah petugas) {
        if (petugas != null && !daftarPetugas.contains(petugas)) {
            daftarPetugas.add(petugas);
        }
    }

    public KategoriSampah cariKategori(String kode) {
        for (KategoriSampah k : daftarKategori) {
            if (k.getKodeSampah().equalsIgnoreCase(kode)) {
                return k;
            }
        }
        return null;
    }

    public String getNamaUnit() {
        return namaUnit;
    }

    public String getAlamatRT() {
        return alamatRT;
    }

    public List<KategoriSampah> getDaftarKategori() {
        return Collections.unmodifiableList(daftarKategori);
    }

    public List<PetugasBankSampah> getDaftarPetugas() {
        return Collections.unmodifiableList(daftarPetugas);
    }
}

