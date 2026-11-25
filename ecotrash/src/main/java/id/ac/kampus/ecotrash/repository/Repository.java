package id.ac.kampus.ecotrash.repository;

import id.ac.kampus.ecotrash.exception.DataTidakDitemukanException;

import java.util.List;
import java.util.Optional;

/**
 * Kontrak generik penyimpanan entitas (Generics & DIP).
 * Menggantikan repository spesifik per entitas untuk menghilangkan duplikasi kode.
 */
public interface Repository<T, ID> {

    void simpan(T entitas);

    Optional<T> cari(ID id);

    T ambil(ID id) throws DataTidakDitemukanException;

    List<T> semua();
}
