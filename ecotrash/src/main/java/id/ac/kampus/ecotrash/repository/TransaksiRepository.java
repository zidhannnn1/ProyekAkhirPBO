package id.ac.kampus.ecotrash.repository;

import id.ac.kampus.ecotrash.model.TransaksiSetoran;
import java.util.List;

/**
 * Interface abstraksi untuk operasi penyimpanan dan pengambilan data transaksi setoran.
 * Menerapkan Dependency Inversion Principle (DIP).
 */
public interface TransaksiRepository {
    void simpan(TransaksiSetoran transaksi);
    TransaksiSetoran cariBerdasarkanKode(String kode);
    List<TransaksiSetoran> cariSemua();
    List<TransaksiSetoran> cariBerdasarkanRekeningNasabah(String nomorRekening);
}

