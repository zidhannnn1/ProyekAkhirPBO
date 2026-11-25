package id.ac.kampus.ecotrash.repository;

import id.ac.kampus.ecotrash.exception.DataTidakDitemukanException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * Implementasi generik Repository berbasis LinkedHashMap.
 * Mempertahankan urutan masuk (insertion order) sekaligus mendukung pencarian O(1) lewat ID.
 */
public class InMemoryRepository<T, ID> implements Repository<T, ID> {

    private final Map<ID, T> data = new LinkedHashMap<>();
    private final String namaEntitas;
    private final Function<T, ID> pembuatKunci;

    public InMemoryRepository(String namaEntitas, Function<T, ID> pembuatKunci) {
        if (namaEntitas == null || namaEntitas.isBlank()) {
            throw new IllegalArgumentException("namaEntitas tidak boleh kosong");
        }
        if (pembuatKunci == null) {
            throw new IllegalArgumentException("pembuatKunci tidak boleh null");
        }
        this.namaEntitas = namaEntitas;
        this.pembuatKunci = pembuatKunci;
    }

    @Override
    public void simpan(T entitas) {
        if (entitas == null) {
            throw new IllegalArgumentException("Entitas tidak boleh null");
        }
        ID kunci = pembuatKunci.apply(entitas);
        if (kunci == null) {
            throw new IllegalArgumentException("Kunci entitas tidak boleh null");
        }
        data.put(kunci, entitas);
    }

    @Override
    public Optional<T> cari(ID id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public T ambil(ID id) throws DataTidakDitemukanException {
        return cari(id).orElseThrow(
                () -> new DataTidakDitemukanException(namaEntitas, String.valueOf(id)));
    }

    @Override
    public List<T> semua() {
        return Collections.unmodifiableList(new ArrayList<>(data.values()));
    }
}
