package id.ac.kampus.ecotrash.repository;

import id.ac.kampus.ecotrash.model.TransaksiSetoran;

import java.util.List;

/**
 * Abstraksi penyimpanan data transaksi setoran (DIP).
 * Service layer bergantung pada interface ini, bukan implementasi konkret.
 */
public interface TransaksiRepository {
    void simpan(TransaksiSetoran transaksi);
    TransaksiSetoran cariBerdasarkanKode(String kode);
    List<TransaksiSetoran> cariSemua();
    List<TransaksiSetoran> cariBerdasarkanRekeningNasabah(String nomorRekening);
}
