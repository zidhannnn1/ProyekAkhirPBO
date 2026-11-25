package id.ac.kampus.ecotrash;

import id.ac.kampus.ecotrash.exception.DataTidakDitemukanException;
import id.ac.kampus.ecotrash.exception.TransaksiTidakValidException;
import id.ac.kampus.ecotrash.model.*;
import id.ac.kampus.ecotrash.repository.InMemoryRepository;
import id.ac.kampus.ecotrash.repository.Repository;
import id.ac.kampus.ecotrash.reward.RewardTabunganRupiah;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pengujian unit untuk KategoriSampah, TransaksiSetoran, Collection, dan Repository<T, ID>.
 */
public class KategoriDanTransaksiUnitTest {

    private NasabahWarga nasabah;
    private SampahAnorganik botol;
    private MinyakJelantah jelantah;
    private TransaksiSetoran tx;

    @BeforeEach
    void siapkan() {
        nasabah = new NasabahWarga("NSB02", "Pak Ahmad", "081311223344", "04", "REK-04-018");
        botol = new SampahAnorganik("SMP01", "Botol Plastik PET", 4500.0, "Grade A");
        jelantah = new MinyakJelantah("SMP04", 6500.0, 1.2);
        tx = new TransaksiSetoran("TRX-UNIT-01", nasabah, LocalDate.now());
    }

    @Test
    @DisplayName("TC-07 Batas bawah tidak valid: tambahItem berat 0 -> IllegalArgumentException")
    void tambahItemBeratNolDitolak() {
        assertThrows(IllegalArgumentException.class, () -> tx.tambahItem(botol, 0.0, "Kosong"));
        assertTrue(tx.getDaftarItem().isEmpty());
    }

    @Test
    @DisplayName("TC-08 Partisi tidak valid: tambahItem berat negatif -> IllegalArgumentException")
    void tambahItemBeratNegatifDitolak() {
        assertThrows(IllegalArgumentException.class, () -> tx.tambahItem(botol, -2.5, "Minus"));
        assertTrue(tx.getDaftarItem().isEmpty());
    }

    @Test
    @DisplayName("TC-09 Batas bawah valid & polimorfisme hitungNilaiSubtotal")
    void tambahItemValidDanPolimorfismeKategori() throws TransaksiTidakValidException {
        tx.tambahItem(botol, 0.1, "Sampel kecil");
        tx.tambahItem(jelantah, 2.0, "Saring bersih");
        assertAll(
                () -> assertEquals(450.0, botol.hitungNilaiSubtotal(0.1), 0.001),
                () -> assertEquals(13000.0, jelantah.hitungNilaiSubtotal(2.0), 0.001),
                () -> assertEquals(2.1, tx.hitungTotalBerat(), 0.001),
                () -> assertEquals(13450.0, tx.hitungTotalNilai(), 0.001));
    }

    @Test
    @DisplayName("TC-10 HashSet<KategoriSampah>: dua objek kode SMP01 sama dianggap satu")
    void hashSetKategoriMencegahDuplikatKode() {
        Set<KategoriSampah> himpunan = new HashSet<>();
        himpunan.add(new SampahAnorganik("SMP01", "Botol Plastik PET", 4500.0, "Grade A"));
        himpunan.add(new SampahAnorganik("SMP01", "Botol Plastik PET", 4500.0, "Grade A"));
        assertEquals(1, himpunan.size(), "Kategori berkode sama harus dianggap satu di HashSet");
    }

    @Test
    @DisplayName("TC-11 Kategori sama pada satu transaksi digabung menjadi satu baris")
    void tambahItemKategoriSamaDigabung() throws TransaksiTidakValidException {
        tx.tambahItem(botol, 2.0, "Tahap 1");
        tx.tambahItem(new SampahAnorganik("SMP01", "Botol Plastik PET", 4500.0, "Grade A"),
                2.5, "Tahap 2");
        assertAll(
                () -> assertEquals(1, tx.getDaftarItem().size(), "Item kategori sama harus digabung"),
                () -> assertEquals(4.5, tx.hitungTotalBerat(), 0.001),
                () -> assertEquals(20250.0, tx.hitungTotalNilai(), 0.001));
    }

    @Test
    @DisplayName("TC-12 Penjagaan status: tambahItem setelah SELESAI -> TransaksiTidakValidException")
    void tambahItemSetelahSelesaiDitolak() throws TransaksiTidakValidException {
        tx.tambahItem(botol, 2.0, "Valid");
        tx.selesaikanTransaksi(new RewardTabunganRupiah());

        assertThrows(TransaksiTidakValidException.class,
                () -> tx.tambahItem(jelantah, 1.0, "Susulan ilegal"));
        assertAll(
                () -> assertEquals(StatusTransaksi.SELESAI, tx.getStatus()),
                () -> assertEquals(1, tx.getDaftarItem().size()),
                () -> assertEquals(9000.0, tx.hitungTotalNilai(), 0.001));
    }

    @Test
    @DisplayName("TC-13 Repository<T, ID> generik melempar DataTidakDitemukanException bila ID tidak ada")
    void repositoryGenerikSimpanDanAmbil() {
        Repository<KategoriSampah, String> repo = new InMemoryRepository<>("KategoriSampah",
                KategoriSampah::getKodeSampah);
        repo.simpan(botol);

        assertAll(
                () -> assertTrue(repo.cari("SMP01").isPresent()),
                () -> assertEquals(botol, repo.ambil("SMP01")),
                () -> assertThrows(DataTidakDitemukanException.class, () -> repo.ambil("SMP999")));
    }
}
