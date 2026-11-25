package id.ac.kampus.ecotrash.model;

import id.ac.kampus.ecotrash.exception.DataTidakDitemukanException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Unit bank sampah tingkat RT. Aggregation (◇) terhadap KategoriSampah
 * dan PetugasBankSampah — objek-objek tersebut bisa hidup mandiri
 * di luar unit ini.
 */
public class BankSampahUnit {

    private final String namaUnit;
    private final String alamatRT;
    private final List<KategoriSampah> daftarKategori;
    private final List<PetugasBankSampah> daftarPetugas;

    public BankSampahUnit(String namaUnit, String alamatRT) {
        this.namaUnit = namaUnit;
        this.alamatRT = alamatRT;
        this.daftarKategori = new ArrayList<>();
        this.daftarPetugas = new ArrayList<>();
    }

    public void tambahKategori(KategoriSampah kategori) {
        if (kategori == null) return;
        if (!daftarKategori.contains(kategori)) {
            daftarKategori.add(kategori);
        }
    }

    public void daftarkanPetugas(PetugasBankSampah petugas) {
        if (petugas == null) return;
        if (!daftarPetugas.contains(petugas)) {
            daftarPetugas.add(petugas);
        }
    }

    public Optional<KategoriSampah> cariKategori(String kode) {
        for (KategoriSampah k : daftarKategori) {
            if (k.getKodeSampah().equals(kode)) {
                return Optional.of(k);
            }
        }
        return Optional.empty();
    }

    public KategoriSampah ambilKategori(String kode) throws DataTidakDitemukanException {
        return cariKategori(kode).get();
    }

    public String getNamaUnit() { return namaUnit; }
    public String getAlamatRT() { return alamatRT; }

    public List<KategoriSampah> getDaftarKategori() {
        return Collections.unmodifiableList(daftarKategori);
    }

    public List<PetugasBankSampah> getDaftarPetugas() {
        return Collections.unmodifiableList(daftarPetugas);
    }
}
