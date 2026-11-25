package id.ac.kampus.ecotrash.model;

import id.ac.kampus.ecotrash.exception.DataTidakDitemukanException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Unit bank sampah tingkat RT. Aggregation (◇) terhadap KategoriSampah
 * dan PetugasBankSampah — menggunakan LinkedHashSet untuk mencegah duplikasi kategori.
 */
public class BankSampahUnit {

    private final String namaUnit;
    private final String alamatRT;
    private final Set<KategoriSampah> daftarKategori;
    private final List<PetugasBankSampah> daftarPetugas;

    public BankSampahUnit(String namaUnit, String alamatRT) {
        this.namaUnit = namaUnit;
        this.alamatRT = alamatRT;
        this.daftarKategori = new LinkedHashSet<>();
        this.daftarPetugas = new ArrayList<>();
    }

    public boolean tambahKategori(KategoriSampah kategori) {
        if (kategori == null) return false;
        return daftarKategori.add(kategori);
    }

    public void daftarkanPetugas(PetugasBankSampah petugas) {
        if (petugas == null) return;
        if (!daftarPetugas.contains(petugas)) {
            daftarPetugas.add(petugas);
        }
    }

    public Optional<KategoriSampah> cariKategori(String kode) {
        if (kode == null) return Optional.empty();
        for (KategoriSampah k : daftarKategori) {
            if (k.getKodeSampah().equals(kode)) {
                return Optional.of(k);
            }
        }
        return Optional.empty();
    }

    public KategoriSampah ambilKategori(String kode) throws DataTidakDitemukanException {
        return cariKategori(kode).orElseThrow(
                () -> new DataTidakDitemukanException("KategoriSampah", kode));
    }

    public String getNamaUnit() { return namaUnit; }
    public String getAlamatRT() { return alamatRT; }

    public List<KategoriSampah> getDaftarKategori() {
        return Collections.unmodifiableList(new ArrayList<>(daftarKategori));
    }

    public Set<KategoriSampah> getHimpunanKategori() {
        return Collections.unmodifiableSet(daftarKategori);
    }

    public List<PetugasBankSampah> getDaftarPetugas() {
        return Collections.unmodifiableList(daftarPetugas);
    }
}
