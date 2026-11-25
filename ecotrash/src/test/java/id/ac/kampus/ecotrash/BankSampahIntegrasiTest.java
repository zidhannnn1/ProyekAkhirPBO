package id.ac.kampus.ecotrash;

import id.ac.kampus.ecotrash.exception.DataTidakDitemukanException;
import id.ac.kampus.ecotrash.exception.SaldoTidakCukupException;
import id.ac.kampus.ecotrash.exception.TransaksiTidakValidException;
import id.ac.kampus.ecotrash.model.*;
import id.ac.kampus.ecotrash.repository.InMemoryRepository;
import id.ac.kampus.ecotrash.repository.Repository;
import id.ac.kampus.ecotrash.reward.RewardPoinSembako;
import id.ac.kampus.ecotrash.reward.RewardTabunganRupiah;
import id.ac.kampus.ecotrash.service.BankSampahService;
import id.ac.kampus.ecotrash.service.KatalogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pengujian integrasi antarmodul: Model + Reward + Repository + Service + Notifikasi tiruan + CSV.
 */
public class BankSampahIntegrasiTest {

    private Repository<TransaksiSetoran, String> txRepo;
    private List<String> logNotifikasi;
    private BankSampahService service;
    private NasabahWarga siti;
    private PetugasBankSampah budi;
    private SampahAnorganik botol;
    private MinyakJelantah jelantah;

    @BeforeEach
    void siapkan() {
        txRepo = new InMemoryRepository<>("TransaksiSetoran", TransaksiSetoran::getKodeTransaksi);
        logNotifikasi = new ArrayList<>();
        service = new BankSampahService(txRepo, (telp, pesan) -> logNotifikasi.add(telp + ": " + pesan));
        siti = new NasabahWarga("NSB01", "Ibu Siti", "081298765432", "04", "REK-04-012");
        budi = new PetugasBankSampah("PTG01", "Budi Santoso", "081234567890", "04", "NIP-01", "Kasir");
        botol = new SampahAnorganik("SMP01", "Botol PET", 4500.0, "Grade A");
        jelantah = new MinyakJelantah("SMP04", 6500.0, 1.2);
    }

    @Test
    @DisplayName("TC-14 Integrasi Fitur 1: Setoran sukses -> SELESAI, tersimpan, saldo bertambah, notif terkirim")
    void integrasiSetoranSukses() throws TransaksiTidakValidException {
        TransaksiSetoran tx = new TransaksiSetoran("TRX-INT-01", siti, LocalDate.now());
        tx.tambahItem(botol, 4.0, "Bersih"); // Rp 18.000

        service.prosesSetoran(tx, new RewardTabunganRupiah(), budi);

        assertAll(
                () -> assertEquals(StatusTransaksi.SELESAI, tx.getStatus()),
                () -> assertEquals(1, txRepo.semua().size()),
                () -> assertEquals(18000.0, siti.getSaldoTabungan()),
                () -> assertEquals(1, logNotifikasi.size()),
                () -> assertTrue(logNotifikasi.get(0).contains("TRX-INT-01")));
    }

    @Test
    @DisplayName("TC-15 Integrasi Fitur 1 gagal: setoran kosong -> TransaksiTidakValidException, DIBATALKAN, tidak tersimpan")
    void integrasiSetoranGagalKosongDibatalkan() {
        TransaksiSetoran txKosong = new TransaksiSetoran("TRX-INT-FAIL", siti, LocalDate.now());

        TransaksiTidakValidException e = assertThrows(TransaksiTidakValidException.class,
                () -> service.prosesSetoran(txKosong, new RewardTabunganRupiah(), budi));

        assertAll(
                () -> assertTrue(e.getMessage().contains("tidak memiliki item")),
                () -> assertEquals(StatusTransaksi.DIBATALKAN, txKosong.getStatus()),
                () -> assertTrue(txRepo.semua().isEmpty(), "Transaksi gagal tidak boleh masuk repo"),
                () -> assertEquals(0.0, siti.getSaldoTabungan()),
                () -> assertEquals(1, logNotifikasi.size()),
                () -> assertTrue(logNotifikasi.get(0).contains("dibatalkan")));
    }

