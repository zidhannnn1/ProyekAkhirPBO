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

/**
 * Main Class Assembler (Composition Root).
 * Menghubungkan seluruh dependensi objek melalui Dependency Injection.
 */
public class App {
    public static void main(String[] args) {
        // 1. Inisialisasi Unit Bank Sampah (Agregasi)
        BankSampahUnit unitRT = new BankSampahUnit("Bank Sampah Berkah Mandiri", "RT 04 / RW 02 Kelurahan Sukamaju");

        // 2. Registrasi Master Kategori Sampah (Agregasi)
        SampahAnorganik botolPlastik = new SampahAnorganik("SMP01", "Botol Plastik PET Bening", 4500, "Grade A");
        SampahAnorganik kardus = new SampahAnorganik("SMP02", "Kardus Bekas / Box Cokelat", 2500, "Grade A");
        SampahAnorganik kalengAluminium = new SampahAnorganik("SMP03", "Kaleng Minuman Aluminium", 12000, "Grade A");
        MinyakJelantah jelantah = new MinyakJelantah("SMP04", 6500, 1.2);

        unitRT.tambahKategori(botolPlastik);
        unitRT.tambahKategori(kardus);
        unitRT.tambahKategori(kalengAluminium);
        unitRT.tambahKategori(jelantah);

        // 3. Registrasi Petugas Operasional (Agregasi)
        PetugasBankSampah petugasBudi = new PetugasBankSampah("PTG01", "Budi Santoso", "081234567890", "04", "NIP-RT04-01", "Bendahara Penimbangan");
        unitRT.daftarkanPetugas(petugasBudi);

        // 4. Inisialisasi Data Nasabah Warga
        NasabahWarga nasabahSiti = new NasabahWarga("NSB01", "Ibu Siti Aminah", "081298765432", "04", "REK-04-012");
        NasabahWarga nasabahAhmad = new NasabahWarga("NSB02", "Pak Ahmad Fauzi", "081311223344", "04", "REK-04-018");

        // 5. Inisialisasi Repository dan Service (Dependency Inversion)
        TransaksiRepository repository = new MemoryTransaksiRepository();
        NotifikasiService notifikasi = (telepon, pesan) -> System.out.println("[NOTIFIKASI SMS/WA ke " + telepon + "]: " + pesan);
        BankSampahService service = new BankSampahService(repository, notifikasi);

        // 6. Jalankan CLI Presentation
        BankSampahCLI cli = new BankSampahCLI(service, unitRT);
        cli.cetakBanner();
        cli.tampilkanKatalog();

        // 7. Simulasi Transaksi 1: Nasabah Ibu Siti menyetor botol & kardus (Skema Tabungan Rupiah)
        System.out.println("\n>>> SIMULASI TRANSAKSI 1: Penimbangan Sampah Ibu Siti");
        TransaksiSetoran tx1 = new TransaksiSetoran("TRX-2026-001", nasabahSiti, LocalDate.now());
        tx1.tambahItem(botolPlastik, 4.5, "Botol bersih tanpa label"); // 4.5 kg @ 4500 = Rp 20.250
        tx1.tambahItem(kardus, 6.0, "Kardus lipat rapi");             // 6.0 kg @ 2500 = Rp 15.000
        
        SkemaReward rewardUang = new RewardTabunganRupiah();
        service.prosesSetoran(tx1, rewardUang);
        System.out.printf("Total Nilai Transaksi: Rp %,.2f\n", tx1.hitungTotalNilai());
        System.out.printf("Saldo Tabungan Kas Ibu Siti Saat Ini: Rp %,.2f\n", nasabahSiti.getSaldoTabungan());

        // 8. Simulasi Transaksi 2: Nasabah Pak Ahmad menyetor aluminium & jelantah (Skema Poin Sembako)
        System.out.println("\n>>> SIMULASI TRANSAKSI 2: Penimbangan Sampah Pak Ahmad");
        TransaksiSetoran tx2 = new TransaksiSetoran("TRX-2026-002", nasabahAhmad, LocalDate.now());
        tx2.tambahItem(kalengAluminium, 2.0, "Kaleng minuman bersih"); // 2.0 kg @ 12000 = Rp 24.000
        tx2.tambahItem(jelantah, 4.0, "Jelantah disaring");            // 4.0 liter @ 6500 = Rp 26.000
        
        SkemaReward rewardSembako = new RewardPoinSembako();
        service.prosesSetoran(tx2, rewardSembako);
        System.out.printf("Total Nilai Transaksi: Rp %,.2f\n", tx2.hitungTotalNilai());
        System.out.printf("Poin Belanja Sembako Pak Ahmad Saat Ini: %d Poin\n", nasabahAhmad.getPoinReward());

        // 9. Cetak Rekapitulasi Akhir
        cli.tampilkanRekapitulasi();

        System.out.println("\n[INFO] Arsitektur kode Java PBO EcoTrash v2 berhasil diinisialisasi secara utuh.");
    }
}

