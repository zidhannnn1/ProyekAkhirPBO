package id.ac.kampus.ecotrash.repository;

import id.ac.kampus.ecotrash.model.TransaksiSetoran;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Implementasi in-memory menggunakan CopyOnWriteArrayList
 * agar aman diakses dari beberapa thread tanpa sinkronisasi manual.
 */
public class MemoryTransaksiRepository implements TransaksiRepository {

    private final CopyOnWriteArrayList<TransaksiSetoran> dataTransaksi = new CopyOnWriteArrayList<>();

    @Override
    public void simpan(TransaksiSetoran transaksi) {
        if (transaksi == null) {
            throw new IllegalArgumentException("Transaksi tidak boleh null.");
        }
        dataTransaksi.add(transaksi);
    }

    @Override
    public TransaksiSetoran cariBerdasarkanKode(String kode) {
        if (kode == null) {
            return null;
        }
        for (TransaksiSetoran tx : dataTransaksi) {
            if (tx.getKodeTransaksi().equals(kode)) {
                return tx;
            }
        }
        return null;
    }

    @Override
    public List<TransaksiSetoran> cariSemua() {
        return new ArrayList<>(dataTransaksi);
    }

    @Override
    public List<TransaksiSetoran> cariBerdasarkanRekeningNasabah(String nomorRekening) {
        List<TransaksiSetoran> hasil = new ArrayList<>();
        if (nomorRekening == null) {
            return hasil;
        }
        for (TransaksiSetoran tx : dataTransaksi) {
            if (tx.getNasabah().getNomorRekening().equals(nomorRekening)) {
                hasil.add(tx);
            }
        }
        return hasil;
    }
}
