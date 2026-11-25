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
import id.ac.kampus.ecotrash.service.NotifikasiService;
import id.ac.kampus.ecotrash.ui.BankSampahCLI;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * Main Class Assembler (Composition Root) EcoTrash v4.
 * Method main() dipecah menjadi method terfokus agar tetap ringkas dan terukur.
 */
public class App {

    public static void main(String[] args) {
        BankSampahUnit unitRT = new BankSampahUnit(
                "Bank Sampah Berkah Mandiri",
                "RT 04 / RW 02 Kelurahan Sukamaju");

        Repository<KategoriSampah, String> kategoriRepo = new InMemoryRepository<>("KategoriSampah",
                KategoriSampah::getKodeSampah);
        Repository<TransaksiSetoran, String> transaksiRepo = new InMemoryRepository<>("TransaksiSetoran",
                TransaksiSetoran::getKodeTransaksi);

        NotifikasiService notifikasi = (telepon, pesan) -> System.out
                .println("[NOTIFIKASI ke " + telepon + "]: " + pesan);

        KatalogService katalogService = new KatalogService(kategoriRepo);
        BankSampahService service = new BankSampahService(transaksiRepo, notifikasi, new RewardTabunganRupiah());
        BankSampahCLI cli = new BankSampahCLI(service, unitRT);

        cli.cetakBanner();
        demoImporKatalog(katalogService, kategoriRepo, unitRT);
        cli.tampilkanKatalog();

        PetugasBankSampah petugasBudi = new PetugasBankSampah(
                "PTG01", "Budi Santoso", "081234567890", "04", "NIP-RT04-01", "Petugas Loket");
        unitRT.daftarkanPetugas(petugasBudi);

        NasabahWarga nasabahSiti = new NasabahWarga("NSB01", "Ibu Siti Aminah", "081298765432", "04", "REK-04-012");
        NasabahWarga nasabahAhmad = new NasabahWarga("NSB02", "Pak Ahmad Fauzi", "081311223344", "04", "REK-04-018");
        List<NasabahWarga> daftarNasabah = Arrays.asList(nasabahSiti, nasabahAhmad);

        demoSetoranDanGalat(service, cli, unitRT, petugasBudi, nasabahSiti, nasabahAhmad);
        demoPencairanDanDataHilang(service, nasabahSiti);
        cetakLaporanAkhir(cli, daftarNasabah, unitRT.getDaftarPetugas());

        cekModeInteraktif(args, cli, daftarNasabah);
    }

    private static void demoImporKatalog(KatalogService katalogService,
                                         Repository<KategoriSampah, String> kategoriRepo,
                                         BankSampahUnit unitRT) {
        Path csvPath = Path.of("ecotrash/data/katalog.csv");
        if (!Files.exists(csvPath)) {
            csvPath = Path.of("data/katalog.csv");
        }
        try {
            KatalogService.HasilImpor hasil = katalogService.imporCsv(csvPath);
            for (KategoriSampah k : kategoriRepo.semua()) {
                unitRT.tambahKategori(k);
            }
            System.out.println("\n[IMPOR CSV KATALOG] Berhasil: " + hasil.getBerhasil()
                    + ", galat baris: " + hasil.getGalat());
        } catch (IOException e) {
            System.out.println("[PERINGATAN] Gagal membaca CSV (" + e.getMessage() + "), memakai katalog bawaan.");
            unitRT.tambahKategori(new SampahAnorganik("SMP01", "Botol Plastik PET Bening", 4500, "Grade A"));
            unitRT.tambahKategori(new SampahAnorganik("SMP02", "Kardus Bekas / Box Cokelat", 2500, "Grade A"));
            unitRT.tambahKategori(new SampahAnorganik("SMP03", "Kaleng Minuman Aluminium", 12000, "Grade A"));
            unitRT.tambahKategori(new MinyakJelantah("SMP04", 6500, 1.2));
        }
    }

