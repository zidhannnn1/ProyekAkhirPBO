package id.ac.kampus.ecotrash.repository;

import id.ac.kampus.ecotrash.model.TransaksiSetoran;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Implementasi in-memory dari TransaksiRepository.
 * Aman untuk pengujian unit dan eksekusi thread-safe.
 */
public class MemoryTransaksiRepository implements TransaksiRepository {
    private final List<TransaksiSetoran> storage = new CopyOnWriteArrayList<>();

    @Override
    public void simpan(TransaksiSetoran transaksi) {
        if (transaksi != null) {
            storage.add(transaksi);
        }
    }

    @Override
    public TransaksiSetoran cariBerdasarkanKode(String kode) {
        if (kode == null) return null;
        for (TransaksiSetoran t : storage) {
            if (t.getKodeTransaksi().equalsIgnoreCase(kode)) {
                return t;
            }
        }
        return null;
    }

    @Override
    public List<TransaksiSetoran> cariSemua() {
        return new ArrayList<>(storage);
    }

    @Override
    public List<TransaksiSetoran> cariBerdasarkanRekeningNasabah(String nomorRekening) {
        List<TransaksiSetoran> hasil = new ArrayList<>();
        if (nomorRekening == null) return hasil;
        for (TransaksiSetoran t : storage) {
            if (t.getNasabah().getNomorRekening().equalsIgnoreCase(nomorRekening)) {
                hasil.add(t);
            }
        }
        return hasil;
    }
}