    @Test
    @DisplayName("TC-16 Integrasi Pembatalan Setoran: status DIBATALKAN dan saldo kas ditarik kembali")
    void integrasiBatalkanSetoranMengembalikanSaldo() throws Exception {
        TransaksiSetoran tx = new TransaksiSetoran("TRX-INT-BTL", siti, LocalDate.now());
        tx.tambahItem(botol, 4.0, "Kering");
        service.prosesSetoran(tx, new RewardTabunganRupiah(), budi);
        assertEquals(18000.0, siti.getSaldoTabungan());

        service.batalkanSetoran("TRX-INT-BTL");

        assertAll(
                () -> assertEquals(StatusTransaksi.DIBATALKAN, tx.getStatus()),
                () -> assertEquals(0.0, siti.getSaldoTabungan()),
                () -> assertEquals(0.0, service.hitungTotalPerputaranKas()),
                () -> assertTrue(service.beratPerKategori().isEmpty()));
    }

    @Test
    @DisplayName("TC-17 Integrasi Fitur 2 & Lookup: pencairan kurang -> SaldoTidakCukupException; kategori hilang -> DataTidakDitemukanException")
    void integrasiPencairanGagalDanKategoriHilang() throws Exception {
        siti.tambahSaldo(18000.0);
        SaldoTidakCukupException exSaldo = assertThrows(SaldoTidakCukupException.class,
                () -> service.cairkanSaldoNasabah(siti, 50000.0));

        BankSampahUnit unit = new BankSampahUnit("Unit RT 04", "RT 04");
        unit.tambahKategori(botol);

        assertAll(
                () -> assertEquals(32000.0, exSaldo.getKekurangan()),
                () -> assertEquals(18000.0, siti.getSaldoTabungan()),
                () -> assertTrue(logNotifikasi.get(0).contains("ditolak")),
                () -> assertThrows(DataTidakDitemukanException.class, () -> unit.ambilKategori("SMP999")));
    }

    @Test
    @DisplayName("TC-18 Integrasi Katalog CSV (try-with-resources), Polimorfisme Poin Sembako, dan Laporan TreeMap")
    void integrasiImporCsvDanLaporanTerurut() throws Exception {
        Path tmpCsv = Files.createTempFile("katalog-test", ".csv");
        Files.writeString(tmpCsv,
                "# Header komentar\n"
                        + "SMP01;ANORGANIK;Botol PET;4500;Grade A\n"
                        + "SMP_BAD;ANORGANIK;Harga Salah;abc;Grade A\n"
                        + "SMP_KURANG;ANORGANIK;Kolom Kurang\n"
                        + "SMP04;JELANTAH;Minyak Jelantah;6500;1.2\n");
        try {
            Repository<KategoriSampah, String> katRepo = new InMemoryRepository<>("Kategori",
                    KategoriSampah::getKodeSampah);
            KatalogService katalogService = new KatalogService(katRepo);
            KatalogService.HasilImpor hasil = katalogService.imporCsv(tmpCsv);

            TransaksiSetoran tx1 = new TransaksiSetoran("TRX-1", siti, LocalDate.now());
            tx1.tambahItem(katRepo.ambil("SMP04"), 4.0, "Minyak");
            tx1.tambahItem(katRepo.ambil("SMP01"), 2.0, "Plastik");
            service.prosesSetoran(tx1, new RewardPoinSembako(), budi);

            Map<String, Double> rekap = service.beratPerKategori();
            List<String> urutanKunci = new ArrayList<>(rekap.keySet());

            assertAll(
                    () -> assertEquals(2, hasil.getBerhasil()),
                    () -> assertEquals(2, hasil.getGalat().size()),
                    () -> assertEquals(35, siti.getPoinReward()), // (4*6500 + 2*4500) = 35.000 -> 35 poin
                    () -> assertEquals(0.0, siti.getSaldoTabungan()),
                    () -> assertEquals(List.of("Botol PET", "Minyak Jelantah Rumah Tangga"), urutanKunci),
                    () -> assertThrows(IOException.class,
                            () -> katalogService.imporCsv(Path.of("tidak_ada_file_123.csv"))));
        } finally {
            Files.deleteIfExists(tmpCsv);
        }
    }

    @Test
    @DisplayName("TC-19 Integrasi Riwayat & Pembatalan Ganda: cariTransaksiNasabah dan tolak batal ulang")
    void integrasiRiwayatDanPembatalanGanda() throws Exception {
        TransaksiSetoran tx = new TransaksiSetoran("TRX-INT-19", siti, LocalDate.now());
        tx.tambahItem(botol, 2.0, "Kering");
        service.prosesSetoran(tx, new RewardTabunganRupiah(), budi);
        service.batalkanSetoran("TRX-INT-19");

        assertAll(
                () -> assertEquals(1, service.cariTransaksiNasabah("REK-04-012").size()),
                () -> assertTrue(service.cariTransaksiNasabah("REK-LAIN").isEmpty()),
                () -> assertThrows(TransaksiTidakValidException.class,
                        () -> service.batalkanSetoran("TRX-INT-19")));
    }
}