    private static void demoSetoranDanGalat(BankSampahService service, BankSampahCLI cli,
                                            BankSampahUnit unitRT, PetugasBankSampah petugas,
                                            NasabahWarga siti, NasabahWarga ahmad) {
        System.out.println("\n>>> [DEMO FITUR 1] PENIMBANGAN, SETORAN & PENANGANAN GALAT");
        try {
            KategoriSampah botol = unitRT.ambilKategori("SMP01");
            KategoriSampah kardus = unitRT.ambilKategori("SMP02");
            KategoriSampah aluminium = unitRT.ambilKategori("SMP03");
            KategoriSampah jelantah = unitRT.ambilKategori("SMP04");

            TransaksiSetoran tx1 = new TransaksiSetoran("TRX-2026-001", siti, LocalDate.now());
            tx1.tambahItem(botol, 2.0, "Setoran awal");
            tx1.tambahItem(botol, 2.5, "Tambahan botol bersih");
            tx1.tambahItem(kardus, 6.0, "Kardus lipat rapi");
            service.prosesSetoran(tx1, new RewardTabunganRupiah(), petugas);
            cli.cetakSlipSetoran(tx1);

            TransaksiSetoran tx2 = new TransaksiSetoran("TRX-2026-002", ahmad, LocalDate.now());
            tx2.tambahItem(aluminium, 2.0, "Kaleng aluminium bersih");
            tx2.tambahItem(jelantah, 4.0, "Minyak jelantah saring");
            service.prosesSetoran(tx2, new RewardPoinSembako(), petugas);
            cli.cetakSlipSetoran(tx2);
        } catch (TransaksiTidakValidException | DataTidakDitemukanException e) {
            System.out.println("Ditangani: " + e.getMessage());
        }

        // Demo setoran kosong -> ditangkap oleh TransaksiTidakValidException
        try {
            TransaksiSetoran txKosong = new TransaksiSetoran("TRX-2026-ERR", siti, LocalDate.now());
            service.prosesSetoran(txKosong, new RewardTabunganRupiah(), petugas);
        } catch (TransaksiTidakValidException e) {
            System.out.println("Ditangani (Setoran Kosong): " + e.getMessage());
        }
    }

    private static void demoPencairanDanDataHilang(BankSampahService service, NasabahWarga siti) {
        System.out.println("\n>>> [DEMO FITUR 2] PENCAIRAN SALDO KAS & DATA TIDAK DITEMUKAN");
        try {
            service.cairkanSaldoNasabah(siti, 20000.0);
            System.out.printf("[BERHASIL] Pencairan Rp 20.000 disetujui. Sisa saldo: Rp %,.2f\n",
                    siti.getSaldoTabungan());
        } catch (SaldoTidakCukupException e) {
            System.out.println("Ditangani: " + e.getMessage());
        }

        try {
            service.cairkanSaldoNasabah(siti, 50000.0);
        } catch (SaldoTidakCukupException e) {
            System.out.printf("Ditangani (Saldo Kurang): %s | Kekurangan: Rp %,.0f\n",
                    e.getMessage(), e.getKekurangan());
        }

        try {
            service.ambilTransaksi("TRX-999");
        } catch (DataTidakDitemukanException e) {
            System.out.println("Ditangani (Data Hilang): " + e.getMessage());
        }
    }

    private static void cetakLaporanAkhir(BankSampahCLI cli, List<NasabahWarga> nasabah,
                                          List<PetugasBankSampah> petugas) {
        cli.tampilkanRekapitulasi();
        cli.cetakDaftarPengguna(nasabah);
        cli.cetakDaftarPengguna(petugas);
    }

    private static void cekModeInteraktif(String[] args, BankSampahCLI cli,
                                          List<NasabahWarga> daftarNasabah) {
        for (String arg : args) {
            if ("--interactive".equalsIgnoreCase(arg) || "-i".equalsIgnoreCase(arg)) {
                Scanner scanner = new Scanner(System.in);
                cli.jalankanMenuInteraktif(scanner, daftarNasabah);
                scanner.close();
                return;
            }
        }
        System.out.println("\n[PETUNJUK] Jalankan dengan argumen '--interactive' untuk menu loket interaktif.");
    }
}
