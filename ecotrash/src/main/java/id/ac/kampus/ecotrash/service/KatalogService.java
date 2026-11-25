package id.ac.kampus.ecotrash.service;

import id.ac.kampus.ecotrash.model.KategoriSampah;
import id.ac.kampus.ecotrash.model.MinyakJelantah;
import id.ac.kampus.ecotrash.model.SampahAnorganik;
import id.ac.kampus.ecotrash.repository.Repository;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Layanan impor katalog komoditas sampah dari berkas CSV.
 * Menggunakan try-with-resources dan penanganan galat dua tingkat:
 * baris rusak dicatat di daftar galat tanpa menggagalkan impor keseluruhan,
 * sedangkan kegagalan baca berkas (IOException) diteruskan ke pemanggil.
 */
public class KatalogService {

    public static class HasilImpor {
        private final int berhasil;
        private final List<String> galat;

        public HasilImpor(int berhasil, List<String> galat) {
            this.berhasil = berhasil;
            this.galat = Collections.unmodifiableList(new ArrayList<>(galat));
        }

        public int getBerhasil() {
            return berhasil;
        }

        public List<String> getGalat() {
            return galat;
        }
    }

    private final Repository<KategoriSampah, String> kategoriRepo;

    public KatalogService(Repository<KategoriSampah, String> kategoriRepo) {
        if (kategoriRepo == null) {
            throw new IllegalArgumentException("kategoriRepo tidak boleh null");
        }
        this.kategoriRepo = kategoriRepo;
    }

    public HasilImpor imporCsv(Path berkas) throws IOException {
        if (berkas == null) {
            throw new IllegalArgumentException("Path berkas tidak boleh null");
        }
        int berhasil = 0;
        List<String> galat = new ArrayList<>();

        try (BufferedReader in = Files.newBufferedReader(berkas)) {
            String baris;
            int no = 0;
            while ((baris = in.readLine()) != null) {
                no++;
                if (baris.isBlank() || baris.trim().startsWith("#")) {
                    continue;
                }
                try {
                    KategoriSampah kategori = parseBaris(baris);
                    kategoriRepo.simpan(kategori);
                    berhasil++;
                } catch (IllegalArgumentException e) {
                    galat.add("baris " + no + ": " + e.getMessage());
                }
            }
        }
        return new HasilImpor(berhasil, galat);
    }

    private KategoriSampah parseBaris(String baris) {
        String[] kolom = baris.split(";", -1);
        if (kolom.length != 5) {
            throw new IllegalArgumentException("harus 5 kolom, ada " + kolom.length);
        }
        String kode = kolom[0].trim();
        String jenis = kolom[1].trim().toUpperCase();
        String nama = kolom[2].trim();
        double harga;
        try {
            harga = Double.parseDouble(kolom[3].trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("harga '" + kolom[3].trim() + "' bukan angka", e);
        }
        String atributKhusus = kolom[4].trim();

        if ("ANORGANIK".equals(jenis)) {
            return new SampahAnorganik(kode, nama, harga, atributKhusus);
        }
        if ("JELANTAH".equals(jenis)) {
            double kadarAsam;
            try {
                kadarAsam = Double.parseDouble(atributKhusus);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("kadar asam '" + atributKhusus + "' bukan angka", e);
            }
            return new MinyakJelantah(kode, harga, kadarAsam);
        }
        throw new IllegalArgumentException("jenis sampah '" + jenis + "' tidak dikenal");
    }

    public Repository<KategoriSampah, String> getKategoriRepo() {
        return kategoriRepo;
    }
}
