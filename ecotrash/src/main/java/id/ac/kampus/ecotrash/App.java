package id.ac.kampus.ecotrash;

import id.ac.kampus.ecotrash.model.*;
import id.ac.kampus.ecotrash.repository.MemoryTransaksiRepository;
import id.ac.kampus.ecotrash.repository.TransaksiRepository;
import id.ac.kampus.ecotrash.reward.RewardPoinSembako;
import id.ac.kampus.ecotrash.reward.RewardTabunganRupiah;
import id.ac.kampus.ecotrash.reward.SkemaReward;
import id.ac.kampus.ecotrash.service.BankSampahService;
import id.ac.kampus.ecotrash.service.NotifikasiService;
import id.ac.kampus.ecotrash.ui.BankSampahCLI;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * Main Class Assembler (Composition Root).
 * Menghubungkan seluruh dependensi objek melalui Dependency Injection (DIP).
 * Mengimplementasikan 2 fitur utama berjalan (Fitur 1: Setoran Sampah,
 * Fitur 2: Pencairan Kas) sesuai rubrik penilaian Pertemuan 11.
 */
public class App {

    public static void main(String[] args) {
        // Inisialisasi Unit Bank Sampah RT (Agregasi)
        BankSampahUnit unitRT = new BankSampahUnit(
                "Bank Sampah Berkah Mandiri",
                "RT 04 / RW 02 Kelurahan Sukamaju"
        );

        // Registrasi Master Kategori Komoditas Sampah (Inheritance dari KategoriSampah)
        SampahAnorganik botolPlastik = new SampahAnorganik("SMP01", "Botol Plastik PET Bening", 4500, "Grade A");
        SampahAnorganik kardus = new SampahAnorganik("SMP02", "Kardus Bekas / Box Cokelat", 2500, "Grade A");
        SampahAnorganik kalengAluminium = new SampahAnorganik("SMP03", "Kaleng Minuman Aluminium", 12000, "Grade A");
        MinyakJelantah jelantah = new MinyakJelantah("SMP04", 6500, 1.2);

        unitRT.tambahKategori(botolPlastik);
        unitRT.tambahKategori(kardus);
        unitRT.tambahKategori(kalengAluminium);
        unitRT.tambahKategori(jelantah);

        // Registrasi Petugas Operasional Kasir
        PetugasBankSampah petugasBudi = new PetugasBankSampah(
                "PTG01", "Budi Santoso", "081234567890", "04", "NIP-RT04-01", "Petugas Loket"
        );
        unitRT.daftarkanPetugas(petugasBudi);

        // Inisialisasi Data Nasabah Warga RT
        NasabahWarga nasabahSiti = new NasabahWarga("NSB01", "Ibu Siti Aminah", "081298765432", "04", "REK-04-012");
        NasabahWarga nasabahAhmad = new NasabahWarga("NSB02", "Pak Ahmad Fauzi", "081311223344", "04", "REK-04-018");
        List<NasabahWarga> daftarNasabah = Arrays.asList(nasabahSiti, nasabahAhmad);

        // Inisialisasi Dependency Inversion (Repository & Notifikasi)
        TransaksiRepository repository = new MemoryTransaksiRepository();
        NotifikasiService notifikasi = (telepon, pesan) ->
                System.out.println("[NOTIFIKASI SMS/WA ke " + telepon + "]: " + pesan);

        BankSampahService service = new BankSampahService(repository, notifikasi);
        BankSampahCLI cli = new BankSampahCLI(service, unitRT);

        cli.cetakBanner();
        cli.tampilkanKatalog();

        // SIMULASI FITUR 1: Penimbangan & Setoran Sampah Polimorfis
        System.out.println("\n>>> [DEMO FITUR 1] PENIMBANGAN & SETORAN SAMPAH");

        // Transaksi 1: Nasabah Ibu Siti menyetor botol & kardus (Skema Tabungan Rupiah)
        TransaksiSetoran tx1 = new TransaksiSetoran("TRX-2026-001", nasabahSiti, LocalDate.now());
        tx1.tambahItem(botolPlastik, 4.5, "Botol bersih tanpa label"); // 4.5 kg * 4500 = 20.250
        tx1.tambahItem(kardus, 6.0, "Kardus lipat rapi");             // 6.0 kg * 2500 = 15.000

        SkemaReward rewardRupiah = new RewardTabunganRupiah();
        service.prosesSetoran(tx1, rewardRupiah, petugasBudi);
        cli.cetakSlipSetoran(tx1);
        System.out.printf("Saldo Tabungan Kas Ibu Siti: Rp %,.2f\n", nasabahSiti.getSaldoTabungan());

        // Transaksi 2: Nasabah Pak Ahmad menyetor aluminium & jelantah (Skema Poin Sembako)
        TransaksiSetoran tx2 = new TransaksiSetoran("TRX-2026-002", nasabahAhmad, LocalDate.now());
        tx2.tambahItem(kalengAluminium, 2.0, "Kaleng aluminium bersih"); // 2.0 kg * 12000 = 24.000
        tx2.tambahItem(jelantah, 4.0, "Minyak jelantah saring");         // 4.0 liter * 6500 = 26.000

        SkemaReward rewardSembako = new RewardPoinSembako();
        service.prosesSetoran(tx2, rewardSembako, petugasBudi);
        cli.cetakSlipSetoran(tx2);
        System.out.printf("Poin Belanja Sembako Pak Ahmad: %d Poin\n", nasabahAhmad.getPoinReward());

        // SIMULASI FITUR 2: Pencairan Saldo Kas Tunai Nasabah
        System.out.println("\n>>> [DEMO FITUR 2] PENCAIRAN SALDO KAS TUNAI NASABAH");

        // Skenario 2A (Berhasil): Ibu Siti mencairkan Rp 20.000 dari total saldo Rp 35.250
        System.out.println("\n[Skenario 2A: Pencairan Berhasil]");
        System.out.printf("Saldo awal Ibu Siti: Rp %,.2f\n", nasabahSiti.getSaldoTabungan());
        System.out.println("Memproses penarikan kas tunai sebesar Rp 20.000...");
        boolean statusCair1 = service.cairkanSaldoNasabah(nasabahSiti, 20000.0);
        System.out.println("Status Pencairan : " + (statusCair1 ? "BERHASIL (Uang Tunai Diserahkan)" : "GAGAL"));
        System.out.printf("Sisa Saldo Kas   : Rp %,.2f\n", nasabahSiti.getSaldoTabungan());

        // Skenario 2B (Gagal): Ibu Siti mencoba mencairkan Rp 50.000 (saldo hanya tersisa Rp 15.250)
        System.out.println("\n[Skenario 2B: Pencairan Ditolak (Saldo Kurang)]");
        System.out.printf("Saldo saat ini: Rp %,.2f\n", nasabahSiti.getSaldoTabungan());
        System.out.println("Mencoba penarikan kas tunai sebesar Rp 50.000...");
        boolean statusCair2 = service.cairkanSaldoNasabah(nasabahSiti, 50000.0);
        System.out.println("Status Pencairan : " + (statusCair2 ? "BERHASIL" : "GAGAL DITOLAK (Saldo Tabungan Tidak Mencukupi)"));
        System.out.printf("Saldo Kas Tetap  : Rp %,.2f\n", nasabahSiti.getSaldoTabungan());

        // Rekapitulasi Akhir
        cli.tampilkanRekapitulasi();

        // Opsi Menu Interaktif jika pengguna memanggil dengan argumen --interactive
        boolean interactive = false;
        for (String arg : args) {
            if ("--interactive".equalsIgnoreCase(arg) || "-i".equalsIgnoreCase(arg)) {
                interactive = true;
                break;
            }
        }
        if (interactive) {
            Scanner scanner = new Scanner(System.in);
            cli.jalankanMenuInteraktif(scanner, daftarNasabah);
            scanner.close();
        } else {
            System.out.println("[PETUNJUK] Jalankan dengan argumen '--interactive' untuk membuka menu loket interaktif.");
        }
    }
}
